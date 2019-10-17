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

import projects.milfie.captcha.domain.Client;
import projects.milfie.captcha.domain.Consumer;
import projects.milfie.captcha.domain.Profile;
import projects.milfie.captcha.domain.Puzzle;

import java.util.Collection;
import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Local
public interface ClientServiceLocal
   extends ConsumerServiceLocal
{

   public Collection<Profile> findProfiles
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName);

   public Profile findProfile
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName,
       @NotNull @Pattern (regexp = Profile.NAME_PATTERN)
       final String profileName);

   public Profile createProfile
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName,
       @NotNull @Pattern (regexp = Profile.NAME_PATTERN)
       final String profileName)
      throws ClientNotFoundException,
             ProfileAlreadyExistsException;

   public Profile updateProfile
      (@NotNull final Profile profile)
      throws ProfileAlreadyModifiedException;

   public void deleteProfile
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName,
       @NotNull @Pattern (regexp = Profile.NAME_PATTERN)
       final String profileName)
      throws ProfileNotFoundException,
             ProfileDeleteDefaultException;

   public void deleteProfile
      (@NotNull final Profile profile)
      throws ProfileDeleteDefaultException;

   public long activateConsumer
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName,
       @NotNull @Pattern (regexp = Profile.NAME_PATTERN)
       final String profileName,
       @NotNull @Pattern (regexp = Consumer.NAME_PATTERN)
       final String consumerName)
      throws ClientNotFoundException,
             ProfileNotFoundException;

   public void deactivateConsumer
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName,
       @NotNull @Pattern (regexp = Consumer.NAME_PATTERN)
       final String consumerName);

   public boolean commitConsumerAnswer
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName,
       @NotNull @Pattern (regexp = Consumer.NAME_PATTERN)
       final String consumerName,
       @NotNull @Pattern (regexp = Puzzle.ANSWER_PATTERN)
       final String answer);

   public boolean isLastPuzzleSolved
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName,
       @NotNull @Pattern (regexp = Consumer.NAME_PATTERN)
       final String consumerName);
}
