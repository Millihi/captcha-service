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

import projects.milfie.captcha.domain.Client;
import projects.milfie.captcha.domain.Consumer;
import projects.milfie.captcha.domain.Role;
import projects.milfie.captcha.i18n.ConsumerMessageBundle;
import projects.milfie.captcha.service.AdminServiceLocal;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import static projects.milfie.captcha.view.admin.Configuration.*;

@ViewScoped
@ManagedBean
@RolesAllowed ({Role.ADMIN_NAME})
public class ConsumerControllerBean
   extends AbstractPageController
   implements Serializable
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   @PostConstruct
   public void postConstructInit () {
      reset ();
      resourceInit ();

      final ExternalContext ectx = FacesContext
         .getCurrentInstance ()
         .getExternalContext ();

      client = (Client) getFlash ().get (FLASH_KEY_CLIENT);

      final String clientName = ectx.getRequestParameterMap ().get ("client");

      if (client == null) {
         if (clientName == null) {
            error (getGlobalMessage ("ui.error.client.noName"));
         }
         else if (isClientName (clientName)) {
            client = service.find (clientName);

            if (client == null) {
               error
                  (getGlobalMessage
                      ("ui.error.client.notFound", clientName));
            }
         }
         else {
            error (getGlobalMessage ("ui.error.client.illegalName"));
         }
      }
      else if (!client.getName ().equals (clientName)) {
         throw new IllegalStateException
            ("Inconsistent query param \"client\".");
      }

      menuControllerBean.setPageController (this);
      menuControllerBean.setClient (client);

      if (client == null) {
         return;
      }

      view = View.LIST;
      consumers = service.findConsumers (clientName);
   }

   @Override
   public void resourceInit () {
      super.resourceInit ();
      moduleMessages =
         new ConsumerMessageBundle (localeKeeperBean.getLocale ());
   }

   public void setMenuControllerBean
      (final MenuControllerBean menuControllerBean)
   {
      this.menuControllerBean = menuControllerBean;
   }

   public Client getClient () {
      return client;
   }

   public Collection<Consumer> getConsumers () {
      return consumers;
   }

   public String getState () {
      return view.name ().toLowerCase ();
   }

   public String getTemplate () {
      return view.template;
   }

   public String getClientName () {
      return (client == null ? null : client.getName ());
   }

   public void setClientName (final String clientName) {
      return;
   }

   public String error (final String message) {
      view = View.ERROR;
      setErrorMessage (message);
      return null;
   }

   public String submit () {
      return view.submit (this);
   }

   public String cancel () {
      return view.cancel (this);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @EJB
   private AdminServiceLocal service;

   @ManagedProperty (value = "#{menuControllerBean}")
   private MenuControllerBean menuControllerBean;

   private Client               client;
   private Collection<Consumer> consumers;
   private View                 view;

   private void reset () {
      consumers = Collections.emptyList ();
      view = View.LIST;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private enum View {
      LIST ("/WEB-INF/view/admin/consumer/list.xhtml")
         {
            @Override
            public String submit (final ConsumerControllerBean bean) {
               return null;
            }

            @Override
            public String cancel (final ConsumerControllerBean bean) {
               bean.reset ();
               getFlash ().put (FLASH_KEY_CLIENT, bean.client);
               return
                  CLIENT_VIEW + '?' +
                  FACES_REDIRECT + '&' +
                  "name=" + bean.client.getName ();
            }
         },
      ERROR ("/WEB-INF/view/admin/profile/error.xhtml")
         {
            @Override
            public String submit (final ConsumerControllerBean bean) {
               return this.cancel (bean);
            }

            @Override
            public String cancel (final ConsumerControllerBean bean) {
               getFlash ().put (FLASH_KEY_CLIENT, bean.client);
               return
                  PROFILE_VIEW + '?' + FACES_REDIRECT + '&' + FACES_PARAMS;
            }
         };

      public abstract String submit (final ConsumerControllerBean bean);

      public abstract String cancel (final ConsumerControllerBean bean);

      private View (final String template) {
         this.template = template;
      }

      private final String template;
   }

   private static final long serialVersionUID = 201905280603L;
}
