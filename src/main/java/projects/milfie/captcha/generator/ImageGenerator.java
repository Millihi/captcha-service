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

final class ImageGenerator {

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public ImageGenerator () {
      transformer = new CoordinateTransformer ();
      canvas = new Canvas ();
      pen = new Pen (canvas);
      plotter = new Plotter (canvas, pen);
      assembler = new GifAssembler (canvas);

      transformer.setCanvasHeight (IMAGE_HEIGHT);
      transformer.setRotationFactor (SYMBOL_ROTATION);
      transformer.setScalingFactor (SYMBOL_SCALING);
      transformer.setScribleFactor (SYMBOL_SCRIBBLE);
      transformer.setSpacingFactor (SYMBOL_SPACING);

      reset ();
   }

   public byte[] generate (final String word) {
      reset ();
      draw (word);
      return assembler.assembly ();
   }

   public String printCanvas () {
      return canvas.toString ();
   }

   public ImageGenerator reset () {
      canvas.clear ();
      canvas.setWidth (IMAGE_HEIGHT);
      canvas.setHeight (IMAGE_HEIGHT);
      return this;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final CoordinateTransformer transformer;
   private final Canvas                canvas;
   private final Pen                   pen;
   private final Plotter               plotter;
   private final GifAssembler          assembler;

   private void draw (final String str) {
      final Pair offset = new Pair (Pair.ONE);
      final Pair p0 = new Pair ();
      final Pair p1 = new Pair ();

      for (int i = 0, len = str.length (); i < len; ++i) {
         final Font font = Font.findFont (str.charAt (i));

         transformer.initialize (font.width, font.height);

         final int[][] strokes = transformCoords (font.strokes);
         final int offsetX = findXOffset (strokes);
         offset.x += offsetX;

         for (final int[] stroke : strokes) {
            int coord = 0;

            p0.assign (stroke[coord++], stroke[coord++])
              .add (offset)
              .restrictBottom (Pair.ZERO)
              .restrictTop (Integer.MAX_VALUE, IMAGE_HEIGHT - 2);
            plotter.setPos (p0);

            while (coord < stroke.length) {
               p1.assign (stroke[coord++], stroke[coord++])
                 .add (offset)
                 .restrictBottom (Pair.ZERO)
                 .restrictTop (Integer.MAX_VALUE, IMAGE_HEIGHT - 2);
               plotter.drawLine (Pair.subtract (p1, p0));
               p0.assign (p1);
            }
         }

         offset.x += findXLength (strokes) - offsetX +
                     transformer.transformWidth (0);
      }

      // TODO: Correction according to pen's width.
      canvas.setWidth (offset.x + 2);
      assert (canvas.getHeight () == IMAGE_HEIGHT)
         : "Image not fit the height.";
   }

   private int[][] transformCoords (final int[][] src) {
      final Pair point = new Pair (Pair.ZERO);
      final int[][] dst = new int[src.length][];

      for (int sr = 0, dr = 0, sl = src.length; sr < sl; ++sr, ++dr) {
         final int srl = src[sr].length;

         dst[dr] = new int[srl];

         int sc = 0, dc = 0;

         while (sc < srl) {
            point.x = src[sr][sc++];
            point.y = src[sr][sc++];
            transformer.transformCoord (point);
            dst[dr][dc++] = point.x;
            dst[dr][dc++] = point.y;
         }
      }

      return dst;
   }

   private static int findXOffset (final int[][] coords) {
      if (coords.length <= 0) {
         return 0;
      }

      int min = Integer.MAX_VALUE;

      for (int c = 0, cl = coords.length; c < cl; ++c) {
         for (int r = 0, rl = coords[c].length; r < rl; r += 2) {
            if (coords[c][r] < min) {
               min = coords[c][r];
            }
         }
      }

      return (-min);
   }

   private static int findXLength (final int[][] coords) {
      if (coords.length <= 0) {
         return 0;
      }

      int min = Integer.MAX_VALUE;
      int max = Integer.MIN_VALUE;

      for (int c = 0, cl = coords.length; c < cl; ++c) {
         for (int r = 0, rl = coords[c].length; r < rl; r += 2) {
            if (coords[c][r] < min) {
               min = coords[c][r];
            }
            if (coords[c][r] > max) {
               max = coords[c][r];
            }
         }
      }

      return Math.abs (max - min + 1);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final int    IMAGE_HEIGHT    = 32;
   private static final double SYMBOL_SCRIBBLE = 0.0;
   private static final double SYMBOL_SCALING  = 0.5;
   private static final double SYMBOL_ROTATION = 0.25;
   private static final double SYMBOL_SPACING  = 2.0;

   private enum Font {
      LETTER_A ('a', 8, new int[][]{
         new int[]{0, 4, 2, 2, 4, 2, 6, 4, 6, 10, 8, 12},
         new int[]{6, 6, 2, 6, 0, 8, 0, 10, 2, 12, 4, 12, 6, 10}
      }),
      LETTER_B ('b', 6, new int[][]{
         new int[]{0, 0, 0, 12, 4, 12, 6, 10, 6, 8, 4, 6, 0, 6}
      }),
      LETTER_C ('c', 6, new int[][]{
         new int[]{6, 12, 2, 12, 0, 10, 0, 8, 2, 6, 6, 6}
      }),
      LETTER_D ('d', 6, new int[][]{
         new int[]{6, 0, 6, 12, 2, 12, 0, 10, 0, 8, 2, 6, 6, 6}
      }),
      LETTER_E ('e', 6, new int[][]{
         new int[]{6, 12, 2, 12, 0, 10, 0, 6, 2, 4, 6, 4, 6, 6, 4, 8, 0, 8}
      }),
      LETTER_F ('f', 6, new int[][]{
         new int[]{2, 12, 2, 2, 4, 0, 6, 0},
         new int[]{0, 6, 4, 6}
      }),
      LETTER_G ('g', 6, new int[][]{
         new int[]{6, 12, 2, 12, 0, 10, 0, 8, 2, 6, 6, 6, 6, 14, 4, 16, 0, 16}
      }),
      LETTER_H ('h', 6, new int[][]{
         new int[]{0, 0, 0, 12},
         new int[]{0, 6, 4, 6, 6, 8, 6, 12}
      }),
      LETTER_I ('i', 4, new int[][]{
         new int[]{2, 6, 2, 12},
         new int[]{2, 2, 3, 3, 2, 4, 1, 3}
      }),
      LETTER_J ('j', 6, new int[][]{
         new int[]{4, 6, 4, 14, 2, 16, 0, 14},
         new int[]{4, 2, 5, 3, 4, 4, 3, 3}
      }),
      LETTER_K ('k', 6, new int[][]{
         new int[]{0, 0, 0, 12},
         new int[]{0, 8, 2, 8, 4, 4},
         new int[]{0, 8, 2, 8, 6, 12}
      }),
      LETTER_L ('l', 4, new int[][]{
         new int[]{1, 0, 1, 10, 3, 12}
      }),
      LETTER_M ('m', 8, new int[][]{
         new int[]{0, 12, 0, 6, 2, 6, 4, 8},
         new int[]{4, 12, 4, 6, 6, 6, 8, 8, 8, 12}
      }),
      LETTER_N ('n', 6, new int[][]{
         new int[]{0, 6, 0, 12},
         new int[]{0, 8, 2, 6, 4, 6, 6, 8, 6, 12}
      }),
      LETTER_O ('o', 6, new int[][]{
         new int[]{0, 8, 2, 6, 4, 6, 6, 8, 6, 10, 4, 12, 2, 12, 0, 10, 0, 8}
      }),
      LETTER_P ('p', 6, new int[][]{
         new int[]{0, 16, 0, 6, 4, 6, 6, 8, 6, 10, 4, 12, 0, 12}
      }),
      LETTER_Q ('q', 6, new int[][]{
         new int[]{6, 12, 2, 12, 0, 10, 0, 8, 2, 6, 6, 6, 6, 16},
         new int[]{4, 14, 8, 14}
      }),
      LETTER_R ('r', 6, new int[][]{
         new int[]{0, 6, 0, 12},
         new int[]{0, 8, 2, 6, 4, 6}
      }),
      LETTER_S ('s', 6, new int[][]{
         new int[]{0, 12, 4, 12, 6, 10, 0, 8, 2, 6, 6, 6}
      }),
      LETTER_T ('t', 6, new int[][]{
         new int[]{2, 2, 2, 10, 4, 12},
         new int[]{0, 6, 4, 6}
      }),
      LETTER_U ('u', 6, new int[][]{
         new int[]{0, 6, 0, 12, 4, 12, 6, 10, 6, 6}
      }),
      LETTER_V ('v', 6, new int[][]{
         new int[]{0, 6, 3, 12, 6, 6}
      }),
      LETTER_W ('w', 8, new int[][]{
         new int[]{0, 6, 0, 10, 2, 12, 4, 10, 6, 12, 8, 10, 8, 6}
      }),
      LETTER_X ('x', 6, new int[][]{
         new int[]{0, 6, 6, 12},
         new int[]{0, 12, 6, 6}
      }),
      LETTER_Y ('y', 6, new int[][]{
         new int[]{0, 6, 0, 10, 2, 12, 6, 12},
         new int[]{6, 6, 6, 14, 4, 16, 0, 16}
      }),
      LETTER_Z ('z', 6, new int[][]{
         new int[]{0, 6, 6, 6, 0, 12, 6, 12},
         new int[]{1, 9, 5, 9}
      });

      public static Font findFont (final char symbol) {
         for (final Font font : Font.values ()) {
            if (font.symbol == symbol) {
               return font;
            }
         }
         throw new IllegalArgumentException
            ("Unknown symbol [" + symbol + "] !");
      }

      public static final int WIDTH  = 8;
      public static final int HEIGHT = 16;

      private Font (final char symbol,
                    final int width,
                    final int[][] strokes)
      {
         this.symbol = symbol;
         this.width = width;
         this.height = Font.HEIGHT;
         this.strokes = strokes;
      }

      private final char    symbol;
      private final int     width;
      private final int     height;
      private final int[][] strokes;
   }
}
