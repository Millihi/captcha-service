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

import javax.security.auth.message.module.ServerAuthModule;

public enum AuthTypeSpec {
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   BASIC
      (BasicServerAuthModule.class,
       BasicServerAuthModuleConfig.class),
   FORM
      (FormServerAuthModule.class,
       FormServerAuthModuleConfig.class),
   NONE
      (DummyServerAuthModule.class);

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public Class<? extends ServerAuthModule> getModuleClass () {
      return moduleClass;
   }

   public boolean hasConfig () {
      return (configClass != null);
   }

   public Class<? extends AppServerAuthModuleConfig> getConfigClass () {
      return configClass;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private AuthTypeSpec
      (final Class<? extends ServerAuthModule> moduleClass)
   {
      this.moduleClass = moduleClass;
      this.configClass = null;
   }

   private AuthTypeSpec
      (final Class<? extends ServerAuthModule> moduleClass,
       final Class<? extends AppServerAuthModuleConfig> configClass)
   {
      this.moduleClass = moduleClass;
      this.configClass = configClass;
   }

   private final Class<? extends ServerAuthModule>          moduleClass;
   private final Class<? extends AppServerAuthModuleConfig> configClass;
}
