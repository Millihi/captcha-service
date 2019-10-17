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

import projects.milfie.captcha.dao.ClientDAO;
import projects.milfie.captcha.dao.ProfileDAO;
import projects.milfie.captcha.domain.*;

import java.util.Collection;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Stateless
@RolesAllowed ({Role.ADMIN_NAME, Role.USER_NAME})
public class ClientServiceBean
   extends ConsumerServiceBean
   implements ClientServiceLocal
{

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   public Collection<Profile> findProfiles
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName)
   {
      return profileDAO.findByClient (clientName);
   }

   @Override
   public Profile findProfile
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName,
       @NotNull @Pattern (regexp = Profile.NAME_PATTERN)
       final String profileName)
   {
      return profileDAO.findByNames (clientName, profileName);
   }

   @Override
   public Profile createProfile
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName,
       @NotNull @Pattern (regexp = Profile.NAME_PATTERN)
       final String profileName)
      throws ClientNotFoundException,
             ProfileAlreadyExistsException
   {
      final Client client = clientDAO.find (clientName);

      if (client == null) {
         throw new ClientNotFoundException
            ("Client \"" + clientName + "\" not found.");
      }

      if (profileDAO.findByNames (clientName, profileName) != null) {
         throw new ProfileAlreadyExistsException
            ("Client \"" + clientName +
             "\" already has profile \"" + profileName + "\".");
      }

      final Profile profile = new Profile (profileName, client);
      profileDAO.persist (profile);

      return profile;
   }

   @Override
   public Profile updateProfile (@NotNull final Profile profile)
      throws ProfileAlreadyModifiedException
   {
      try {
         return profileDAO.update (profile);
      }
      catch (final OptimisticLockException e) {
         throw new ProfileAlreadyModifiedException (e);
      }
   }

   @Override
   public void deleteProfile
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName,
       @NotNull @Pattern (regexp = Profile.NAME_PATTERN)
       final String profileName)
      throws ProfileNotFoundException,
             ProfileDeleteDefaultException
   {
      final Profile profile = profileDAO
         .findByNames (clientName, profileName);

      if (profile == null) {
         throw new ProfileNotFoundException
            ("Profile \"" + profileName + "\" not found.");
      }

      deleteProfile (profile);
   }

   @Override
   public void deleteProfile (@NotNull final Profile profile)
      throws ProfileDeleteDefaultException
   {
      if (Profile.DEFAULT_NAME.equalsIgnoreCase (profile.getName ())) {
         throw new ProfileDeleteDefaultException
            ("An attempt to remove default profile.");
      }

      final List<Consumer> consumers =
         consumerDAO.findByClient (profile.getClientName ());

      for (final Consumer consumer : consumers) {
         if (consumer.getProfileName ().equals (profile.getName ())) {
            consumerDAO.remove (consumer);
         }
      }

      profileDAO.remove (profile);
   }

   @Override
   public long activateConsumer
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName,
       @NotNull @Pattern (regexp = Profile.NAME_PATTERN)
       final String profileName,
       @NotNull @Pattern (regexp = Consumer.NAME_PATTERN)
       final String consumerName)
      throws ClientNotFoundException,
             ProfileNotFoundException
   {
      Consumer consumer = retrieve (clientName, consumerName);

      if (consumer == null) {
         final Client client = clientDAO.find (clientName);

         if (client == null) {
            throw new ClientNotFoundException
               ("Client \"" + clientName + "\" not found.");
         }

         final Profile profile = profileDAO
            .findByNames (clientName, profileName);

         if (profile == null) {
            throw new ProfileNotFoundException
               ("Profile \"" + profileName + "\" not found.");
         }

         consumer = new Consumer (consumerName, client, profile);
         consumerDAO.persist (consumer);
      }
      else if (!consumer.getProfileName ().equals (profileName)) {
         final Profile profile = profileDAO
            .findByNames (clientName, profileName);

         if (profile == null) {
            throw new ProfileNotFoundException
               ("Profile \"" + profileName + "\" not found.");
         }

         consumer.setProfile (profile);
         consumerDAO.update (consumer);
      }

      return consumer.getId ();
   }

   @Override
   public void deactivateConsumer
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName,
       @NotNull @Pattern (regexp = Consumer.NAME_PATTERN)
       final String consumerName)
   {
      final Consumer consumer = consumerDAO
         .findByName (clientName, consumerName);

      if (consumer != null) {
         consumerDAO.remove (consumer);
      }
   }

   @Override
   public boolean commitConsumerAnswer
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName,
       @NotNull @Pattern (regexp = Consumer.NAME_PATTERN)
       final String consumerName,
       @NotNull @Pattern (regexp = Puzzle.ANSWER_PATTERN)
       final String answer)
   {
      final Consumer consumer = retrieve (clientName, consumerName);

      if (consumer == null) {
         return false;
      }

      final Puzzle puzzle = consumer.getPuzzle ();

      if (puzzle.isEmpty ()) {
         return false;
      }

      consumer.setPuzzle (Puzzle.EMPTY);
      consumer.setLastPuzzleSolved (puzzle.getAnswer ().equals (answer));
      consumerDAO.update (consumer);

      return consumer.isLastPuzzleSolved ();
   }

   @Override
   public boolean isLastPuzzleSolved
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName,
       @NotNull @Pattern (regexp = Consumer.NAME_PATTERN)
       final String consumerName)
   {
      final Consumer consumer =
         consumerDAO.findByName (clientName, consumerName);

      return
         (consumer != null &&
          isActiveConsumer (consumer, System.currentTimeMillis ()) &&
          consumer.isLastPuzzleSolved ());
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @Inject
   protected ClientDAO clientDAO;

   @Inject
   protected ProfileDAO profileDAO;
}
