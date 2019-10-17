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

import projects.milfie.captcha.domain.Profile;
import projects.milfie.captcha.domain.ProfilePK;

import java.util.Collection;
import javax.enterprise.context.RequestScoped;

@RequestScoped
class ProfileDAOImpl
   extends AbstractDAOImpl<ProfilePK, Profile>
   implements ProfileDAO
{

   public ProfileDAOImpl () {
      super (Profile.class);
   }

   @Override
   public Collection<Profile> findAll () {
      return
         entityManager
            .createNamedQuery (Profile.QUERY_FIND_ALL, entityClass)
            .getResultList ();
   }

   @Override
   public Collection<Profile> findByClient (final String clientName) {
      return
         entityManager
            .createNamedQuery (Profile.QUERY_FIND_BY_CLIENT, entityClass)
            .setParameter ("clientName", clientName)
            .getResultList ();
   }

   @Override
   public Profile findByNames (final String clientName,
                               final String profileName)
   {
      return
         entityManager.find
            (entityClass, new ProfilePK (profileName, clientName));
   }

   @Override
   public Profile update (final Profile entity) {
      final Profile updatedEntity = entityManager.merge (entity);
      entityManager.flush ();
      return updatedEntity;
   }
}
