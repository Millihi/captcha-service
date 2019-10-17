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

package projects.milfie.captcha.view.admin;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter (value = "clientPasswordConverter")
public class ClientPasswordConverter
   implements Converter
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   public Object getAsObject (final FacesContext context,
                              final UIComponent component,
                              final String value)
   {
      if (context == null || component == null) {
         throw new NullPointerException ();
      }

      if (value == null || hasZeroLength (value)) {
         return (null);
      }

      return value.toCharArray ();
   }

   @Override
   public String getAsString (final FacesContext context,
                              final UIComponent component,
                              final Object value)
   {
      if (context == null || component == null) {
         throw new NullPointerException ();
      }

      return "";
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static boolean hasZeroLength (final String string) {
      if (string.isEmpty ()) {
         return true;
      }

      int zeroes = 0;

      for (int i = 0; i < string.length (); ++i) {
         if (string.charAt (i) == ' ') {
            ++zeroes;
         }
      }

      return (zeroes == string.length ());
   }
}
