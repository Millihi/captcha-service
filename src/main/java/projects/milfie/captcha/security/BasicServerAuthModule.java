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

import java.util.Base64;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.security.auth.Subject;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public final class BasicServerAuthModule
   extends AppServerAuthModule
{
   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @Inject
   private BasicServerAuthModuleConfig config;

   @Override
   protected AuthStatus tryOnMethod (final MessageInfo messageInfo,
                                     final Subject clientSubject,
                                     final Subject serviceSubject)
      throws AuthException
   {
      final String authHeader = ((HttpServletRequest)
         messageInfo.getRequestMessage ()).getHeader (AUTHORIZATION_HEADER);

      if (authHeader == null) {
         return null;
      }

      if (!authHeader.startsWith (BASIC_PREFIX)) {
         return handleNotAuthorized (clientSubject);
      }

      final String credentials = new String
         (Base64.getDecoder ().decode
            (authHeader.substring (BASIC_PREFIX.length ()).trim ()));

      final int colonPos = credentials.indexOf (':');

      if (colonPos <= 0 || colonPos >= credentials.length () - 1) {
         return handleNotAuthorized (clientSubject);
      }

      return
         handleAuthorize
            (messageInfo,
             clientSubject,
             credentials.substring (0, colonPos),
             credentials.substring (colonPos + 1),
             config.isStateful ());
   }

   @Override
   protected AuthStatus sendChallenge (final MessageInfo messageInfo,
                                       final Subject clientSubject,
                                       final Subject serviceSubject)
   {
      final HttpServletRequest request =
         (HttpServletRequest) messageInfo.getRequestMessage ();
      final HttpServletResponse response =
         (HttpServletResponse) messageInfo.getResponseMessage ();

      String realm = request.getServletContext ().getServletContextName ();

      if (realm == null || realm.isEmpty ()) {
         realm = request.getServerName () + request.getContextPath ();
      }

      response.setHeader
         (AUTHENTICATION_HEADER, BASIC_PREFIX + "realm=\"" + realm + "\"");
      response.setStatus
         (HttpServletResponse.SC_UNAUTHORIZED);

      return AuthStatus.SEND_CONTINUE;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String
      BASIC_PREFIX          = "Basic ",
      AUTHORIZATION_HEADER  = "Authorization",
      AUTHENTICATION_HEADER = "WWW-Authenticate";
}
