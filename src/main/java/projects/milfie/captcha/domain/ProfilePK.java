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

public class ProfilePK
   implements Serializable
{

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public ProfilePK () {
   }

   public ProfilePK (final String name,
                     final String clientName)
   {
      this.name = name;
      this.clientName = clientName;
   }

   public String getName () {
      return name;
   }

   public String getClientName () {
      return clientName;
   }

   @Override
   public int hashCode () {
      int hash = 0;
      hash = ((hash << 5) - hash) +
             (name == null ? 0 : name.hashCode ());
      hash = ((hash << 5) - hash) +
             (clientName == null ? 0 : clientName.hashCode ());
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
      final ProfilePK that = (ProfilePK) obj;
      return
         (this.name.equals (that.name) &&
          this.clientName.equals (that.clientName));
   }

   @Override
   public String toString () {
      return
         this.getClass ().getSimpleName () + " : {" +
         "name : " + name + ", " +
         "clientName : " + clientName + " }";
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   protected String name;
   protected String clientName;

   private static final long serialVersionUID = 201905280602L;
}
