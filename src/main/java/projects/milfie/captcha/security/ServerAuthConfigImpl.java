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

import java.util.Map;
import java.util.logging.Logger;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.config.ServerAuthConfig;
import javax.security.auth.message.config.ServerAuthContext;

public final class ServerAuthConfigImpl
   implements ServerAuthConfig
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public ServerAuthConfigImpl (final String layer,
                                final String appContext,
                                final CallbackHandler handler,
                                final ServerAuthModuleFactory moduleFactory,
                                final Map<String, String> providerProperties)
   {
      if (layer == null) {
         throw new IllegalArgumentException ("Given layer is null.");
      }
      if (appContext == null) {
         throw new IllegalArgumentException ("Given appContext is null.");
      }
      if (handler == null) {
         throw new IllegalArgumentException ("Given handler is null.");
      }
      if (moduleFactory == null) {
         throw new IllegalArgumentException ("Given moduleFactory is null.");
      }
      if (providerProperties == null) {
         throw
            new IllegalArgumentException
               ("Given providerProperties is null.");
      }

      this.layer = layer;
      this.appContext = appContext;
      this.handler = handler;
      this.moduleFactory = moduleFactory;
      this.providerProperties = providerProperties;

      this.loadServerAuthContext ();
   }

   @Override
   public ServerAuthContext getAuthContext (final String authContextID,
                                            final Subject serviceSubject,
                                            @SuppressWarnings ("rawtypes")
                                            final Map properties)
   {
      return (appContext.equals (authContextID) ? serverAuthContext : null);
   }

   @Override
   public String getMessageLayer () {
      return layer;
   }

   @Override
   public String getAppContext () {
      return appContext;
   }

   @Override
   public String getAuthContextID (final MessageInfo messageInfo) {
      return appContext;
   }

   @Override
   public void refresh () {
      LOGGER.info ("Refresh of auth contexts is started.");
      loadServerAuthContext ();
   }

   @Override
   public boolean isProtected () {
      return false;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final String                  layer;
   private final String                  appContext;
   private final CallbackHandler         handler;
   private final ServerAuthModuleFactory moduleFactory;
   private final Map<String, String>     providerProperties;

   private ServerAuthContext serverAuthContext;

   private void loadServerAuthContext () {
      try {
         serverAuthContext =
            new ServerAuthContextImpl (handler, moduleFactory);
      }
      catch (final AuthException cause) {
         throw new IllegalStateException (cause);
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final Logger LOGGER =
      Logger.getLogger (ServerAuthConfigImpl.class.getName ());
}
