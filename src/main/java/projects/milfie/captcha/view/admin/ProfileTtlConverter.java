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
import javax.faces.convert.FacesConverter;
import javax.faces.convert.LongConverter;

@FacesConverter (value = "profileTtlConverter")
public class ProfileTtlConverter
   extends LongConverter
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static final Long ONE_SECOND = 1000L;
   public static final Long ONE_MINUTE = 60 * ONE_SECOND;

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   public Object getAsObject (final FacesContext context,
                              final UIComponent component,
                              final String value)
   {
      final Object seconds = super.getAsObject (context, component, value);

      if (seconds == null) {
         return null;
      }

      return ((Long) seconds * ONE_MINUTE);
   }

   @Override
   public String getAsString (final FacesContext context,
                              final UIComponent component,
                              final Object value)
   {
      if (value instanceof Long) {
         return
            super.getAsString
               (context, component, (Long) value / ONE_MINUTE);
      }

      return super.getAsString (context, component, value);
   }
}
