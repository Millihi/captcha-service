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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.regex.Pattern;

abstract class HttpServletServerAuthConfig {
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static final String CONFIG_FILE = "server-auth-module-config.xml";

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public HttpServletServerAuthConfig
      (final Class<? extends Schema> schemaClass,
       final String category)
   {
      if (schemaClass == null || !schemaClass.isEnum ()) {
         throw new IllegalArgumentException ("Wrong schema class given.");
      }
      if (category == null || category.isEmpty ()) {
         throw new IllegalArgumentException ("Given category is empty.");
      }

      this.schemaClass = schemaClass;
      this.category = category;
   }

   public String getCategory () {
      return category;
   }

   public final void reset () {
      loadDefaults ();
   }

   public final void reload () {
      loadFromFile ();
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final Class<? extends Schema> schemaClass;
   private final String                  category;

   protected void loadDefaults () {
      for (final Schema key : schemaClass.getEnumConstants ()) {
         setFieldValue
            (key.getField (), this,
             key.getConverter ().convert (key.getDefaultValue ()));
      }
   }

   protected void loadFromFile () {
      final Properties p = loadProperties (CONFIG_FILE);

      for (final Schema key : schemaClass.getEnumConstants ()) {
         setFieldValue
            (key.getField (), this,
             key.getConverter ().convert
                (p.getProperty
                   (getFullPropertyName (key.getName ()),
                    key.getDefaultValue ())));
      }
   }

   private String getFullPropertyName (final String localName) {
      return category + '.' + localName;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   protected interface Schema {

      public String getName ();

      public Field getField ();

      public Converter getConverter ();

      public String getDefaultValue ();

      static Field getField (final Enum<? extends Schema> schema,
                             final String name)
      {
         final Field field;

         try {
            field = schema
               .getDeclaringClass ()
               .getEnclosingClass ()
               .getDeclaredField (name);
         }
         catch (final NoSuchFieldException cause) {
            throw new IllegalStateException (cause);
         }

         if (field == null) {
            throw new IllegalStateException
               ("Could not obtain field named " + name);
         }

         return field;
      }

      static String getName (final Enum<? extends Schema> schema) {
         final String enumName = schema.name ();
         final int length = enumName.length ();
         final StringBuilder canonName = new StringBuilder ();

         int idx = 0;
         boolean meetUnderscore = false;

         if (enumName.charAt (idx) == '_') {
            canonName.append (enumName.charAt (idx++));
         }

         while (idx < length) {
            final char ch = enumName.charAt (idx++);

            if (ch == '_') {
               meetUnderscore = true;
            }
            else if (Character.isLetter (ch)) {
               if (meetUnderscore) {
                  meetUnderscore = false;
                  canonName.append (Character.toUpperCase (ch));
               }
               else {
                  canonName.append (Character.toLowerCase (ch));
               }
            }
         }

         return canonName.toString ();
      }
   }

   protected enum Converter {
      /////////////////////////////////////////////////////////////////////////
      //  Public static section                                              //
      /////////////////////////////////////////////////////////////////////////

      BOOLEAN
         {
            @Override
            public Boolean convert (final String value) {
               return Boolean.parseBoolean (value);
            }
         },
      INTEGER
         {
            @Override
            public Integer convert (final String value) {
               return Integer.parseInt (value);
            }
         },
      STRING
         {
            @Override
            public String convert (final String value) {
               return value;
            }
         },
      STRING_ARRAY
         {
            @Override
            public String[] convert (final String value) {
               if (value == null || value.isEmpty ()) {
                  return EMPTY_STRING_ARRAY;
               }
               return STRING_ARRAY_PATTERN.split (value);
            }
         };

      /////////////////////////////////////////////////////////////////////////
      //  Public section                                                     //
      /////////////////////////////////////////////////////////////////////////

      public abstract Object convert (final String value);

      /////////////////////////////////////////////////////////////////////////
      //  Private static section                                             //
      /////////////////////////////////////////////////////////////////////////

      private static final String[] EMPTY_STRING_ARRAY = new String[0];

      private static final Pattern
         STRING_ARRAY_PATTERN = Pattern.compile ("\\s*,+\\s*");
   }

   private static Properties loadProperties (final String configFile) {
      final Properties properties = new Properties ();

      final ClassLoader classLoader = Thread
         .currentThread ()
         .getContextClassLoader ();

      if (classLoader == null) {
         return properties;
      }

      final InputStream configStream =
         classLoader.getResourceAsStream (configFile);

      if (configStream == null) {
         return properties;
      }

      try {
         properties.loadFromXML (configStream);
      }
      catch (final IOException ignored1) {
         try {
            configStream.close ();
         }
         catch (final IOException ignored2) {
            return properties;
         }
         return properties;
      }

      return properties;
   }

   private static void setFieldValue (final Field field,
                                      final Object instance,
                                      final Object value)
   {
      try {
         field.setAccessible (true);
         field.set (instance, value);
         field.setAccessible (false);
      }
      catch (final IllegalAccessException cause) {
         throw new IllegalStateException (cause);
      }
   }
}
