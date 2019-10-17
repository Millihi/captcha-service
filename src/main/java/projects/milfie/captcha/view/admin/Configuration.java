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

import projects.milfie.captcha.domain.Client;
import projects.milfie.captcha.domain.Profile;

import java.util.regex.Pattern;

class Configuration {

   static final String FLASH_KEY_CLIENT  = "client";
   static final String FLASH_KEY_PROFILE = "profile";

   static final String WELCOME_VIEW  = "/welcome";
   static final String CLIENT_VIEW   = "/admin/client";
   static final String PROFILE_VIEW  = "/admin/profile";
   static final String CONSUMER_VIEW = "/admin/consumer";
   static final String INITIAL_VIEW  = CLIENT_VIEW;

   static final Pattern CLIENT_NAME_PATTERN  =
      Pattern.compile (Client.NAME_PATTERN);
   static final Pattern PROFILE_NAME_PATTERN =
      Pattern.compile (Profile.NAME_PATTERN);

   static boolean isClientName (final String name) {
      return
         (!name.isEmpty () &&
          CLIENT_NAME_PATTERN.matcher (name).matches ());
   }

   static boolean isProfileName (final String name) {
      return
         (!name.isEmpty () &&
          PROFILE_NAME_PATTERN.matcher (name).matches ());
   }
}
