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

public class ProfileAlreadyExistsException
   extends ServiceException
{

   public ProfileAlreadyExistsException () {
      super ();
   }

   public ProfileAlreadyExistsException (final String message) {
      super (message);
   }

   public ProfileAlreadyExistsException (final Throwable cause) {
      super (cause);
   }

   private static final long serialVersionUID = 201905240531L;
}
