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
@Table (name = "PROFILES")
@NamedQueries (value = {
   @NamedQuery (
      name = Profile.QUERY_FIND_ALL,
      query = "SELECT p FROM Profile p"),
   @NamedQuery (
      name = Profile.QUERY_FIND_BY_CLIENT,
      query = "SELECT p FROM Profile p " +
              "WHERE p.clientName = :clientName")})
@IdClass (ProfilePK.class)
public class Profile
   implements Serializable
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static final String QUERY_FIND_ALL       = "Profile.findAll";
   public static final String QUERY_FIND_BY_CLIENT = "Profile.findByClient";

   public static final String NAME_PATTERN =
      "^[a-zA-Z][a-zA-Z0-9]*([_-][a-zA-Z0-9]+)*$";

   public static final String DEFAULT_NAME = "Default";

   public static final long ONE_SECOND           = 1000L;
   public static final long DEFAULT_CONSUMER_TTL = 30 * 60 * ONE_SECOND;
   public static final long DEFAULT_PUZZLE_TTL   = 1 * 60 * ONE_SECOND;

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public Profile (final Client client) {
      this.setClient (client);
   }

   public Profile (final String name,
                   final Client client)
   {
      this.setClient (client);
      this.name = name;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  name
   @Id
   @NotNull
   @Pattern (regexp = NAME_PATTERN)
   @Column (name = "NAME", nullable = false)
   private String name;

   public String getName () {
      return name;
   }

   public void setName (final String name) {
      this.name = name;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  clientName
   @Id
   @NotNull
   @Pattern (regexp = NAME_PATTERN)
   @Column (name = "CLIENT_NAME", nullable = false)
   private String clientName;

   public String getClientName () {
      return clientName;
   }

   protected void setClientName (final String clientName) {
      this.clientName = clientName;
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
   //  consumerTTL
   @NotNull
   @Min (ONE_SECOND)
   @Column (name = "CONSUMER_TTL", nullable = false)
   private long consumerTTL = DEFAULT_CONSUMER_TTL;

   public long getConsumerTTL () {
      return consumerTTL;
   }

   public void setConsumerTTL (final long consumerTTL) {
      this.consumerTTL = consumerTTL;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  puzzleTTL
   @NotNull
   @Min (ONE_SECOND)
   @Column (name = "PUZZLE_TTL", nullable = false)
   private long puzzleTTL = DEFAULT_PUZZLE_TTL;

   public long getPuzzleTTL () {
      return puzzleTTL;
   }

   public void setPuzzleTTL (final long puzzleTTL) {
      this.puzzleTTL = puzzleTTL;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @Version
   @Column (name = "LOCK_VERSION", nullable = false)
   private int version = 0;

   protected Profile () {
   }

   private static final long serialVersionUID = 201905220431L;
}
