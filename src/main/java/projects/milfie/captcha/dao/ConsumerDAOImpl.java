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
import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

@RequestScoped
class ConsumerDAOImpl
   extends AbstractDAOImpl<Long, Consumer>
   implements ConsumerDAO
{

   public ConsumerDAOImpl () {
      super (Consumer.class);
   }

   @Override
   public List<Consumer> findAll () {
      return
         entityManager
            .createNamedQuery (Consumer.QUERY_FIND_ALL, entityClass)
            .getResultList ();
   }

   @Override
   public List<Consumer> findByClient (final String clientName) {
      return
         entityManager
            .createNamedQuery (Consumer.QUERY_FIND_BY_CLIENT, entityClass)
            .setParameter ("clientName", clientName)
            .getResultList ();
   }

   @Override
   public Consumer findByName (final String clientName,
                               final String consumerName)
   {
      final TypedQuery<Consumer> query = entityManager
         .createNamedQuery (Consumer.QUERY_FIND_BY_NAME, entityClass)
         .setParameter ("consumerName", consumerName)
         .setParameter ("clientName", clientName);

      final Consumer consumer;
      try {
         consumer = query.getSingleResult ();
      }
      catch (final NoResultException e) {
         return null;
      }
      return consumer;
   }

   @Override
   public int removeExpired (final long currentTime) {
      return
         entityManager
            .createNamedQuery (Consumer.QUERY_REMOVE_EXPIRED)
            .setParameter ("currentTime", currentTime)
            .executeUpdate ();
   }
}
