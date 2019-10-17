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
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table (name = "CLIENTS")
@NamedQueries (value = {
   @NamedQuery (
      name = Client.QUERY_FIND_ALL,
      query = "SELECT c FROM Client c")})
public class Client
   implements Serializable
{

   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static final String QUERY_FIND_ALL = "Client.findAll";

   public static final String NAME_PATTERN     =
      "^[a-zA-Z][a-zA-Z0-9]*([_-][a-zA-Z0-9]+)*$";
   public static final String PASSWORD_PATTERN =
      "^[a-zA-Z0-9.<,>/?!;:'\"\\[\\]{}+=_-]+$";

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public Client () {
   }

   public Client (final String name,
                  final String password)
   {
      this.name = name;
      this.password = password;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  name
   @Id
   @NotNull
   @Pattern (regexp = Client.NAME_PATTERN)
   @Column (name = "NAME", nullable = false)
   private String name;

   public String getName () {
      return name;
   }

   public void setName (final String name) {
      this.name = name;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  password
   @NotNull
   @Pattern (regexp = Client.PASSWORD_PATTERN)
   @Column (name = "PASSWORD", nullable = false)
   private String password;

   public String getPassword () {
      return password;
   }

   public void setPassword (final String password) {
      this.password = password;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  roles
   @NotNull
   @Size (min = 0)
   @ElementCollection (
      targetClass = Role.class,
      fetch = FetchType.EAGER)
   @CollectionTable (
      name = "ROLES",
      joinColumns = {
         @JoinColumn (
            name = "CLIENT",
            nullable = false,
            updatable = false)},
      uniqueConstraints = {
         @UniqueConstraint (columnNames = {"CLIENT", "ROLE"})})
   @Column (name = "ROLE", nullable = false)
   @Enumerated (EnumType.STRING)
   private Set<Role> roles = new HashSet<> ();

   public Set<Role> getRoles () {
      return roles;
   }

   public void setRoles (final Set<Role> roles) {
      this.roles = roles;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @Version
   @Column (name = "LOCK_VERSION", nullable = false)
   private int version = 0;

   private static final long serialVersionUID = 201905220429L;
}
