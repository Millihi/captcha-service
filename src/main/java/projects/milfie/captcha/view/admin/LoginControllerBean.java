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

package projects.milfie.captcha.view.admin;

import projects.milfie.captcha.i18n.LoginMessageBundle;

import java.io.Serializable;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static projects.milfie.captcha.security.Configuration.*;
import static projects.milfie.captcha.view.admin.Configuration.*;

@ViewScoped
@ManagedBean
public class LoginControllerBean
   extends AbstractPageController
   implements Serializable
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   @PostConstruct
   public void postConstructInit () {
      resourceInit ();
   }

   @Override
   public void resourceInit () {
      super.resourceInit ();
      moduleMessages =
         new LoginMessageBundle (localeKeeperBean.getLocale ());
   }

   public void loginDispatcher () {
      final FacesContext fctx = FacesContext.getCurrentInstance ();
      final ExternalContext ectx = fctx.getExternalContext ();
      final Principal principal = ectx.getUserPrincipal ();

      final HttpServletRequest request =
         (HttpServletRequest) ectx.getRequest ();

      if (isPostAuth (request)) {
         if (principal == null) {
            setErrorMessage (getGlobalMessage ("ui.error.login.incorrect"));
         }
         else {
            final HttpSession session = request.getSession (false);
            final String redirectURL = getRedirectURL (session);

            if (redirectURL == null) {
               performFacesRedirect (INITIAL_VIEW + '?' + FACES_REDIRECT);
            }
            else {
               session.removeAttribute (SESSION_KEY_REDIRECT_TO);
               try {
                  ectx.redirect (redirectURL);
               }
               catch (final Throwable thrown) {
                  LOG.log
                     (Level.WARNING,
                      "An error oocured while send redirect:",
                      thrown);
               }
            }
         }
      }
      else if (principal != null) {
         performFacesRedirect (INITIAL_VIEW + '?' + FACES_REDIRECT);
      }
   }

   public void logoutDispatcher () {
      final Principal principal = FacesContext
         .getCurrentInstance ()
         .getExternalContext ()
         .getUserPrincipal ();

      if (principal == null) {
         performFacesRedirect (WELCOME_VIEW + '?' + FACES_REDIRECT);
      }
   }

   public String logout () {
      final ExternalContext ectx = FacesContext
         .getCurrentInstance ()
         .getExternalContext ();

      if (ectx.getUserPrincipal () != null) {
         try {
            ((HttpServletRequest) ectx.getRequest ()).logout ();
         }
         catch (final ServletException e) {
            LOG.warning
               ("An attempt to call the logout () failed with \"" +
                e.getMessage () + "\".");
         }
         ectx.invalidateSession ();
      }

      return WELCOME_VIEW + '?' + FACES_REDIRECT;
   }

   public String cancel () {
      final Principal principal = FacesContext
         .getCurrentInstance ()
         .getExternalContext ()
         .getUserPrincipal ();

      if (principal == null) {
         return WELCOME_VIEW + '?' + FACES_REDIRECT;
      }

      return INITIAL_VIEW + '?' + FACES_REDIRECT;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final Logger LOG =
      Logger.getLogger (LoginControllerBean.class.getSimpleName ());

   private static final long serialVersionUID = 201905280604L;

   private static boolean isPostAuth (final HttpServletRequest request) {
      if (request == null) {
         return false;
      }

      return
         Boolean.parseBoolean
            ((String) request.getAttribute (ATTR_KEY_IS_POST_AUTH));
   }

   private static String getRedirectURL (final HttpSession session) {
      if (session == null) {
         return null;
      }

      return (String) session.getAttribute (SESSION_KEY_REDIRECT_TO);
   }
}
