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

final class Pen {

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public Pen (final Canvas canvas) {
      if (canvas == null) {
         throw new IllegalArgumentException ("Canvas is null.");
      }
      this.canvas = canvas;
   }

   public void draw (final Pair p) {
      assert (p.x >= 0) : "The p.x coordinate is negative!";
      assert (p.y >= 0) : "The p.y coordinate is negative!";

      final int rowLen = 2;
      final int colLen = 2;

      for (int c = 0; c < colLen; ++c) {
         for (int r = 0; r < rowLen; ++r) {
            if (brush[c][r]) {
               canvas.write (p.x + r, p.y + c, brush[c][r]);
            }
         }
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final Canvas canvas;

   private final boolean[][] brush = {
      {true, false, false},
      {false, true, false},
      {false, false, true},
   };
}
