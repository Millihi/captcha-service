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
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.inject.Singleton;
import javax.security.auth.message.module.ServerAuthModule;

@Singleton
public final class Configuration {

   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static final String ATTR_KEY_IS_POST_AUTH =
      "projects.milfie.captcha.security.isPostAuth";

   public static final String SESSION_KEY_REDIRECT_TO =
      "projects.milfie.captcha.security.redirectTo";

   public enum AuthType {
      BASIC
         (BasicAppServerAuthModule.class,
          Schema.BASIC_AUTH_ENABLED,
          Schema.BASIC_AUTH_RESOURCES),
      FORM
         (FormAppServerAuthModule.class,
          Schema.FORM_AUTH_ENABLED,
          Schema.FORM_AUTH_RESOURCES),
      NONE
         (DummyAppServerAuthModule.class, null, null);

      /////////////////////////////////////////////////////////////////////////
      //  Public section                                                     //
      /////////////////////////////////////////////////////////////////////////

      public Class<? extends ServerAuthModule> getModuleClass () {
         return moduleClass;
      }

      /////////////////////////////////////////////////////////////////////////
      //  Private section                                                    //
      /////////////////////////////////////////////////////////////////////////

      private AuthType (final Class<? extends ServerAuthModule> moduleClass,
                        final Schema schemaEnabled,
                        final Schema schemaResources)
      {
         this.moduleClass = moduleClass;
         this.schemaEnabled = schemaEnabled;
         this.schemaResources = schemaResources;
      }

      private final Class<? extends ServerAuthModule> moduleClass;
      private final Schema                            schemaEnabled;
      private final Schema                            schemaResources;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public Configuration () {
      this (DEFAULT_CONFIG_FILE);
   }

   public Configuration (final String configFile) {
      if (configFile == null || configFile.isEmpty ()) {
         throw new IllegalArgumentException ("Given config file is empty.");
      }

      this.configFile = configFile;
      this.resourceMap = new HashMap<> ();
      this.loadFromFile ();
   }

   public String getConfigFile () {
      return configFile;
   }

   public String[] getExcludedFromAuthResources () {
      return excludedFromAuthResources;
   }

   public boolean isBasicAuthEnabled () {
      return basicAuthEnabled;
   }

   public boolean isBasicAuthStateful () {
      return basicAuthStateful;
   }

   public String[] getBasicAuthResources () {
      return basicAuthResources;
   }

   public String getDeclaredBasicResource (final String path) {
      return getMostSpecificDeclaredResource (path, basicAuthResources);
   }

   public boolean isInBasicAuthResources (final String path) {
      return (getAuthType (path) == AuthType.BASIC);
   }

   public boolean isFormAuthEnabled () {
      return formAuthEnabled;
   }

   public boolean isFormAuthStateful () {
      return formAuthStateful;
   }

   public String[] getFormAuthResources () {
      return formAuthResources;
   }

   public String getDeclaredFormResource (final String path) {
      return getMostSpecificDeclaredResource (path, formAuthResources);
   }

   public boolean isInFormAuthResources (final String path) {
      return (getAuthType (path) == AuthType.FORM);
   }

   public String getFormAuthLoginPage () {
      return formAuthLoginPage;
   }

   public String getFormAuthErrorPage () {
      return formAuthErrorPage;
   }

   public String getFormAuthAction () {
      return formAuthAction;
   }

   public String getFormAuthUsernameField () {
      return formAuthUsernameField;
   }

   public String getFormAuthPasswordField () {
      return formAuthPasswordField;
   }

   public AuthType getAuthType (final String path) {
      return
         resourceMap.get
            (getMostSpecificDeclaredResource
                (validateResource (path), resourceMap.keySet ()));
   }

   public void reset () {
      loadDefaults ();
   }

