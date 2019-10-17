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

import static org.junit.Assert.assertTrue;

public class CanvasTest {

   public CanvasTest () {
      this.canvas = new Canvas ();
   }

   @Test
   public void testCanvas () throws Exception {
      fill (canvas, true);
      assertTrue
         ("The canvas isn't filled!",
          isFilled (canvas, true));

      fill (canvas, false);
      assertTrue
         ("The canvas isn't filled!",
          isFilled (canvas, false));

      final int expectedWidth = 3 * canvas.getWidth () / 2;
      final int expectedHeight = 4 * canvas.getHeight () / 3;

      final int x = expectedWidth - 1;
      final int y = expectedHeight - 1;
      boolean value = true;

      canvas.write (x, y, value);

      assertTrue
         ("The canvas width is incorrect!",
          canvas.getWidth () == expectedWidth);
      assertTrue
         ("The canvas height is incorrect!",
          canvas.getHeight () == expectedHeight);
      assertTrue
         ("Couldn't read the value!",
          canvas.read (x, y) == value);

      value = false;
      canvas.write (x, y, value);

      assertTrue
         ("The canvas width is incorrect!",
          canvas.getWidth () == expectedWidth);
      assertTrue
         ("The canvas height is incorrect!",
          canvas.getHeight () == expectedHeight);
      assertTrue
         ("Couldn't read the value!",
          canvas.read (x, y) == value);
      assertTrue
         ("The canvas isn't filled!",
          isFilled (canvas, false));
   }

   @Test
   public void testSetWidth () throws Exception {
      fill (canvas, false);

      final int oldWidth = canvas.getWidth ();
      final int newWidth = 2 * oldWidth;

      canvas.setWidth (newWidth);
      assertTrue
         ("The canvas width is incorrect!",
          canvas.getWidth () == newWidth);
      assertTrue
         ("The canvas width isn't growed!",
          isFilled (canvas, false));

      canvas.setWidth (oldWidth);
      assertTrue
         ("The canvas width is incorrect!",
          canvas.getWidth () == oldWidth);
      assertTrue
         ("The canvas width isn't shrinked!",
          isFilled (canvas, false));
   }

   @Test
   public void testSetHeight () throws Exception {
      fill (canvas, false);

      final int oldHeight = canvas.getHeight ();
      final int newHeight = 2 * oldHeight;

      canvas.setHeight (newHeight);
      assertTrue
         ("The canvas height is incorrect!",
          canvas.getHeight () == newHeight);
      assertTrue
         ("The canvas height isn't growed!",
          isFilled (canvas, false));

      canvas.setHeight (oldHeight);
      assertTrue
         ("The canvas height is incorrect!",
          canvas.getHeight () == oldHeight);
      assertTrue
         ("The canvas height isn't shrinked!",
          isFilled (canvas, false));
   }

   @Test
   public void testClear () throws Exception {
      fill (canvas, true);

      canvas.clear ();
      assertTrue
         ("The canvas isn't cleared!",
          isFilled (canvas, false));
   }

   private final Canvas canvas;

   private static void fill (final Canvas canvas,
                             final boolean value)
   {
      final int height = canvas.getHeight ();
      final int width = canvas.getWidth ();

      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            canvas.write (x, y, value);
         }
      }
   }

   private static boolean isFilled (final Canvas canvas,
                                    final boolean value)
   {
      final int height = canvas.getHeight ();
      final int width = canvas.getWidth ();

      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            if (canvas.read (x, y) != value) {
               return false;
            }
         }
      }

      return true;
   }
}
