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

import java.lang.reflect.Field;
import javax.inject.Singleton;

@Singleton
public final class GeneralServerAuthConfig
   extends HttpServletServerAuthConfig
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static final String
      ATTR_KEY_IS_POST_AUTH   = "projects.milfie.captcha.security.isPostAuth",
      SESSION_KEY_REDIRECT_TO = "projects.milfie.captcha.security.redirectTo";

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public GeneralServerAuthConfig () {
      super (GeneralSchema.class, CATEGORY);
   }

   public String[] getExcludedResources () {
      return excludedResources;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private volatile String[] excludedResources;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String CATEGORY = "general";

   private enum GeneralSchema
      implements Schema
   {
      /////////////////////////////////////////////////////////////////////////
      //  Public static section                                              //
      /////////////////////////////////////////////////////////////////////////

      EXCLUDED_RESOURCES (Converter.STRING_ARRAY, "");

      /////////////////////////////////////////////////////////////////////////
      //  Public section                                                     //
      /////////////////////////////////////////////////////////////////////////

      @Override
      public String getName () {
         return name;
      }

      @Override
      public Field getField () {
         return field;
      }

      @Override
      public Converter getConverter () {
         return converter;
      }

      @Override
      public String getDefaultValue () {
         return defaultValue;
      }

      /////////////////////////////////////////////////////////////////////////
      //  Private section                                                    //
      /////////////////////////////////////////////////////////////////////////

      private GeneralSchema (final Converter converter,
                             final String defaultValue)
      {
         this.name = Schema.getName (this);
         this.field = Schema.getField (this, this.name);
         this.converter = converter;
         this.defaultValue = defaultValue;
      }

      private final String    name;
      private final Field     field;
      private final Converter converter;
      private final String    defaultValue;
   }
}
