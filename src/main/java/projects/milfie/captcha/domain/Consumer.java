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

package projects.milfie.captcha.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Table (name = "CONSUMERS")
@NamedQueries (value = {
   @NamedQuery (
      name = Consumer.QUERY_FIND_ALL,
      query = "SELECT c FROM Consumer c"),
   @NamedQuery (
      name = Consumer.QUERY_FIND_BY_CLIENT,
      query = "SELECT c FROM Consumer c " +
              "WHERE c.clientName = :clientName"),
   @NamedQuery (
      name = Consumer.QUERY_FIND_BY_NAME,
      query = "SELECT c FROM Consumer c " +
              "WHERE c.name = :consumerName " +
              "AND c.clientName = :clientName"),
   @NamedQuery (
      name = Consumer.QUERY_REMOVE_EXPIRED,
      query = "DELETE FROM Consumer c " +
              "WHERE :currentTime - c.lastActivity >= (" +
              "SELECT p.consumerTTL FROM Profile p " +
              "WHERE p.clientName = c.clientName " +
              "AND p.name = c.profileName)")})
public class Consumer
   implements Serializable
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static final String
      QUERY_FIND_ALL       = "Consumer.findAll",
      QUERY_FIND_BY_CLIENT = "Consumer.findByClient",
      QUERY_FIND_BY_NAME   = "Consumer.findByName",
      QUERY_REMOVE_EXPIRED = "Consumer.removeExpired";

   public static final String
      NAME_PATTERN = "^[a-zA-Z0-9_-]+$";

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public Consumer (final String name,
                    final Client client,
                    final Profile profile)
   {
      this.name = name;
      this.setClient (client);
      this.setProfile (profile);
      this.lastActivity = System.currentTimeMillis ();
   }

   ////////////////////////////////////////////////////////////////////////////
   //  id
   @Id
   @GeneratedValue
      (generator = "ConsumerIdSeq",
       strategy = GenerationType.SEQUENCE)
   @SequenceGenerator
      (name = "ConsumerIdSeq",
       sequenceName = "CONSUMER_ID_SEQ",
       initialValue = 1,
       allocationSize = 1)
   @Column (name = "ID", nullable = false)
   private long id;

   public long getId () {
      return id;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  name
   @NotNull
   @Pattern (regexp = Consumer.NAME_PATTERN)
   @Column (name = "NAME", nullable = false)
   private String name;

   public String getName () {
      return name;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  clientName
   @NotNull
   @Pattern (regexp = Client.NAME_PATTERN)
   @Column (name = "CLIENT_NAME", nullable = false)
   private String clientName;

   public String getClientName () {
      return clientName;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  profileName
   @NotNull
   @Pattern (regexp = Profile.NAME_PATTERN)
   @Column (name = "PROFILE_NAME", nullable = false)
   private String profileName;

   public String getProfileName () {
      return profileName;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  client
   @NotNull
   @ManyToOne (optional = false, fetch = FetchType.LAZY)
   @JoinColumn (
      name = "CLIENT_NAME",
      referencedColumnName = "NAME",
      insertable = false,
      updatable = false)
   private Client client;

   public Client getClient () {
      return client;
   }

   protected void setClient (final Client client) {
      this.client = client;
      this.clientName = client.getName ();
   }

   ////////////////////////////////////////////////////////////////////////////
   //  profile
   @NotNull
   @ManyToOne (optional = false, fetch = FetchType.EAGER)
   @JoinColumns (value = {
      @JoinColumn (
         name = "PROFILE_NAME",
         referencedColumnName = "NAME",
         insertable = false,
         updatable = false),
      @JoinColumn (
         name = "CLIENT_NAME",
         referencedColumnName = "CLIENT_NAME",
         insertable = false,
         updatable = false)})
   private Profile profile;

   public Profile getProfile () {
      return profile;
   }

   public void setProfile (final Profile profile) {
      this.profile = profile;
      this.profileName = profile.getName ();
   }

   ////////////////////////////////////////////////////////////////////////////
   //  lastActivity
   @NotNull
   @Min (1)
   @Column (name = "LAST_ACTIVITY", nullable = false)
   private long lastActivity;

   public long getLastActivity () {
      return lastActivity;
   }

   public void setLastActivity (final long lastActivity) {
      this.lastActivity = lastActivity;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  puzzle
   @NotNull
   @Embedded
   private Puzzle puzzle = Puzzle.EMPTY;

   public Puzzle getPuzzle () {
      return puzzle;
   }

   public void setPuzzle (final Puzzle puzzle) {
      this.puzzle = puzzle;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  lastPuzzleSolved
   @Column (name = "SOLVED_LAST_PUZZLE", nullable = false)
   private boolean lastPuzzleSolved = false;

   public boolean isLastPuzzleSolved () {
      return lastPuzzleSolved;
   }

   public void setLastPuzzleSolved (final boolean lastPuzzleSolved) {
      this.lastPuzzleSolved = lastPuzzleSolved;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   protected Consumer () {
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final long serialVersionUID = 201905220430L;
}
