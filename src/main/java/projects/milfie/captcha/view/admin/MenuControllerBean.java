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

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import static projects.milfie.captcha.view.admin.Configuration.*;

@ViewScoped
@ManagedBean
public class MenuControllerBean
   extends AbstractController
   implements Serializable
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public enum Item {
      HOME
         {
            @Override
            public boolean isEnabled (final MenuControllerBean bean) {
               return true;
            }

            @Override
            public boolean isActive (final MenuControllerBean bean) {
               return
                  (bean.pageController instanceof ClientControllerBean &&
                   bean.client == null);
            }

            @Override
            public String view (final MenuControllerBean bean) {
               return CLIENT_VIEW + '?' + FACES_REDIRECT;
            }
         },
      CLIENT
         {
            @Override
            public boolean isEnabled (final MenuControllerBean bean) {
               return (bean.client != null);
            }

            @Override
            public boolean isActive (final MenuControllerBean bean) {
               return
                  (bean.pageController instanceof ClientControllerBean &&
                   bean.client != null);
            }

            @Override
            public String view (final MenuControllerBean bean) {
               getFlash ().put (FLASH_KEY_CLIENT, bean.client);
               return
                  CLIENT_VIEW + '?' +
                  FACES_REDIRECT + '&' +
                  "name=" + bean.client.getName ();
            }
         },
      PROFILE
         {
            @Override
            public boolean isEnabled (final MenuControllerBean bean) {
               return (bean.client != null);
            }

            @Override
            public boolean isActive (final MenuControllerBean bean) {
               return bean.pageController instanceof ProfileControllerBean;
            }

            @Override
            public String view (final MenuControllerBean bean) {
               getFlash ().put (FLASH_KEY_CLIENT, bean.client);
               return
                  PROFILE_VIEW + '?' +
                  FACES_REDIRECT + '&' +
                  "client=" + bean.client.getName ();
            }
         },
      CONSUMER
         {
            @Override
            public boolean isEnabled (final MenuControllerBean bean) {
               return (bean.client != null);
            }

            @Override
            public boolean isActive (final MenuControllerBean bean) {
               return bean.pageController instanceof ConsumerControllerBean;
            }

            @Override
            public String view (final MenuControllerBean bean) {
               return
                  CONSUMER_VIEW + '?' +
                  FACES_REDIRECT + '&' +
                  "client=" + bean.client.getName ();
            }
         };

      public String getKey () {
         return this.key;
      }

      public abstract boolean isEnabled (final MenuControllerBean bean);

      public abstract boolean isActive (final MenuControllerBean bean);

      public abstract String view (final MenuControllerBean bean);

      private Item () {
         this.key = this.name ().toLowerCase ();
      }

      private String key;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public Client getClient () {
      return client;
   }

   public void setClient (final Client client) {
      this.client = client;
   }

   public AbstractPageController getPageController () {
      return pageController;
   }

   public void setPageController
      (final AbstractPageController pageController)
   {
      this.pageController = pageController;
   }

   public Item[] getItems () {
      return Item.values ();
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private AbstractPageController pageController;
   private Client                 client;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final long serialVersionUID = 201906220727L;
}
