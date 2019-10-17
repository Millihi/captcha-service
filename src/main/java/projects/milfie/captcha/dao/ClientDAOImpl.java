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

import projects.milfie.captcha.domain.Client;

import java.util.Collection;
import javax.enterprise.context.RequestScoped;

@RequestScoped
class ClientDAOImpl
   extends AbstractDAOImpl<String, Client>
   implements ClientDAO
{

   public ClientDAOImpl () {
      super (Client.class);
   }

   @Override
   public Collection<Client> findAll () {
      return
         entityManager
            .createNamedQuery (Client.QUERY_FIND_ALL, entityClass)
            .getResultList ();
   }

   @Override
   public Client update (final Client entity) {
      final Client updatedEntity = entityManager.merge (entity);
      entityManager.flush ();
      return updatedEntity;
   }
}
