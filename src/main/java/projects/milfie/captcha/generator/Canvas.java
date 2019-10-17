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

import java.util.Arrays;

final class Canvas {

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public Canvas () {
      this.canvas = new boolean[height][width];
      this.clear ();
   }

   public int getWidth () {
      return width;
   }

   public void setWidth (final int width) {
      assert (width > 0) : "The width is not positive.";
      ensureCapacity (width, this.height);
      this.width = width;
   }

   public int getHeight () {
      return height;
   }

   public void setHeight (final int height) {
      assert (height > 0) : "The height is not positive.";
      ensureCapacity (this.width, height);
      this.height = height;
   }

   public boolean read (final int x, final int y) {
      assert (x >= 0 && x < width) : "The x coordinate is out of range.";
      assert (y >= 0 && y < height) : "The y coordinate is out of range.";
      return canvas[y][x];
   }

   public void write (final int x, final int y, final boolean value) {
      assert (x >= 0 && y >= 0) : "The coordinate is negative.";
      ensureCapacity (x + 1, y + 1);
      canvas[y][x] = value;
   }

   public void clear () {
      for (int y = 0, len = canvas.length; y < len; ++y) {
         Arrays.fill (canvas[y], DEFAULT_VALUE);
      }
   }

   @Override
   public String toString () {
      final StringBuilder builder = new StringBuilder ();

      for (int y = 0; y < height; ++y) {
         for (int x = 0; x < width; ++x) {
            builder.append ((canvas[y][x] ? '@' : '.'));
         }
         builder.append ('\n');
      }
      return builder.toString ();
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private int width  = DEFAULT_SIZE;
   private int height = DEFAULT_SIZE;
   private boolean[][] canvas;

   private void ensureCapacity (final int desiredWidth,
                                final int desiredHeight)
   {
      if (this.width < desiredWidth) {
         correctHorizontalCapacity (desiredWidth);
         this.width = desiredWidth;
      }

      if (this.height < desiredHeight) {
         correctVerticalCapacity (desiredHeight);
         this.height = desiredHeight;
      }
   }

   private void correctHorizontalCapacity (final int desired) {
      final int available = canvas[0].length;

      if (available < desired) {
         final int suggested = findNewSize (available, desired);

         for (int y = 0, len = canvas.length; y < len; ++y) {
            canvas[y] = Arrays.copyOf (canvas[y], suggested);
            Arrays.fill (canvas[y], width, desired, DEFAULT_VALUE);
         }
      }
      else {
         for (int y = 0; y < height; ++y) {
            Arrays.fill (canvas[y], width, desired, DEFAULT_VALUE);
         }
      }
   }

   private void correctVerticalCapacity (final int desired) {
      final int available = canvas.length;

      if (available < desired) {
         final int suggested = findNewSize (available, desired);
         final int rowCapacity = canvas[0].length;

         canvas = Arrays.copyOf (canvas, suggested);

         for (int y = height; y < suggested; ++y) {
            canvas[y] = new boolean[rowCapacity];
            Arrays.fill (canvas[y], 0, width, DEFAULT_VALUE);
         }
      }
      else {
         for (int y = height; y < desired; ++y) {
            Arrays.fill (canvas[y], 0, width, DEFAULT_VALUE);
         }
      }
   }

   private static int findNewSize (final int reference,
                                   final int desired)
   {
      int size = reference;

      while (size < desired) {
         if (Integer.MAX_VALUE - size < size) {
            throw new IllegalStateException ("Storage overflow.");
         }
         size += size;
      }

      return size;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final int     DEFAULT_SIZE  = 16;
   private static final boolean DEFAULT_VALUE = false;
}
