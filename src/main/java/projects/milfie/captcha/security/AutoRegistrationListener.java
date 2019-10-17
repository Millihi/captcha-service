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
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public final class AutoRegistrationListener
   implements ServletContextListener
{

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   public void contextInitialized (final ServletContextEvent sce) {
      final AuthConfigProvider provider = new AuthConfigProviderImpl (module);
      final AuthConfigFactory factory = AuthConfigFactory.getFactory ();
      final ServletContext servletContext = sce.getServletContext ();

      moduleRegistrationID = factory.registerConfigProvider
         (provider,
          "HttpServlet",
          getAppContextID (servletContext),
          "Captcha authentication config provider");
   }

   @Override
   public void contextDestroyed (final ServletContextEvent sce) {
      final AuthConfigFactory factory = AuthConfigFactory.getFactory ();

      factory.removeRegistration (moduleRegistrationID);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @Inject
   private ServerAuthModule module;
   private String moduleRegistrationID = "";

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static String getAppContextID (final ServletContext context) {
      return
         context.getVirtualServerName () + ' ' + context.getContextPath ();
   }
}
