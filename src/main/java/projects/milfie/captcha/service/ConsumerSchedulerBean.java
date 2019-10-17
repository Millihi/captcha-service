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

package projects.milfie.captcha.service;

import projects.milfie.captcha.dao.ConsumerDAO;

import java.util.logging.Logger;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

@Singleton
public class ConsumerSchedulerBean {

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @Schedule (hour = "*", minute = "0/10", persistent = false)
   private void removeExpired () {
      final int count = consumerDAO
         .removeExpired (System.currentTimeMillis ());

      if (count > 0) {
         LOG.info ("Remove " + count + " expired consumers.");
      }
   }

   @Inject
   private ConsumerDAO consumerDAO;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final Logger LOG =
      Logger.getLogger (ConsumerSchedulerBean.class.getSimpleName ());
}
