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
import java.util.EnumMap;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.HttpServletRequest;

import static projects.milfie.captcha.security.Configuration.AuthType;

public final class ServerAuthContextImpl
   implements ServerAuthContext
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public ServerAuthContextImpl (final CallbackHandler handler,
                                 final ServerAuthModuleFactory moduleFactory)
      throws AuthException
   {
      if (handler == null) {
         throw new IllegalArgumentException ("Given handler is null.");
      }
      if (moduleFactory == null) {
         throw new IllegalArgumentException ("Given moduleFactory is null.");
      }

      this.handler = handler;
      this.moduleFactory = moduleFactory;
      this.modules = new EnumMap<> (AuthType.class);

      final Map<String, String> properties = Collections.emptyMap ();

      this.config = moduleFactory.getInstance (Configuration.class);

      for (final AuthType type : AuthType.values ()) {
         final AppServerAuthModule module =
            moduleFactory.getInstance (type.getModuleClass ());
         module.initialize
            (DEFAULT_POLICY, DEFAULT_POLICY, handler, properties);
         modules.put (type, module);
      }
   }

   @Override
   public AuthStatus validateRequest (final MessageInfo messageInfo,
                                      final Subject clientSubject,
                                      final Subject serviceSubject)
      throws AuthException
   {
      return
         getInstance (getResourceURI (messageInfo))
            .validateRequest (messageInfo, clientSubject, serviceSubject);
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

   private final Configuration                          config;
   private final CallbackHandler                        handler;
   private final ServerAuthModuleFactory                moduleFactory;
   private final EnumMap<AuthType, AppServerAuthModule> modules;

   private ServerAuthModule getInstance (final String resource) {
      return modules.get (config.getAuthType (resource));
   }

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
