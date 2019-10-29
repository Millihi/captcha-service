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

import javax.inject.Inject;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public final class AuthProviderRegistrar
   implements ServletContextListener
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   public void contextInitialized (final ServletContextEvent sce) {
      final AuthConfigProvider provider =
         new AuthConfigProviderImpl (moduleProvider);
      final AuthConfigFactory factory = AuthConfigFactory.getFactory ();
      final ServletContext servletContext = sce.getServletContext ();
      final String appContextID = getAppContextID (servletContext);

      factory.registerConfigProvider
         (provider,
          MESSAGE_LAYER,
          appContextID,
          PROVIDER_DESCRIPTION);
   }

   @Override
   public void contextDestroyed (final ServletContextEvent sce) {
      return;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @Inject
   private AuthModuleProvider moduleProvider;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String
      MESSAGE_LAYER        = "HttpServlet",
      PROVIDER_DESCRIPTION = "Captcha authentication config provider";

   private static String getAppContextID (final ServletContext context) {
      return
         context.getVirtualServerName () + ' ' + context.getContextPath ();
   }
}
