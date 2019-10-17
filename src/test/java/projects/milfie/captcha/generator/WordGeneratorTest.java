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

import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertFalse;

public class WordGeneratorTest {

   public WordGeneratorTest () {
      this.words = new TreeSet<> ();
      this.generator = new WordGenerator ();
   }

   @Test
   public void testGenerate () throws Exception {
      for (int i = 0; i < PROBE_COUNT; i++) {
         final String word = generator.generate ();

         assertFalse ("The word is null!", word == null);
         assertFalse ("The word is empty!", word.isEmpty ());

         words.add (word);
      }

      assertFalse
         ("Too many duplicates!",
          words.size () < (PROBE_COUNT / 4) * 3);
   }

   private final Set<String>   words;
   private final WordGenerator generator;

   private static final int PROBE_COUNT = 1000;
}
