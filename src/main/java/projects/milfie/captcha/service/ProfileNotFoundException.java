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

public class ProfileNotFoundException
   extends ServiceException
{

   public ProfileNotFoundException () {
      super ();
   }

   public ProfileNotFoundException (final String message) {
      super (message);
   }

   public ProfileNotFoundException (final Throwable cause) {
      super (cause);
   }

   private static final long serialVersionUID = 201905220257L;
}
