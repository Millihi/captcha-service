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
import projects.milfie.captcha.domain.Role;
import projects.milfie.captcha.i18n.ClientMessageBundle;
import projects.milfie.captcha.service.AdminServiceLocal;
import projects.milfie.captcha.service.ClientAlreadyExistsException;
import projects.milfie.captcha.service.ClientAlreadyModifiedException;

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
public class ClientControllerBean
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

      final String clientName = ectx.getRequestParameterMap ().get ("name");

      if (client == null) {
         if (clientName == null) {
            view = View.LIST;
            clients = service.findAll ();
         }
         else if (isClientName (clientName)) {
            view = View.VIEW;
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
            ("Inconsistent query parameter \"name\".");
      }
      else {
         view = View.VIEW;
      }

      menuControllerBean.setPageController (this);
      menuControllerBean.setClient (client);
   }

   @Override
   public void resourceInit () {
      super.resourceInit ();
      moduleMessages =
         new ClientMessageBundle (localeKeeperBean.getLocale ());
   }

   public void setMenuControllerBean
      (final MenuControllerBean menuControllerBean)
   {
      this.menuControllerBean = menuControllerBean;
   }

   public Collection<Client> getClients () {
      return clients;
   }

   public Client getClient () {
      return client;
   }

   public Role[] getRoles () {
      return Role.values ();
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

   public char[] getPassword () {
      return password;
   }

   public void setPassword (final char[] password) {
      this.password = password;
   }

   public String create () {
      client = new Client ();
      view = View.CREATE;
      return null;
   }

   public String view (final Client client) {
      this.client = client;
      getFlash ().put (FLASH_KEY_CLIENT, client);
      return CLIENT_VIEW + '?' + FACES_REDIRECT + '&' + FACES_PARAMS;
   }

   public String profile (final Client client) {
      this.client = client;
      getFlash ().put (FLASH_KEY_CLIENT, client);
      return
         PROFILE_VIEW + '?' +
         FACES_REDIRECT + '&' +
         "client=" + client.getName ();
   }

   public String consumer (final Client client) {
      this.client = client;
      getFlash ().put (FLASH_KEY_CLIENT, client);
      return
         CONSUMER_VIEW + '?' +
         FACES_REDIRECT + '&' +
         "client=" + client.getName ();
   }

   public String edit (final Client client) {
      this.client = client;
      view = View.EDIT;
      return null;
   }

   public String changePassword (final Client client) {
      this.client = client;
      view = View.PASSWORD;
      return null;
   }

   public String delete (final Client client) {
      this.client = client;
      view = View.DELETE;
      return null;
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

   private Collection<Client> clients;
   private Client             client;
   private View               view;
   private char[]             password;

   private void reset () {
      clients = Collections.emptyList ();
      client = null;
      view = View.LIST;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private enum View {
      LIST ("/WEB-INF/view/admin/client/list.xhtml")
         {
            @Override
            public String submit (final ClientControllerBean bean) {
               return null;
            }

            @Override
            public String cancel (final ClientControllerBean bean) {
               return null;
            }
         },
      CREATE ("/WEB-INF/view/admin/client/create.xhtml")
         {
            @Override
            public String submit (final ClientControllerBean bean) {
               bean.view = VIEW;
               try {
                  bean.client = bean.service.create
                     (bean.client, bean.password);
               }
               catch (final ClientAlreadyExistsException ignored) {
                  return bean.error
                     (bean.getGlobalMessage
                        ("ui.error.client.alreadyExists",
                         bean.client.getName ()));
               }
               getFlash ().put (FLASH_KEY_CLIENT, bean.client);
               return
                  CLIENT_VIEW + '?' + FACES_REDIRECT + '&' + FACES_PARAMS;
            }

            @Override
            public String cancel (final ClientControllerBean bean) {
               bean.view = LIST;
               return null;
            }
         },
      VIEW ("/WEB-INF/view/admin/client/view.xhtml")
         {
            @Override
            public String submit (final ClientControllerBean bean) {
               return null;
            }

            @Override
            public String cancel (final ClientControllerBean bean) {
               bean.reset ();
               return CLIENT_VIEW + '?' + FACES_REDIRECT;
            }
         },
      EDIT ("/WEB-INF/view/admin/client/edit.xhtml")
         {
            @Override
            public String submit (final ClientControllerBean bean) {
               bean.view = VIEW;
               try {
                  bean.client = bean.service.update (bean.client);
               }
               catch (final ClientAlreadyModifiedException ignored) {
                  return bean.error
                     (bean.getGlobalMessage
                        ("ui.error.client.hasChanged",
                         bean.client.getName ()));
               }
               return null;
            }

            @Override
            public String cancel (final ClientControllerBean bean) {
               bean.view = VIEW;
               return null;
            }
         },
      PASSWORD ("/WEB-INF/view/admin/client/password.xhtml")
         {
            @Override
            public String submit (final ClientControllerBean bean) {
               bean.view = VIEW;
               try {
                  bean.client = bean.service.changePassword
                     (bean.client, bean.password);
               }
               catch (final ClientAlreadyModifiedException ignored) {
                  return bean.error
                     (bean.getGlobalMessage
                        ("ui.error.client.hasChanged",
                         bean.client.getName ()));
               }
               return null;
            }

            @Override
            public String cancel (final ClientControllerBean bean) {
               bean.view = VIEW;
               return null;
            }
         },
      DELETE ("/WEB-INF/view/admin/client/delete.xhtml")
         {
            @Override
            public String submit (final ClientControllerBean bean) {
               bean.service.remove (bean.client);
               bean.reset ();
               return CLIENT_VIEW + '?' + FACES_REDIRECT;
            }

            @Override
            public String cancel (final ClientControllerBean bean) {
               bean.view = VIEW;
               return null;
            }
         },
      ERROR ("/WEB-INF/view/admin/client/error.xhtml")
         {
            @Override
            public String submit (final ClientControllerBean bean) {
               return this.cancel (bean);
            }

            @Override
            public String cancel (final ClientControllerBean bean) {
               return CLIENT_VIEW + '?' + FACES_REDIRECT + '&' + FACES_PARAMS;
            }
         };

      public abstract String submit (final ClientControllerBean bean);

      public abstract String cancel (final ClientControllerBean bean);

      private View (final String template) {
         this.template = template;
      }

      private final String template;
   }

   private static final long serialVersionUID = 201905280603L;
}
