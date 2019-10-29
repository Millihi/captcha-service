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
import java.security.Principal;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

abstract class HttpServletServerAuthModule
   implements ServerAuthModule
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   @SuppressWarnings ("rawtypes")
   public final Class[] getSupportedMessageTypes () {
      return SUPPORTED_MESSAGE_TYPES;
   }

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
         result = tryOnAuthRequired
            (messageInfo, clientSubject, serviceSubject);

         if (result == null) {
            result = sendUnauthorized
               (messageInfo, clientSubject, serviceSubject);
         }
      }

      return result;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   protected MessagePolicy   requestPolicy;
   protected MessagePolicy   responsePolicy;
   protected CallbackHandler handler;

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
      if (!isAuthenticationRequired (messageInfo)) {
         return handleNotAuthorized (clientSubject);
      }

      if (!requestPolicy.isMandatory ()) {
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
      catch (final IOException | UnsupportedCallbackException cause) {
         throw (AuthException) new AuthException ().initCause (cause);
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String JASPIC_KEY_IS_MANDATORY     =
      "javax.security.auth.message.MessagePolicy.isMandatory";
   private static final String JASPIC_KEY_REGISTER_SESSION =
      "javax.servlet.http.registerSession";

   @SuppressWarnings ("rawtypes")
   private static final Class<?>[] SUPPORTED_MESSAGE_TYPES = new Class[]{
      HttpServletRequest.class,
      HttpServletResponse.class
   };

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

   @SuppressWarnings ("unchecked")
   protected static void clearRegisterSession (final MessageInfo info) {
      info.getMap ().remove (JASPIC_KEY_REGISTER_SESSION);
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
