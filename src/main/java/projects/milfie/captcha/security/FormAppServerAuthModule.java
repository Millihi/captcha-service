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

import java.io.IOException;
import javax.inject.Singleton;
import javax.security.auth.Subject;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public final class FormAppServerAuthModule
   extends AbstractAppServerAuthModule
{
   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   protected AuthStatus tryOnMethod (final MessageInfo messageInfo,
                                     final Subject clientSubject,
                                     final Subject serviceSubject)
      throws AuthException
   {
      final HttpServletRequest request =
         (HttpServletRequest) messageInfo.getRequestMessage ();

      if (!POST.equalsIgnoreCase (request.getMethod ())) {
         return null;
      }

      final String requestURI = cutJsessionId (request.getRequestURI ());

      if (!requestURI.endsWith (config.getFormAuthAction ())) {
         return null;
      }

      request.setAttribute
         (Configuration.ATTR_KEY_IS_POST_AUTH, Boolean.toString (true));

      return
         handleAuthorize
            (messageInfo,
             clientSubject,
             request.getParameter (config.getFormAuthUsernameField ()),
             request.getParameter (config.getFormAuthPasswordField ()),
             config.isFormAuthStateful ());
   }

   @Override
   protected AuthStatus sendChallenge (final MessageInfo messageInfo,
                                       final Subject clientSubject,
                                       final Subject serviceSubject)
      throws AuthException
   {
      final HttpServletRequest request =
         (HttpServletRequest) messageInfo.getRequestMessage ();
      final HttpServletResponse response =
         (HttpServletResponse) messageInfo.getResponseMessage ();
      final String loginURI =
         request.getContextPath () + config.getFormAuthLoginPage ();

      request.getSession (true).setAttribute
         (Configuration.SESSION_KEY_REDIRECT_TO, getRelativeURL (request));

      try {
         response.sendRedirect (response.encodeRedirectURL (loginURI));
      }
      catch (final IOException e) {
         throw (AuthException) new AuthException ().initCause (e);
      }

      return AuthStatus.SEND_CONTINUE;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String POST = "POST";
}
