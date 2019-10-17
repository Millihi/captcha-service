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
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.config.ServerAuthConfig;
import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.module.ServerAuthModule;

public final class ServerAuthConfigImpl
   implements ServerAuthConfig
{

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public ServerAuthConfigImpl (final String layer,
                                final String appContext,
                                final CallbackHandler handler,
                                final Map<String, String> providerProperties,
                                final ServerAuthModule serverAuthModule)
   {
      this.layer = layer;
      this.appContext = appContext;
      this.handler = handler;
      this.serverAuthModule = serverAuthModule;
   }

   @Override
   public ServerAuthContext getAuthContext (final String authContextID,
                                            final Subject serviceSubject,
                                            @SuppressWarnings ("rawtypes")
                                            final Map properties)
      throws AuthException
   {
      return new ServerAuthContextImpl (handler, serverAuthModule);
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
      return;
   }

   @Override
   public boolean isProtected () {
      return false;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final String           layer;
   private final String           appContext;
   private final CallbackHandler  handler;
   private final ServerAuthModule serverAuthModule;
}
