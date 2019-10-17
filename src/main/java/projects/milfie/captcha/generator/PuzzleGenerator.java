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

import projects.milfie.captcha.domain.Puzzle;

public final class PuzzleGenerator {

   public PuzzleGenerator () {
      this.wordGenerator = new WordGenerator ();
      this.imageGenerator = new ImageGenerator ();
   }

   public Puzzle generate () {
      final String answer = wordGenerator.generate ();
      final byte[] question = imageGenerator.generate (answer);
      return new Puzzle (question, answer);
   }

   private final WordGenerator  wordGenerator;
   private final ImageGenerator imageGenerator;
}
