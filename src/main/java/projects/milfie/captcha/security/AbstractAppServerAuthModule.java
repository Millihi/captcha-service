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

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.servlet.http.HttpServletRequest;

abstract class AbstractAppServerAuthModule
   implements AppServerAuthModule
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   public void initialize
      (final MessagePolicy requestPolicy,
       final MessagePolicy responsePolicy,
       final CallbackHandler handler,
       @SuppressWarnings ("rawtypes")
       final Map options)
   {
      this.requestPolicy = requestPolicy;
      this.responsePolicy = responsePolicy;
      this.handler = handler;
   }

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
   protected MessagePolicy           requestPolicy;
   protected MessagePolicy           responsePolicy;
   protected CallbackHandler         handler;

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

   protected final AuthStatus tryOnAlreadyAuthorized
      (final MessageInfo messageInfo,
       final Subject clientSubject,
       final Subject serviceSubject)
      throws AuthException
   {
      final Principal principal = ((HttpServletRequest)
         messageInfo.getRequestMessage ()).getUserPrincipal ();

      if (principal == null) {
         return null;
      }

      return handleAlreadyAuthorized (clientSubject, principal);
   }

   protected final AuthStatus tryOnAuthRequired
      (final MessageInfo messageInfo,
       final Subject clientSubject,
       final Subject serviceSubject)
      throws AuthException
   {
      if (!requestPolicy.isMandatory ()) {
         return handleNotAuthorized (clientSubject);
      }

      if (!isAuthenticationRequired (messageInfo)) {
         return handleNotAuthorized (clientSubject);
      }

      return null;
   }

   protected final AuthStatus sendUnauthorized
      (final MessageInfo messageInfo,
       final Subject clientSubject,
       final Subject serviceSubject)
      throws AuthException
   {
      return handleNotAuthorized (clientSubject);
   }

   protected final AuthStatus handleAlreadyAuthorized
      (final Subject client,
       final Principal principal)
      throws AuthException
   {
      handleCallbacks (new CallerPrincipalCallback (client, principal));

      return AuthStatus.SUCCESS;
   }

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

   protected final AuthStatus handleNotAuthorized (final Subject client)
      throws AuthException
   {
      handleCallbacks (new CallerPrincipalCallback (client, (Principal) null));

      return AuthStatus.SUCCESS;
   }

   protected final void handleCallbacks (final Callback... callbacks)
      throws AuthException
   {
      try {
         handler.handle (callbacks);
      }
      catch (final IOException | UnsupportedCallbackException e) {
         throw (AuthException) new AuthException ().initCause (e);
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String JASPIC_KEY_IS_MANDATORY     =
      "javax.security.auth.message.MessagePolicy.isMandatory";
   private static final String JASPIC_KEY_REGISTER_SESSION =
      "javax.servlet.http.registerSession";

   protected static boolean isAuthenticationRequired (final MessageInfo info) {
      return
         Boolean.parseBoolean
            ((String) info.getMap ().get (JASPIC_KEY_IS_MANDATORY));
   }

   @SuppressWarnings ("unchecked")
   protected static void setRegisterSession (final MessageInfo info) {
      info.getMap ().put
         (JASPIC_KEY_REGISTER_SESSION, Boolean.TRUE.toString ());
   }

   protected static String getResourceURI (final HttpServletRequest request) {
      return
         request.getRequestURI ().substring
            (request.getContextPath ().length ());
   }

   protected static String getRelativeURL (final HttpServletRequest request) {
      final String requestURI = cutJsessionId (request.getRequestURI ());

      if (request.getQueryString () == null) {
         return requestURI;
      }
      return requestURI + '?' + request.getQueryString ();
   }

   protected static String cutJsessionId (final String uri) {
      if (uri != null) {
         final int endIdx = uri.indexOf (";jsessionid=");

         if (endIdx >= 0) {
            return uri.substring (0, endIdx);
         }
      }

      return uri;
   }
}
