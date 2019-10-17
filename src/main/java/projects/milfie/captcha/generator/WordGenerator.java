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

package projects.milfie.captcha.generator;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

///////////////////////////////////////////////////////////////////////////////
//  Purpose:
//     Generates a random english pseudoword by internal grammar rules.
final class WordGenerator {

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public WordGenerator () {
      randomSrc  = new Random ();
      keyPattern = Pattern.compile (Grammar.getKeyPattern ());
   }

   public String generate () {
      return expandRule (Grammar.getInitialRule ());
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final Random  randomSrc;
   private final Pattern keyPattern;

   private String expandRule (final String rule) {
      final StringBuffer buffer = new StringBuffer ();
      final Matcher matcher = keyPattern.matcher (rule);

      while (matcher.find ()) {
         matcher.appendReplacement
            (buffer, expandRule (getRandomRule (matcher.group (1))));
      }

      matcher.appendTail (buffer);

      return buffer.toString ();
   }

   private String getRandomRule (final String key) {
      final String[] rules = Grammar.getRules (key);
      final int index = randomSrc.nextInt (rules.length);
      return rules[index];
   }
}
