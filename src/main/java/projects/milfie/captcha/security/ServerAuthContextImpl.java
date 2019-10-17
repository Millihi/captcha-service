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
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.module.ServerAuthModule;

public final class ServerAuthContextImpl
   implements ServerAuthContext
{

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public ServerAuthContextImpl (final CallbackHandler handler,
                                 final ServerAuthModule serverAuthModule)
      throws AuthException
   {
      if (serverAuthModule == null) {
         throw new IllegalArgumentException ("SAM is null.");
      }

      this.serverAuthModule = serverAuthModule;

      this.serverAuthModule.initialize
         (DEFAULT_POLICY,
          DEFAULT_POLICY,
          handler,
          Collections.<String, String>emptyMap ());
   }

   @Override
   public AuthStatus validateRequest (final MessageInfo messageInfo,
                                      final Subject clientSubject,
                                      final Subject serviceSubject)
      throws AuthException
   {
      return serverAuthModule.validateRequest
         (messageInfo, clientSubject, serviceSubject);
   }

   @Override
   public AuthStatus secureResponse (final MessageInfo messageInfo,
                                     final Subject serviceSubject)
      throws AuthException
   {
      return serverAuthModule.secureResponse (messageInfo, serviceSubject);
   }

   @Override
   public void cleanSubject (final MessageInfo messageInfo,
                             final Subject subject)
      throws AuthException
   {
      serverAuthModule.cleanSubject (messageInfo, subject);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final ServerAuthModule serverAuthModule;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final MessagePolicy DEFAULT_POLICY =
      new MessagePolicy (new MessagePolicy.TargetPolicy[] {}, true);
}