   public void reload () {
      loadFromFile ();
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final String                configFile;
   private final Map<String, AuthType> resourceMap;

   private String[] excludedFromAuthResources;
   private boolean  basicAuthEnabled;
   private boolean  basicAuthStateful;
   private String[] basicAuthResources;
   private boolean  formAuthEnabled;
   private boolean  formAuthStateful;
   private String[] formAuthResources;
   private String   formAuthLoginPage;
   private String   formAuthErrorPage;
   private String   formAuthAction;
   private String   formAuthUsernameField;
   private String   formAuthPasswordField;

   private void loadDefaults () {
      for (final Schema key : Schema.values ()) {
         final Object value = key.converter.convert (key.defaultValue);
         try {
            key.field.set (this, value);
         }
         catch (final IllegalAccessException e) {
            throw (IllegalStateException)
               new IllegalStateException ().initCause (e);
         }
      }

      loadResourceMap ();
   }

   private void loadFromFile () {
      final Properties p = loadProperties (configFile);

      for (final Schema key : Schema.values ()) {
         final Object value = key.converter.convert
            (p.getProperty (key.name, key.defaultValue));
         try {
            key.field.set (this, value);
         }
         catch (final IllegalAccessException e) {
            throw (IllegalStateException)
               new IllegalStateException ().initCause (e);
         }
      }

      loadResourceMap ();
   }

   private void loadResourceMap () {
      resourceMap.clear ();

      for (final AuthType type : AuthType.values ()) {
         if (type.schemaEnabled != null) {
            final boolean enabled;
            final String[] resources;

            try {
               enabled = (boolean) type.schemaEnabled.field.get (this);
               resources = (String[]) type.schemaResources.field.get (this);
            }
            catch (final IllegalAccessException cause) {
               throw new IllegalStateException (cause);
            }

            for (final String resource : resources) {
               final AuthType oldType =
                  resourceMap.get (validateResource (resource));

               if (oldType != null) {
                  throw new IllegalStateException
                     ("The resource [" + resource + "] " +
                      "already has declared auth type [" + oldType + "]");
               }

               resourceMap.put (resource, (enabled ? type : AuthType.NONE));
            }
         }
      }

      for (final String excluded : excludedFromAuthResources) {
         resourceMap.put (validateResource (excluded), AuthType.NONE);
      }

      if (resourceMap.get ("/") == null) {
         resourceMap.put ("/", AuthType.NONE);
      }

      LOGGER.info ("Mapped resources:");

      for (final Map.Entry<String, AuthType> entry : resourceMap.entrySet ()) {
         LOGGER.info
            ("     " + entry.getKey () + " as " +
             entry.getValue ().getClass ().getSimpleName () + '.' +
             entry.getValue ());
      }
   }

   private String validateResource (final String resource) {
      if (resource == null || resource.isEmpty ()) {
         throw new IllegalArgumentException ("Given resource is null.");
      }
      if (resource.charAt (0) != '/') {
         throw new IllegalArgumentException
            ("The resource [" + resource + "] does not starts with '/'");
      }
      return resource;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String DEFAULT_CONFIG_FILE = "sam-config.xml";

   private static final Logger LOGGER =
      Logger.getLogger (Configuration.class.getName ());

   private enum Schema {
      EXCLUDED_FROM_AUTH_RESOURCES
         ("excludedFromAuthResources", Converter.STRING_ARRAY, ""),
      BASIC_AUTH_ENABLED
         ("basicAuthEnabled", Converter.BOOLEAN, "false"),
      BASIC_AUTH_STATEFUL
         ("basicAuthStateful", Converter.BOOLEAN, "false"),
      BASIC_AUTH_RESOURCES
         ("basicAuthResources", Converter.STRING_ARRAY, "/"),
      FORM_AUTH_ENABLED
         ("formAuthEnabled", Converter.BOOLEAN, "false"),
      FORM_AUTH_STATEFUL
         ("formAuthStateful", Converter.BOOLEAN, "true"),
      FORM_AUTH_RESOURCES
         ("formAuthResources", Converter.STRING_ARRAY, "/"),
      FORM_AUTH_LOGIN_PAGE
         ("formAuthLoginPage", Converter.STRING, "login.jsp"),
      FORM_AUTH_ERROR_PAGE
         ("formAuthErrorPage", Converter.STRING, "403.jsp"),
      FORM_AUTH_FORM_ACTION
         ("formAuthAction", Converter.STRING, "j_security_check"),
      FORM_AUTH_USERNAME_FIELD
         ("formAuthUsernameField", Converter.STRING, "j_username"),
      FORM_AUTH_PASSWORD_FIELD
         ("formAuthPasswordField", Converter.STRING, "j_password");

      /////////////////////////////////////////////////////////////////////////
      //  Public section                                                     //
      /////////////////////////////////////////////////////////////////////////

      public final String    name;
      public final Field     field;
      public final Converter converter;
      public final String    defaultValue;

      /////////////////////////////////////////////////////////////////////////
      //  Private section                                                    //
      /////////////////////////////////////////////////////////////////////////

      private Schema (final String name,
                      final Converter converter,
                      final String defaultValue)
      {
         this.name = name;
         this.converter = converter;
         this.defaultValue = defaultValue;
         try {
            this.field = Configuration.class.getDeclaredField (name);
         }
         catch (final NoSuchFieldException e) {
            throw (IllegalStateException)
               new IllegalStateException ().initCause (e);
         }
      }
   }

   private enum Converter {
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

   /**
    * Finds a most specific resource, i.e. the most greater prefix in given
    * resources for the given path.
    *
    * @param path
    *    the path for searching.
    * @param resources
    *    resources for searching in.
    *
    * @return declared resource for given path or empty string otherwise.
    */
   private static String getMostSpecificDeclaredResource (
      final String path,
      final String[] resources)
   {
      String result = "/";

      if (path == null || path.isEmpty ()) {
         return result;
      }

      for (final String resource : resources) {
         if (isPrefix (path, resource) &&
             resource.length () > result.length ())
         {
            result = resource;
         }
      }

      return result;
   }

   /**
    * Finds a most specific resource, i.e. the most greater prefix in given
    * resources for the given path.
    *
    * @param path
    *    the path for searching.
    * @param resources
    *    resources for searching in.
    *
    * @return declared resource for given path or empty string otherwise.
    */
   private static String getMostSpecificDeclaredResource (
      final String path,
      final Collection<String> resources)
   {
      String result = "/";

      if (path == null || path.isEmpty ()) {
         return result;
      }

      for (final String resource : resources) {
         if (isPrefix (path, resource) &&
             resource.length () > result.length ())
         {
            result = resource;
         }
      }

      return result;
   }

   private static boolean isPrefix (final String path,
                                    final String prefix)
   {
      if (!path.startsWith (prefix)) {
         return false;
      }

      if ("/".equals (prefix)) {
         return true;
      }

      final int prefixLength = prefix.length ();

      if (path.length () == prefixLength) {
         return true;
      }

      final char nextPathChar = path.charAt (prefixLength);

      return (nextPathChar == '/' || nextPathChar == ';');
   }
}
