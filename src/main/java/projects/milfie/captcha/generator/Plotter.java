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

final class Plotter {

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public Plotter (final Canvas canvas,
                   final Pen pen)
   {
      if (canvas == null) {
         throw new IllegalArgumentException ("Canvas is null.");
      }
      if (pen == null) {
         throw new IllegalArgumentException ("Pen is null.");
      }
      this.canvas = canvas;
      this.pen = pen;
   }

   public Pair getPos () {
      return pos;
   }

   public void setPos (final Pair p) {
      setXPos (p.x);
      setYPos (p.y);
   }

   public int getXPos () {
      return pos.x;
   }

   public void setXPos (final int value) {
      assert (value >= 0) : "The value (" + value + ") is negative!";
      pos.x = value;
   }

   public int getYPos () {
      return pos.y;
   }

   public void setYPos (final int value) {
      assert (value >= 0) : "The value (" + value + ") is negative!";
      pos.y = value;
   }

   public void reset () {
      setPos (Pair.ZERO);
   }

   public void move (final Pair d) {
      moveByX (d.x);
      moveByY (d.y);
   }

   public void moveByX (final int dx) {
      pos.x += dx;
      assert (pos.x >= 0) : "The pos.x (" + pos.x + ") is negative!";
   }

   public void moveByY (final int dy) {
      pos.y += dy;
      assert (pos.y >= 0) : "The pos.y (" + pos.y + ") is negative!";
   }

   public void drawLine (final Pair p0, final Pair p1) {
      setPos (p0);
      drawLine (Pair.subtract (p1, p0));
   }

   public void drawLine (final Pair d) {
      assert (pos.x + d.x >= 0)
         : "The x (" + (pos.x + d.x) + ") coordinate is negative!";
      assert (pos.y + d.y >= 0)
         : "The y (" + (pos.y + d.y) + ") coordinate is negative!";

      final int xOld = pos.x;
      final int yOld = pos.y;

      final int xInc = (d.x < 0 ? -1 : 1);
      final int yInc = (d.y < 0 ? -1 : 1);

      final int xLen = Math.abs (d.x);
      final int yLen = Math.abs (d.y);

      final int xLen2 = xLen + xLen;
      final int yLen2 = yLen + yLen;

      if (xLen >= yLen) {
         int error = yLen2 - xLen;

         for (int i = 0; i < xLen; ++i) {
            pen.draw (pos);

            if (error > 0) {
               pos.y += yInc;
               error -= xLen2;
            }
            error += yLen2;
            pos.x += xInc;
         }
      }
      else {
         int error = xLen2 - yLen;

         for (int i = 0; i < yLen; ++i) {
            pen.draw (pos);

            if (error > 0) {
               pos.x += xInc;
               error -= yLen2;
            }
            error += xLen2;
            pos.y += yInc;
         }
      }

      pen.draw (pos);

      assert (pos.x == xOld + d.x)
         : "Wrong positioning by x: dx = " + (pos.x - xOld + d.x) + "!";
      assert (pos.y == yOld + d.y)
         : "Wrong positioning by y: dy = " + (pos.y - yOld + d.y) + "!";
   }

   public void drawDot (final Pair p) {
      setPos (p);
      drawDot ();
   }

   public void drawDot () {
      pen.draw (pos);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final Canvas canvas;
   private final Pen    pen;

   private Pair pos = new Pair (Pair.ZERO);
}
