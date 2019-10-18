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
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.inject.Singleton;
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
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public final class ServerAuthModuleImpl
   implements ServerAuthModule
{

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public ServerAuthModuleImpl () {
      this.config = new Configuration ();
      this.algorithm = new EnumMap<> (Algorithm.class);

      this.algorithm.put
         (Algorithm.CHECK_IF_ALREADY_AUTHORIZED,
          Action.TRY_ON_ALREADY_AUTHORIZED);
      this.algorithm.put
         (Algorithm.CHECK_IF_AUTH_REQUIRED,
          Action.TRY_ON_AUTH_REQUIRED);
   }

   @Override
   public void initialize (final MessagePolicy requestPolicy,
                           final MessagePolicy responsePolicy,
                           final CallbackHandler handler,
                           @SuppressWarnings ("rawtypes")
                           final Map options)
   {
      this.requestPolicy = requestPolicy;
      this.handler = handler;
   }

   @Override
   @SuppressWarnings ("rawtypes")
   public Class[] getSupportedMessageTypes () {
      return SUPPORTED_MESSAGE_TYPES;
   }

   @Override
   public AuthStatus validateRequest (final MessageInfo messageInfo,
                                      final Subject clientSubject,
                                      final Subject serviceSubject)
      throws AuthException
   {
      final Method method = Method.findEnabled (this, messageInfo);

      if (method == null) {
         algorithm.put
            (Algorithm.CHECK_IF_ATTEMPT_TO_AUTHORIZE, Action.DO_NOTHING);
         algorithm.put
            (Algorithm.TRY_SEND_AUTH_CHALLENGE, Action.DO_NOTHING);
      }
      else {
         algorithm.put
            (Algorithm.CHECK_IF_ATTEMPT_TO_AUTHORIZE, method.check);
         algorithm.put
            (Algorithm.TRY_SEND_AUTH_CHALLENGE, method.send);
      }

      for (final Algorithm step : Algorithm.values ()) {
         final AuthStatus result =
            algorithm.get (step).perform (this, messageInfo, clientSubject);

         if (result != null) {
            return result;
         }
      }

      return handleNotAuthorized (clientSubject);
   }

   @Override
   public AuthStatus secureResponse (final MessageInfo info,
                                     final Subject service)
   {
      return AuthStatus.SEND_SUCCESS;
   }

   @Override
   public void cleanSubject (final MessageInfo info,
                             final Subject subject)
   {
      if (subject == null) {
         return;
      }

      subject.getPrincipals ().clear ();
      subject.getPrivateCredentials ().clear ();
      subject.getPublicCredentials ().clear ();
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @EJB
   private AccountServiceLocalBean accountService;
   @Inject
   private PasswordManager         passwordManager;
   private MessagePolicy           requestPolicy;
   private CallbackHandler         handler;

   private final Configuration              config;
   private final EnumMap<Algorithm, Action> algorithm;

   private AuthStatus tryOnAlreadyAuthorized (
      final MessageInfo messageInfo,
      final Subject clientSubject)
      throws AuthException
   {
      final Principal principal = ((HttpServletRequest)
         messageInfo.getRequestMessage ()).getUserPrincipal ();

      if (principal == null) {
         return null;
      }

      return handleAlreadyAuthorized (clientSubject, principal);
   }

   private AuthStatus tryOnFormMethod (final MessageInfo messageInfo,
                                       final Subject clientSubject)
      throws AuthException
   {
      final HttpServletRequest request =
         (HttpServletRequest) messageInfo.getRequestMessage ();

      if (POST.equalsIgnoreCase (request.getMethod ()) &&
          request.getRequestURI ().endsWith (config.getFormAuthAction ()))
      {
         request.setAttribute
            (Configuration.ATTR_KEY_IS_POST_AUTH, Boolean.toString (true));

         return
            handleAuthorize
               (messageInfo,
                clientSubject,
                request.getParameter (config.getFormAuthUsernameField ()),
                request.getParameter (config.getFormAuthPasswordField ()));
      }

      return null;
   }

   private AuthStatus tryOnBasicMethod (final MessageInfo messageInfo,
                                        final Subject clientSubject)
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
             credentials.substring (colonPos + 1));
   }

   private AuthStatus tryOnAuthRequired (final MessageInfo messageInfo,
                                         final Subject clientSubject)
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

   private AuthStatus sendFormChallenge (final MessageInfo messageInfo)
      throws AuthException
   {
      final HttpServletRequest request =
         (HttpServletRequest) messageInfo.getRequestMessage ();
      final HttpServletResponse response =
         (HttpServletResponse) messageInfo.getResponseMessage ();

      final String loginURI =
         request.getContextPath () + config.getFormAuthLoginPage ();

      request.getSession ().setAttribute
         (Configuration.SESSION_KEY_REDIRECT_TO, getRelativeURL (request));

      try {
         response.sendRedirect (response.encodeRedirectURL (loginURI));
      }
      catch (final IOException e) {
         throw (AuthException) new AuthException ().initCause (e);
      }

      return AuthStatus.SEND_CONTINUE;
   }

   private AuthStatus sendBasicChallenge (final MessageInfo messageInfo) {
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

   private AuthStatus handleAlreadyAuthorized (final Subject client,
                                               final Principal principal)
      throws AuthException
   {
      handleCallbacks (new CallerPrincipalCallback (client, principal));

      return AuthStatus.SUCCESS;
   }

   private AuthStatus handleAuthorize (final MessageInfo messageInfo,
                                       final Subject clientSubject,
                                       final String username,
                                       final String password)
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

      setRegisterSession (messageInfo);

      return AuthStatus.SUCCESS;
   }

   private AuthStatus handleNotAuthorized (final Subject client)
      throws AuthException
   {
      handleCallbacks (new CallerPrincipalCallback (client, (Principal) null));

      return AuthStatus.SUCCESS;
   }

   private void handleCallbacks (final Callback... callbacks)
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

   private enum Algorithm {
      CHECK_IF_ALREADY_AUTHORIZED,
      CHECK_IF_ATTEMPT_TO_AUTHORIZE,
      CHECK_IF_AUTH_REQUIRED,
      TRY_SEND_AUTH_CHALLENGE
   }

   private enum Method {
      FORM (Action.TRY_ON_FORM_METHOD, Action.SEND_FORM_CHALLENGE)
         {
            @Override
            public boolean isEnabled (final ServerAuthModuleImpl module,
                                      final MessageInfo info)
            {
               if (!module.config.isFormAuthEnabled ()) {
                  return false;
               }

               final String resource = getResourceURI
                  ((HttpServletRequest) info.getRequestMessage ());

               return module.config.isInFormAuthResources (resource);
            }
         },
      BASIC (Action.TRY_ON_BASIC_METHOD, Action.SEND_BASIC_CHALLENGE)
         {
            @Override
            public boolean isEnabled (final ServerAuthModuleImpl module,
                                      final MessageInfo info)
            {
               if (!module.config.isBasicAuthEnabled ()) {
                  return false;
               }

               final String resource = getResourceURI
                  ((HttpServletRequest) info.getRequestMessage ());

               return module.config.isInBasicAuthResources (resource);
            }
         },
      NONE (Action.DO_NOTHING, Action.DO_NOTHING)
         {
            @Override
            public boolean isEnabled (final ServerAuthModuleImpl module,
                                      final MessageInfo info)
            {
               return true;
            }
         };

      public static Method findEnabled (final ServerAuthModuleImpl module,
                                        final MessageInfo info)
      {
         for (final Method method : Method.values ()) {
            if (method.isEnabled (module, info)) {
               return method;
            }
         }

         return null;
      }

      /////////////////////////////////////////////////////////////////////////
      //  Public section                                                     //
      /////////////////////////////////////////////////////////////////////////

      public final Action check;
      public final Action send;

      public abstract boolean isEnabled (final ServerAuthModuleImpl module,
                                         final MessageInfo info);

      /////////////////////////////////////////////////////////////////////////
      //  Private section                                                    //
      /////////////////////////////////////////////////////////////////////////

      private Method (final Action check,
                      final Action send)
      {
         this.check = check;
         this.send = send;
      }
   }

   private enum Action {
      TRY_ON_ALREADY_AUTHORIZED
         {
            @Override
            public AuthStatus perform (final ServerAuthModuleImpl module,
                                       final MessageInfo info,
                                       final Subject client)
               throws AuthException
            {
               return module.tryOnAlreadyAuthorized (info, client);
            }
         },
      TRY_ON_FORM_METHOD
         {
            @Override
            public AuthStatus perform (final ServerAuthModuleImpl module,
                                       final MessageInfo info,
                                       final Subject client)
               throws AuthException
            {
               return module.tryOnFormMethod (info, client);
            }
         },
      TRY_ON_BASIC_METHOD
         {
            @Override
            public AuthStatus perform (final ServerAuthModuleImpl module,
                                       final MessageInfo info,
                                       final Subject client)
               throws AuthException
            {
               return module.tryOnBasicMethod (info, client);
            }
         },
      TRY_ON_AUTH_REQUIRED
         {
            @Override
            public AuthStatus perform (final ServerAuthModuleImpl module,
                                       final MessageInfo info,
                                       final Subject client)
               throws AuthException
            {
               return module.tryOnAuthRequired (info, client);
            }
         },
      SEND_FORM_CHALLENGE
         {
            @Override
            public AuthStatus perform (final ServerAuthModuleImpl module,
                                       final MessageInfo info,
                                       final Subject client)
               throws AuthException
            {
               return module.sendFormChallenge (info);
            }
         },
      SEND_BASIC_CHALLENGE
         {
            @Override
            public AuthStatus perform (final ServerAuthModuleImpl module,
                                       final MessageInfo info,
                                       final Subject client)
            {
               return module.sendBasicChallenge (info);
            }
         },
      DO_NOTHING
         {
            @Override
            public AuthStatus perform (final ServerAuthModuleImpl module,
                                       final MessageInfo info,
                                       final Subject client)
            {
               return null;
            }
         };

      /////////////////////////////////////////////////////////////////////////
      //  Public section                                                     //
      /////////////////////////////////////////////////////////////////////////

      public abstract AuthStatus perform (final ServerAuthModuleImpl module,
                                          final MessageInfo info,
                                          final Subject client)
         throws AuthException;
   }

   private static final String POST                  = "POST";
   private static final String BASIC_PREFIX          = "Basic ";
   private static final String AUTHORIZATION_HEADER  = "Authorization";
   private static final String AUTHENTICATION_HEADER = "WWW-Authenticate";

   private static final String JASPIC_KEY_IS_MANDATORY     =
      "javax.security.auth.message.MessagePolicy.isMandatory";
   private static final String JASPIC_KEY_REGISTER_SESSION =
      "javax.servlet.http.registerSession";

   @SuppressWarnings ("rawtypes")
   private static final Class<?>[] SUPPORTED_MESSAGE_TYPES = new Class[]{
      HttpServletRequest.class,
      HttpServletResponse.class
   };

   private static boolean isAuthenticationRequired (final MessageInfo info) {
      return
         Boolean.parseBoolean
            ((String) info.getMap ().get (JASPIC_KEY_IS_MANDATORY));
   }

   @SuppressWarnings ("unchecked")
   private static void setRegisterSession (final MessageInfo info) {
      info.getMap ().put
         (JASPIC_KEY_REGISTER_SESSION, Boolean.TRUE.toString ());
   }

   private static String getResourceURI (final HttpServletRequest request) {
      return
         request.getRequestURI ().substring
            (request.getContextPath ().length ());
   }

   private static String getRelativeURL (final HttpServletRequest request) {
      if (request.getQueryString () == null) {
         return request.getRequestURI ();
      }
      return request.getRequestURI () + '?' + request.getQueryString ();
   }
}
