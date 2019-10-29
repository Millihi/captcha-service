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
public final class FormServerAuthModuleConfig
   extends AppServerAuthModuleConfig
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public FormServerAuthModuleConfig () {
      super (FormSchema.class, CATEGORY);
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

   public String getLoginPage () {
      return loginPage;
   }

   public String getErrorPage () {
      return errorPage;
   }

   public String getAction () {
      return action;
   }

   public String getUsernameField () {
      return usernameField;
   }

   public String getPasswordField () {
      return passwordField;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private boolean  enabled;
   private boolean  stateful;
   private String[] resources;
   private String   loginPage;
   private String   errorPage;
   private String   action;
   private String   usernameField;
   private String   passwordField;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String CATEGORY = "form";

   private enum FormSchema
      implements Schema
   {
      /////////////////////////////////////////////////////////////////////////
      //  Public static section                                              //
      /////////////////////////////////////////////////////////////////////////

      ENABLED (Converter.BOOLEAN, "false"),
      STATEFUL (Converter.BOOLEAN, "true"),
      RESOURCES (Converter.STRING_ARRAY, "/"),
      LOGIN_PAGE (Converter.STRING, "login.jsp"),
      ERROR_PAGE (Converter.STRING, "403.jsp"),
      ACTION (Converter.STRING, "j_security_check"),
      USERNAME_FIELD (Converter.STRING, "j_username"),
      PASSWORD_FIELD (Converter.STRING, "j_password");

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

      private FormSchema (final Converter converter,
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
