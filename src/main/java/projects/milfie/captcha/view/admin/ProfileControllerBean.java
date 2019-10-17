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
import projects.milfie.captcha.domain.Profile;
import projects.milfie.captcha.domain.Role;
import projects.milfie.captcha.i18n.ProfileMessageBundle;
import projects.milfie.captcha.service.*;

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
public class ProfileControllerBean
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

      profile = (Profile) getFlash ().get (FLASH_KEY_PROFILE);

      final String profileName = ectx.getRequestParameterMap ().get ("name");

      if (profile == null) {
         if (profileName == null) {
            view = View.LIST;
            profiles = service.findProfiles (clientName);
         }
         else if (isProfileName (profileName)) {
            view = View.VIEW;
            profile = service.findProfile (clientName, profileName);

            if (profile == null) {
               error
                  (getGlobalMessage
                      ("ui.error.profile.notFound", profileName));
               return;
            }
         }
         else {
            error (getGlobalMessage ("ui.error.profile.illegalName"));
            return;
         }
      }
      else if (!profile.getName ().equals (profileName)) {
         throw new IllegalStateException
            ("Inconsistent query param \"profile\".");
      }
      else {
         view = View.VIEW;
      }
   }

   @Override
   public void resourceInit () {
      super.resourceInit ();
      moduleMessages =
         new ProfileMessageBundle (localeKeeperBean.getLocale ());
   }

   public void setMenuControllerBean
      (final MenuControllerBean menuControllerBean)
   {
      this.menuControllerBean = menuControllerBean;
   }

   public Client getClient () {
      return client;
   }

   public Collection<Profile> getProfiles () {
      return profiles;
   }

   public Profile getProfile () {
      return profile;
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

   public String getProfileName () {
      return (profile == null ? null : profile.getName ());
   }

   public void setProfileName (final String profileName) {
      return;
   }

   public String create () {
      profile = new Profile (client);
      view = View.CREATE;
      return null;
   }

   public String view (final Profile profile) {
      this.profile = profile;
      view = View.VIEW;
      getFlash ().put (FLASH_KEY_CLIENT, client);
      getFlash ().put (FLASH_KEY_PROFILE, profile);
      return PROFILE_VIEW + '?' + FACES_REDIRECT + '&' + FACES_PARAMS;
   }

   public String edit (final Profile profile) {
      this.profile = profile;
      view = View.EDIT;
      return null;
   }

   public String delete (final Profile profile) {
      this.profile = profile;
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

   private Client              client;
   private Collection<Profile> profiles;
   private Profile             profile;
   private View                view;

   private void reset () {
      profiles = Collections.emptyList ();
      profile = null;
      view = View.LIST;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private enum View {
      LIST ("/WEB-INF/view/admin/profile/list.xhtml")
         {
            @Override
            public String submit (final ProfileControllerBean bean) {
               return null;
            }

            @Override
            public String cancel (final ProfileControllerBean bean) {
               bean.reset ();
               getFlash ().put (FLASH_KEY_CLIENT, bean.client);
               return
                  CLIENT_VIEW + '?' +
                  FACES_REDIRECT + '&' +
                  "name=" + bean.client.getName ();
            }
         },
      CREATE ("/WEB-INF/view/admin/profile/create.xhtml")
         {
            @Override
            public String submit (final ProfileControllerBean bean) {
               try {
                  bean.profile = bean.service.createProfile
                     (bean.profile.getClientName (),
                      bean.profile.getName ());
               }
               catch (final ProfileAlreadyExistsException ignored) {
                  return bean.error
                     (bean.getGlobalMessage
                        ("ui.error.profile.alreadyExists",
                         bean.profile.getName ()));
               }
               catch (final ClientNotFoundException ignored) {
                  return bean.error
                     (bean.getGlobalMessage
                        ("ui.error.profile.client.notFound",
                         bean.client.getName ()));
               }
               bean.view = VIEW;
               getFlash ().put (FLASH_KEY_CLIENT, bean.client);
               getFlash ().put (FLASH_KEY_PROFILE, bean.profile);
               return
                  PROFILE_VIEW + '?' + FACES_REDIRECT + '&' + FACES_PARAMS;
            }

            @Override
            public String cancel (final ProfileControllerBean bean) {
               bean.view = LIST;
               return null;
            }
         },
      VIEW ("/WEB-INF/view/admin/profile/view.xhtml")
         {
            @Override
            public String submit (final ProfileControllerBean bean) {
               return null;
            }

            @Override
            public String cancel (final ProfileControllerBean bean) {
               bean.reset ();
               getFlash ().put (FLASH_KEY_CLIENT, bean.client);
               return
                  PROFILE_VIEW + '?' + FACES_REDIRECT + '&' + FACES_PARAMS;
            }
         },
      EDIT ("/WEB-INF/view/admin/profile/edit.xhtml")
         {
            @Override
            public String submit (final ProfileControllerBean bean) {
               bean.view = VIEW;
               try {
                  bean.profile = bean.service.updateProfile (bean.profile);
               }
               catch (final ProfileAlreadyModifiedException ignored) {
                  return bean.error
                     (bean.getGlobalMessage
                        ("ui.error.profile.hasChanged",
                         bean.profile.getName ()));
               }
               return null;
            }

            @Override
            public String cancel (final ProfileControllerBean bean) {
               bean.view = VIEW;
               return null;
            }
         },
      DELETE ("/WEB-INF/view/admin/profile/delete.xhtml")
         {
            @Override
            public String submit (final ProfileControllerBean bean) {
               try {
                  bean.service.deleteProfile (bean.profile);
               }
               catch (final ProfileDeleteDefaultException e) {
                  return bean.error
                     (bean.getGlobalMessage
                        ("ui.error.profile.deleteDefault"));
               }
               bean.reset ();
               getFlash ().put (FLASH_KEY_CLIENT, bean.client);
               return
                  PROFILE_VIEW + '?' + FACES_REDIRECT + '&' + FACES_PARAMS;
            }

            @Override
            public String cancel (final ProfileControllerBean bean) {
               bean.view = VIEW;
               return null;
            }
         },
      ERROR ("/WEB-INF/view/admin/profile/error.xhtml")
         {
            @Override
            public String submit (final ProfileControllerBean bean) {
               return this.cancel (bean);
            }

            @Override
            public String cancel (final ProfileControllerBean bean) {
               getFlash ().put (FLASH_KEY_CLIENT, bean.client);
               return
                  PROFILE_VIEW + '?' + FACES_REDIRECT + '&' + FACES_PARAMS;
            }
         };

      public abstract String submit (final ProfileControllerBean bean);

      public abstract String cancel (final ProfileControllerBean bean);

      private View (final String template) {
         this.template = template;
      }

      private final String template;
   }

   private static final long serialVersionUID = 201905280603L;
}
