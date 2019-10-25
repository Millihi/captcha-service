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

import javax.inject.Singleton;
import javax.security.auth.Subject;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public final class DummyAppServerAuthModule
   extends AbstractAppServerAuthModule
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   @SuppressWarnings ("rawtypes")
   public Class[] getSupportedMessageTypes () {
      return SUPPORTED_MESSAGE_TYPES;
   }

   @Override
   public boolean isEnabled () {
      return true;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   protected AuthStatus tryOnMethod (final MessageInfo messageInfo,
                                     final Subject clientSubject,
                                     final Subject serviceSubject)
   {
      return null;
   }

   @Override
   protected AuthStatus sendChallenge (final MessageInfo messageInfo,
                                       final Subject clientSubject,
                                       final Subject serviceSubject)
   {
      return null;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   @SuppressWarnings ("rawtypes")
   private static final Class<?>[] SUPPORTED_MESSAGE_TYPES = new Class[]{
      HttpServletRequest.class,
      HttpServletResponse.class
   };
}
