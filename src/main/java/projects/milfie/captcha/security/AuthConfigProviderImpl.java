/*****************************************************************************
 * "THE CAKE-WARE LICENSE" (Revision 42):                                    *
 *                                                                           *
 *     Milfie <mail@milfie.uu.me> wrote this file. As long as you retain     *
 * this notice you can do whatever you want with this stuff. If we meet      *
 * some day, and you think this stuff is worth it, you must buy me a cake    *
 * in return.                                                                *
 *                                                                           *
 *     Milfie.                                                               *
 *****************************************************************************/

package projects.milfie.captcha.security;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.config.ClientAuthConfig;
import javax.security.auth.message.config.ServerAuthConfig;

public final class AuthConfigProviderImpl
   implements AuthConfigProvider
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public AuthConfigProviderImpl
      (final AuthModuleProvider moduleProvider)
   {
      if (moduleProvider == null) {
         throw new IllegalArgumentException ("Given moduleProvider is null.");
      }

      this.moduleProvider = moduleProvider;
      this.properties = Collections.emptyMap ();
      this.serverAuthConfig = null;
   }

   @Override
   public ClientAuthConfig getClientAuthConfig
      (final String layer,
       final String appContext,
       final CallbackHandler handler)
   {
      return null;
   }

   /**
    * Takes care about concurency.
    * {@inheritDoc}
    */
   @Override
   public ServerAuthConfig getServerAuthConfig
      (final String layer,
       final String appContext,
       final CallbackHandler handler)
      throws AuthException
   {
      ServerAuthConfig config = serverAuthConfig;

      if (config == null) {
         synchronized (this) {
            if (serverAuthConfig == null) {
               serverAuthConfig =
                  createServerAuthConfig (layer, appContext, handler);
            }
            config = serverAuthConfig;
         }
      }
      return config;
   }

   /**
    * Takes care about concurency.
    * {@inheritDoc}
    */
   @Override
   public synchronized void refresh () {
      LOGGER.info ("Refresh of auth configs is started.");

      moduleProvider.refresh ();

      final ServerAuthConfig config = serverAuthConfig;

      if (config != null) {
         config.refresh ();
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final AuthModuleProvider  moduleProvider;
   private final Map<String, String> properties;

   private volatile ServerAuthConfig serverAuthConfig;

   private ServerAuthConfig createServerAuthConfig
      (final String layer,
       final String appContext,
       final CallbackHandler handler)
      throws AuthException
   {
      return
         new ServerAuthConfigImpl
            (layer,
             appContext,
             handler == null ? createDefaultCallbackHandler () : handler,
             moduleProvider,
             properties);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final Logger LOGGER =
      Logger.getLogger (AuthConfigProviderImpl.class.getName ());

   private static final String CALLBACK_HANDLER_PROPERTY_NAME =
      "authconfigprovider.client.callbackhandler";

   //  TODO: Isn't "authconfigprovider.client.callbackhandler" JBoss specific?
   private static CallbackHandler createDefaultCallbackHandler ()
      throws AuthException
   {
      final String callBackClassName = System.getProperty
         (CALLBACK_HANDLER_PROPERTY_NAME);

      if (callBackClassName == null) {
         throw new AuthException
            ("No default handler set via system property " +
             CALLBACK_HANDLER_PROPERTY_NAME);
      }

      try {
         return (CallbackHandler) Thread
            .currentThread ()
            .getContextClassLoader ()
            .loadClass (callBackClassName)
            .newInstance ();
      }
      catch (final Throwable thrown) {
         throw (AuthException) new AuthException ().initCause (thrown);
      }
   }
}
