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
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.config.ServerAuthContext;
import javax.servlet.http.HttpServletRequest;

public final class ServerAuthContextImpl
   implements ServerAuthContext
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public ServerAuthContextImpl
      (final CallbackHandler handler,
       final AuthModuleProvider moduleProvider)
      throws AuthException
   {
      if (handler == null) {
         throw new IllegalArgumentException ("Given handler is null.");
      }
      if (moduleProvider == null) {
         throw new IllegalArgumentException ("Given moduleProvider is null.");
      }

      this.moduleProvider = moduleProvider;

      final Map<String, String> properties = Collections.emptyMap ();

      for (final AuthTypeSpec spec : AuthTypeSpec.values ()) {
         moduleProvider
            .getInstance (spec.getModuleClass ())
            .initialize (DEFAULT_POLICY, DEFAULT_POLICY, handler, properties);
      }
   }

   @Override
   public AuthStatus validateRequest (final MessageInfo messageInfo,
                                      final Subject clientSubject,
                                      final Subject serviceSubject)
      throws AuthException
   {
      moduleProvider.getReadLock ().lock ();
      try {
         return
            moduleProvider
               .getModuleInstance (getResourceURI (messageInfo))
               .validateRequest (messageInfo, clientSubject, serviceSubject);
      }
      finally {
         moduleProvider.getReadLock ().unlock ();
      }
   }

   @Override
   public AuthStatus secureResponse (final MessageInfo messageInfo,
                                     final Subject serviceSubject)
   {
      return AuthStatus.SEND_SUCCESS;
   }

   @Override
   public void cleanSubject (final MessageInfo messageInfo,
                             final Subject subject)
   {
      if (subject != null) {
         subject.getPrincipals ().clear ();
         subject.getPrivateCredentials ().clear ();
         subject.getPublicCredentials ().clear ();
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final AuthModuleProvider moduleProvider;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final MessagePolicy DEFAULT_POLICY =
      new MessagePolicy (new MessagePolicy.TargetPolicy[]{}, true);

   private static String getResourceURI (final MessageInfo info) {
      return
         getResourceURI
            ((HttpServletRequest) info.getRequestMessage ());
   }

   private static String getResourceURI (final HttpServletRequest request) {
      return
         request.getRequestURI ().substring
            (request.getContextPath ().length ());
   }
}
