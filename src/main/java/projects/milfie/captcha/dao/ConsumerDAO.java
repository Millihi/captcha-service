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

package projects.milfie.captcha.dao;

import projects.milfie.captcha.domain.Consumer;

import java.util.List;

public interface ConsumerDAO
   extends AbstractDAO<Long, Consumer>
{

   public List<Consumer> findAll ();

   public List<Consumer> findByClient (final String clientName);

   public Consumer findByName (final String clientName,
                               final String consumerName);

   public int removeExpired (final long currentTime);
}
