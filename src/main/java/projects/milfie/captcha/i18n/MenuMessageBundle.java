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

package projects.milfie.captcha.i18n;

import java.util.Locale;

public class MenuMessageBundle
   extends AbstractUTF8MessageBundle
{

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public MenuMessageBundle () {
      super (BUNDLE_NAME);
   }

   public MenuMessageBundle (final Locale locale) {
      super (BUNDLE_NAME, locale);
   }

   public String getName () {
      return BUNDLE_NAME;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   protected static final String BUNDLE_NAME =
      "projects.milfie.captcha.i18n.menu";
}
