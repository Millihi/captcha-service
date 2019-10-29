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
public final class BasicServerAuthModuleConfig
   extends AppServerAuthModuleConfig
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public BasicServerAuthModuleConfig () {
      super (BasicSchema.class, CATEGORY);
   }

   @Override
   public boolean isEnabled () {
      return enabled;
   }

   @Override
   public boolean isStateful () {
      return stateful;
   }

   @Override
   public String[] getResources () {
      return resources;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private boolean  enabled;
   private boolean  stateful;
   private String[] resources;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String CATEGORY = "basic";

   private enum BasicSchema
      implements Schema
   {
      /////////////////////////////////////////////////////////////////////////
      //  Public static section                                              //
      /////////////////////////////////////////////////////////////////////////

      ENABLED (Converter.BOOLEAN, "false"),
      STATEFUL (Converter.BOOLEAN, "false"),
      RESOURCES (Converter.STRING_ARRAY, "/");

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

      private BasicSchema (final Converter converter,
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
