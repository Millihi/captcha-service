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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Singleton;

import static projects.milfie.captcha.security.Configuration.AuthType;

@Singleton
public class ServerAuthModuleFactory {
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public <T> T getInstance (final Class<T> instanceClass) {
      final Object instance = instances.get (instanceClass);

      if (instance == null) {
         throw new IllegalStateException
            ("Instance for class " + instanceClass + " not found.");
      }

      return instanceClass.cast (instance);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final Map<Class<?>, Object> instances = new HashMap<> ();

   @PostConstruct
   private void initialize () {
      instances.put
         (Configuration.class,
          CDI.current ().select (Configuration.class).get ());

      for (final AuthType authType : AuthType.values ()) {
         instances.put
            (authType.getModuleClass (),
             CDI.current ().select (authType.getModuleClass ()).get ());
      }
   }
}
