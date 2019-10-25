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
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;

@Singleton
public final class DummyAppServerAuthModule
   extends AbstractHttpServletServerAuthModule
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   public AuthStatus secureResponse (final MessageInfo messageInfo,
                                     final Subject serviceSubject)
      throws AuthException
   {
      throw new UnsupportedOperationException ();
   }

   @Override
   public void cleanSubject (final MessageInfo messageInfo,
                             final Subject subject)
      throws AuthException
   {
      throw new UnsupportedOperationException ();
   }
}
