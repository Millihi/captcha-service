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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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

   public Lock getReadLock () {
      return readLock;
   }

   public ServerAuthModule getModuleInstance (final String path) {
      return
         ServerAuthModule.class.cast
            (instances.get (getAuthTypeSpec (path).getModuleClass ()));
   }

   public AppServerAuthModuleConfig getConfigInstance (final String path) {
      return
         AppServerAuthModuleConfig.class.cast
            (instances.get (getAuthTypeSpec (path).getConfigClass ()));
   }

   public <T> T getInstance (final Class<T> instanceClass) {
      return instanceClass.cast (instances.get (instanceClass));
   }

   public AuthTypeSpec getAuthTypeSpec (final String path) {
      return
         resourceMap.get
            (getMostSpecificDeclaredResource
                (validateResource (path), resourceMap.keySet ()));
   }

   public void refresh () {
      writeLock.lock ();
      try {
         instances.clear ();
         resourceMap.clear ();
         initialize ();
      }
      finally {
         writeLock.unlock ();
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final Map<Class<?>, Object>     instances   = new HashMap<> ();
   private final Map<String, AuthTypeSpec> resourceMap = new HashMap<> ();

   private final
   ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock ();
   private final Lock readLock  = readWriteLock.readLock ();
   private final Lock writeLock = readWriteLock.writeLock ();

   @PostConstruct
   private void initialize () {
      final GeneralServerAuthConfig generalConfig =
         CDI.current ().select (GeneralServerAuthConfig.class).get ();

      generalConfig.reload ();
      instances.put (generalConfig.getClass (), generalConfig);

      for (final AuthTypeSpec authType : AuthTypeSpec.values ()) {
         final ServerAuthModule authModule =
            CDI.current ().select (authType.getModuleClass ()).get ();

         instances.put (authModule.getClass (), authModule);

         if (authType.hasConfig ()) {
            final AppServerAuthModuleConfig moduleConfig =
               CDI.current ().select (authType.getConfigClass ()).get ();

            moduleConfig.reload ();
            mapModuleResources (authType, moduleConfig);
            instances.put (moduleConfig.getClass (), moduleConfig);
         }
      }

      mapExcludedResources (generalConfig);
      mapRootResource ();
      logMappedResources ();
   }

   private void mapModuleResources (final AuthTypeSpec spec,
                                    final AppServerAuthModuleConfig config)
   {
      for (final String resource : config.getResources ()) {
         final AuthTypeSpec oldSpec =
            resourceMap.get (validateResource (resource));

         if (oldSpec != null) {
            throw new IllegalStateException
               ("The resource [" + resource + "] " +
                "already has declared auth type [" + oldSpec + "]");
         }

         resourceMap.put
            (resource, config.isEnabled () ? spec : AuthTypeSpec.NONE);
      }
   }

   private void mapExcludedResources (final GeneralServerAuthConfig config) {
      for (final String excluded : config.getExcludedResources ()) {
         resourceMap.put (validateResource (excluded), AuthTypeSpec.NONE);
      }
   }

   private void mapRootResource () {
      if (resourceMap.get (RESOURCE_ROOT) == null) {
         resourceMap.put (RESOURCE_ROOT, AuthTypeSpec.NONE);
      }
   }

   private void logMappedResources () {
      LOGGER.info ("Mapped resources:");

      for (final Map.Entry<String, AuthTypeSpec> entry :
         resourceMap.entrySet ())
      {
         final String resource = entry.getKey ();
         final AuthTypeSpec spec = entry.getValue ();
         LOGGER.info
            ("     " + resource + " as " +
             spec.getClass ().getSimpleName () + '.' + spec);
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String RESOURCE_ROOT = "/";

   private static final Logger LOGGER =
      Logger.getLogger (AuthModuleProvider.class.getName ());

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
