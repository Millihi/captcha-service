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

package projects.milfie.captcha.view.client;

import projects.milfie.captcha.domain.Profile;

import java.io.Serializable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ProfileDTO
   implements Serializable
{

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public ProfileDTO () {
   }

   public ProfileDTO (@NotNull @Pattern (regexp = Profile.NAME_PATTERN)
                      final String profileName)
   {
      this.profileName = profileName;
   }

   public ProfileDTO (@NotNull final Profile profile) {
      this.profileName = profile.getName ();
      this.consumerTTL = profile.getConsumerTTL ();
      this.puzzleTTL = profile.getPuzzleTTL ();
   }

   ////////////////////////////////////////////////////////////////////////////
   //  profileName
   @NotNull
   @Pattern (regexp = Profile.NAME_PATTERN)
   private String profileName;

   public String getProfileName () {
      return profileName;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  consumerTTL
   @NotNull
   @Min (Profile.ONE_SECOND)
   private long consumerTTL = Profile.DEFAULT_CONSUMER_TTL;

   public long getConsumerTTL () {
      return consumerTTL;
   }

   public void setConsumerTTL (final long consumerTTL) {
      this.consumerTTL = consumerTTL;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  puzzleTTL
   @NotNull
   @Min (Profile.ONE_SECOND)
   private long puzzleTTL = Profile.DEFAULT_PUZZLE_TTL;

   public long getPuzzleTTL () {
      return puzzleTTL;
   }

   public void setPuzzleTTL (final long puzzleTTL) {
      this.puzzleTTL = puzzleTTL;
   }

   @Override
   public int hashCode () {
      int hash = 0;
      hash = ((hash << 5) - hash) +
             (profileName == null ? 0 : profileName.hashCode ());
      hash = ((hash << 5) - hash) + (int) consumerTTL;
      hash = ((hash << 5) - hash) + (int) puzzleTTL;
      return hash;
   }

   @Override
   public boolean equals (final Object obj) {
      if (obj == this) {
         return true;
      }
      if (obj == null || obj.getClass () != this.getClass ()) {
         return false;
      }
      final ProfileDTO that = (ProfileDTO) obj;
      return
         (this.profileName.equals (that.profileName) &&
          this.consumerTTL == that.consumerTTL &&
          this.puzzleTTL == that.puzzleTTL);
   }

   @Override
   public String toString () {
      return
         this.getClass ().getSimpleName () + " : {" +
         "profileName : " + profileName + ", " +
         "consumerTTL : " + consumerTTL + ", " +
         "puzzleTTL : " + puzzleTTL + " }";
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private static final long serialVersionUID = 201905220431L;
}
