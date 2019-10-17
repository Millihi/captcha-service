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

public interface ProfileDAO
   extends AbstractDAO<ProfilePK, Profile>
{

   public Collection<Profile> findAll ();

   public Collection<Profile> findByClient (final String clientName);

   public Profile findByNames (final String clientName,
                               final String profileName);
}
