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

package projects.milfie.captcha.security;

abstract class AppServerAuthModuleConfig
   extends HttpServletServerAuthConfig
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public AppServerAuthModuleConfig
      (final Class<? extends Schema> schemaClass,
       final String propertyPrefix)
   {
      super (schemaClass, propertyPrefix);
   }

   public abstract boolean isEnabled ();

   public abstract boolean isStateful ();

   public abstract String[] getResources ();
}
