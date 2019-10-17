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

public class ProfileDeleteDefaultException
   extends ServiceException
{

   public ProfileDeleteDefaultException () {
      super ();
   }

   public ProfileDeleteDefaultException (final String message) {
      super (message);
   }

   public ProfileDeleteDefaultException (final Throwable cause) {
      super (cause);
   }

   private static final long serialVersionUID = 201906280632L;
}
