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

import java.util.logging.Logger;
import javax.inject.Inject;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
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
      final AuthConfigProvider provider =
         new AuthConfigProviderImpl (moduleFactory);
      final AuthConfigFactory factory = AuthConfigFactory.getFactory ();
      final ServletContext servletContext = sce.getServletContext ();
      final String appContextID = getAppContextID (servletContext);

      moduleRegistrationID = factory.registerConfigProvider
         (provider,
          MESSAGE_LAYER,
          appContextID,
          PROVIDER_DESCRIPTION);

      LOGGER.info
         ("Registered auth config provider " + moduleRegistrationID);
   }

   @Override
   public void contextDestroyed (final ServletContextEvent sce) {
      if (moduleRegistrationID == null || moduleRegistrationID.isEmpty ()) {
         return;
      }

      final AuthConfigFactory factory = AuthConfigFactory.getFactory ();

      factory.removeRegistration (moduleRegistrationID);

      LOGGER.info
         ("Deregistered auth config provider " + moduleRegistrationID);

      moduleRegistrationID = null;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @Inject
   private ServerAuthModuleFactory moduleFactory;

   private String moduleRegistrationID = null;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String
      MESSAGE_LAYER        = "HttpServlet",
      PROVIDER_DESCRIPTION = "Captcha authentication config provider";

   private static final Logger LOGGER =
      Logger.getLogger (AutoRegistrationListener.class.getName ());

   private static String getAppContextID (final ServletContext context) {
      return
         context.getVirtualServerName () + ' ' + context.getContextPath ();
   }
}
