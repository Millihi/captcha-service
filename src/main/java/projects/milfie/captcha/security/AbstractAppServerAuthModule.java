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

import projects.milfie.captcha.domain.Client;
import projects.milfie.captcha.domain.Role;
import projects.milfie.captcha.service.AccountServiceLocalBean;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;

abstract class AbstractAppServerAuthModule
   extends AbstractHttpServletServerAuthModule
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   public AuthStatus validateRequest
      (final MessageInfo messageInfo,
       final Subject clientSubject,
       final Subject serviceSubject)
      throws AuthException
   {
      AuthStatus result;

      result = tryOnAlreadyAuthorized
         (messageInfo, clientSubject, serviceSubject);

      if (result == null) {
         result = tryOnMethod
            (messageInfo, clientSubject, serviceSubject);

         if (result == null) {
            result = tryOnAuthRequired
               (messageInfo, clientSubject, serviceSubject);

            if (result == null) {
               result = sendChallenge
                  (messageInfo, clientSubject, serviceSubject);

               if (result == null) {
                  result = sendUnauthorized
                     (messageInfo, clientSubject, serviceSubject);
               }
            }
         }
      }

      return result;
   }

   @Override
   public AuthStatus secureResponse
      (final MessageInfo info,
       final Subject service)
   {
      throw new UnsupportedOperationException ();
   }

   @Override
   public void cleanSubject
      (final MessageInfo info,
       final Subject subject)
   {
      throw new UnsupportedOperationException ();
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @EJB
   protected AccountServiceLocalBean accountService;
   @Inject
   protected PasswordManager         passwordManager;
   @Inject
   protected Configuration           config;

   protected abstract AuthStatus tryOnMethod
      (final MessageInfo messageInfo,
       final Subject clientSubject,
       final Subject serviceSubject)
      throws AuthException;

   protected abstract AuthStatus sendChallenge
      (final MessageInfo messageInfo,
       final Subject clientSubject,
       final Subject serviceSubject)
      throws AuthException;

   protected final AuthStatus handleAuthorize
      (final MessageInfo messageInfo,
       final Subject clientSubject,
       final String username,
       final String password,
       final boolean stateful)
      throws AuthException
   {
      if (username == null || username.isEmpty () ||
          password == null || password.isEmpty ())
      {
         return handleNotAuthorized (clientSubject);
      }

      final Client client = accountService.find (username);

      if (client == null ||
          !passwordManager.isBelongs
             (client.getPassword (), password.toCharArray ()))
      {
         return handleNotAuthorized (clientSubject);
      }

      handleCallbacks
         (new CallerPrincipalCallback (clientSubject, client.getName ()),
          new GroupPrincipalCallback
             (clientSubject,
              client.getRoles ().stream ()
                    .map (Role::toString).toArray (String[]::new)));

      if (stateful) {
         setRegisterSession (messageInfo);
      }

      return AuthStatus.SUCCESS;
   }
}
