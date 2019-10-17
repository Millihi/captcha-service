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

public class ImageGeneratorTest {

   public ImageGeneratorTest () {
      generator = new ImageGenerator ();
   }

   @Test
   public void testGenerate () throws Exception {
      for (int i = 0; i < PROBE_COUNT; i++) {
         for (final String str : TEST_STRINGS) {
            generator.generate (str);
         }
      }
   }

   private final ImageGenerator generator;

   private static final int      PROBE_COUNT  = 1000;
   private static final String[] TEST_STRINGS = {
      "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
      "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
      "the", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog",
      "pack", "my", "box", "with", "five", "dozen", "liquor", "jugs"
   };
}
