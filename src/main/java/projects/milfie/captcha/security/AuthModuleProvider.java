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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Singleton;
import javax.security.auth.message.module.ServerAuthModule;

@Singleton
public class AuthModuleProvider {
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public ServerAuthModule getDeclaredModule (final String path) {
      return
         ServerAuthModule.class.cast
            (instances.get (getAuthTypeSpec (path).getModuleClass ()));
   }

   public <T> T getInstance (final Class<T> instanceClass) {
      return instanceClass.cast (instances.get (instanceClass));
   }

   public AuthTypeSpec getAuthTypeSpec (final String path) {
      final Map<String, AuthTypeSpec> resources = this.resources;
      return
         resources.getOrDefault
            (getMostSpecificDeclaredResource
                (validateResource (path), resources.keySet ()),
             AuthTypeSpec.NONE);
   }

   public synchronized void refresh () {
      resources = dummyResources;
      reloadConfigModules ();
      resources = workResources;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final
   Map<Class<?>, Object>     instances      = new HashMap<> ();
   private final
   Map<String, AuthTypeSpec> dummyResources = new HashMap<> ();
   private final
   Map<String, AuthTypeSpec> workResources  = new ConcurrentHashMap<> ();
   private volatile
   Map<String, AuthTypeSpec> resources      = dummyResources;

   @PostConstruct
   private void initialize () {
      final GeneralServerAuthConfig generalConfig =
         obtainFromCDI (GeneralServerAuthConfig.class);

      generalConfig.reload ();
      instances.put (generalConfig.getClass (), generalConfig);

      for (final AuthTypeSpec authType : AuthTypeSpec.values ()) {
         final ServerAuthModule authModule =
            obtainFromCDI (authType.getModuleClass ());

         instances.put (authModule.getClass (), authModule);

         if (authType.hasConfig ()) {
            final AppServerAuthModuleConfig moduleConfig =
               obtainFromCDI (authType.getConfigClass ());

            moduleConfig.reload ();
            mapModuleResources (authType, moduleConfig, workResources);
            instances.put (moduleConfig.getClass (), moduleConfig);
         }
      }

      mapExcludedResources (generalConfig, workResources);
      mapRootResource (workResources);
      mapRootResource (dummyResources);
      logMappedResources (workResources);

      resources = workResources;
   }

   private void reloadConfigModules () {
      workResources.clear ();

      final GeneralServerAuthConfig generalConfig =
         getInstance (GeneralServerAuthConfig.class);

      generalConfig.reload ();

      for (final AuthTypeSpec authType : AuthTypeSpec.values ()) {
         if (authType.hasConfig ()) {
            final AppServerAuthModuleConfig moduleConfig =
               getInstance (authType.getConfigClass ());

            moduleConfig.reload ();
            mapModuleResources (authType, moduleConfig, workResources);
         }
      }

      mapExcludedResources (generalConfig, workResources);
      mapRootResource (workResources);
      logMappedResources (workResources);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String RESOURCE_ROOT = "/";

   private static final Logger LOGGER =
      Logger.getLogger (AuthModuleProvider.class.getName ());

   private static <T> T obtainFromCDI (final Class<T> cl) {
      return CDI.current ().select (cl).get ();
   }

   private static void mapModuleResources
      (final AuthTypeSpec spec,
       final AppServerAuthModuleConfig config,
       final Map<String, AuthTypeSpec> resources)
   {
      for (final String resource : config.getResources ()) {
         final AuthTypeSpec oldSpec =
            resources.get (validateResource (resource));

         if (oldSpec != null) {
            throw new IllegalStateException
               ("The resource [" + resource + "] " +
                "already has declared auth type [" + oldSpec + "]");
         }

         resources.put
            (resource, config.isEnabled () ? spec : AuthTypeSpec.NONE);
      }
   }

   private static void mapExcludedResources
      (final GeneralServerAuthConfig config,
       final Map<String, AuthTypeSpec> resources)
   {
      for (final String excluded : config.getExcludedResources ()) {
         resources.put (validateResource (excluded), AuthTypeSpec.NONE);
      }
   }

   private static void mapRootResource
      (final Map<String, AuthTypeSpec> resources)
   {
      if (resources.get (RESOURCE_ROOT) == null) {
         resources.put (RESOURCE_ROOT, AuthTypeSpec.NONE);
      }
   }

   private static void logMappedResources
      (final Map<String, AuthTypeSpec> resources)
   {
      LOGGER.info ("Mapped resources:");

      for (final Map.Entry<String, AuthTypeSpec> entry : resources.entrySet ())
      {
         final String resource = entry.getKey ();
         final AuthTypeSpec spec = entry.getValue ();
         LOGGER.info
            ("     " + resource + " as " +
             spec.getClass ().getSimpleName () + '.' + spec);
      }
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
      String result = RESOURCE_ROOT;

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

      if (RESOURCE_ROOT.equals (prefix)) {
         return true;
      }

      final int prefixLength = prefix.length ();

      if (path.length () == prefixLength) {
         return true;
      }

      final char nextPathChar = path.charAt (prefixLength);

      return (nextPathChar == '/' || nextPathChar == ';');
   }

   private static String validateResource (final String resource) {
      if (resource == null || resource.isEmpty ()) {
         throw
            new IllegalArgumentException
               ("Given resource is empty.");
      }
      if (resource.charAt (0) != '/') {
         throw
            new IllegalArgumentException
               ("The resource [" + resource + "] does not starts with '/'");
      }
      return resource;
   }
}
