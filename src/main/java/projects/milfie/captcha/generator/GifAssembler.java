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

final class GifAssembler {

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public GifAssembler (final Canvas canvas) {
      if (canvas == null) {
         throw new IllegalArgumentException ("Null argument.");
      }
      this.canvas = canvas;
      this.reset ();
   }

   public GifAssembler reset () {
      blockSize = 0;
      blockPos = 0;
      imagePos = 0;
      return this;
   }

   public byte[] assembly () {
      reset ();

      final int imageSize =
         findImageSize (canvas.getWidth (), canvas.getHeight ());
      imageBuf = new byte[imageSize];

      writeGifHeader ();
      writeCanvas ();
      writeFooter ();

      assert (imagePos == imageBuf.length)
         : "Incorrect buffer size: written " + imagePos +
           " bytes, but buffer has " + imageBuf.length + " bytes.";

      return imageBuf;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final Canvas canvas;

   private byte[] imageBuf;
   private int imagePos  = 0;
   private int blockPos  = 0;
   private int blockSize = 0;

   private void writeGifHeader () {
      writeTitle ();
      writeLogicScreenDescriptor ();
      writeGlobalPalette ();
      writeGraphicControlExtension ();
      writeImageDescriptor ();
   }

   private void writeCanvas () {
      final int width = canvas.getWidth ();
      final int height = canvas.getHeight ();

      int pixelCount = 0;

      // Inital LZW-code size
      imageBuf[imagePos++] = (byte) 0x07;

      for (int y = 0; y < height; ++y) {
         for (int x = 0; x < width; ++x) {
            if (pixelCount == 0) {
               writeGraphicByte (0x80);
            }

            writeGraphicByte (canvas.read (x, y) ? 1 : 0);

            pixelCount++;

            if (pixelCount >= 126) {
               pixelCount = 0;
            }
         }
      }

      // EOI = number_of_colors + 1 = 128 + 1 = 129 = 0x81
      writeGraphicByte (0x81);

      if (blockSize > 0) {
         imageBuf[blockPos] = (byte) blockSize;
         blockSize = 0;
      }

      // Graphic block terminator
      imageBuf[imagePos++] = (byte) 0x00;
   }

   private void writeFooter () {
      imageBuf[imagePos++] = (byte) ';';
   }

   private void writeTitle () {
      for (int i = 0, len = GIF89A_TITLE.length; i < len; ++i) {
         imageBuf[imagePos++] = GIF89A_TITLE[i];
      }
   }

   private void writeLogicScreenDescriptor () {
      final short width = (short) canvas.getWidth ();
      imageBuf[imagePos++] = (byte) (width & 0xFF);
      imageBuf[imagePos++] = (byte) (width >> Byte.SIZE);

      final short height = (short) canvas.getHeight ();
      imageBuf[imagePos++] = (byte) (height & 0xFF);
      imageBuf[imagePos++] = (byte) (height >> Byte.SIZE);

      imageBuf[imagePos++] = (byte) 0xA6;
      imageBuf[imagePos++] = (byte) 0x00;
      imageBuf[imagePos++] = (byte) 0x00;
   }

   private void writeGlobalPalette () {
      for (int i = 0; i < 128; i+=2) {
         imageBuf[imagePos++] = (byte) 255;
         imageBuf[imagePos++] = (byte) 255;
         imageBuf[imagePos++] = (byte) 255;

         imageBuf[imagePos++] = (byte) 128;
         imageBuf[imagePos++] = (byte) 0;
         imageBuf[imagePos++] = (byte) 0;
      }
   }

   private void writeImageDescriptor () {
      imageBuf[imagePos++] = (byte) ',';

      imageBuf[imagePos++] = (byte) 0x00;
      imageBuf[imagePos++] = (byte) 0x00;

      imageBuf[imagePos++] = (byte) 0x00;
      imageBuf[imagePos++] = (byte) 0x00;

      final short width = (short) canvas.getWidth ();
      imageBuf[imagePos++] = (byte) (width & 0xFF);
      imageBuf[imagePos++] = (byte) (width >> Byte.SIZE);

      final short height = (short) canvas.getHeight ();
      imageBuf[imagePos++] = (byte) (height & 0xFF);
      imageBuf[imagePos++] = (byte) (height >> Byte.SIZE);

      imageBuf[imagePos++] = (byte) 0x00;
   }

   private void writeGraphicControlExtension () {
      imageBuf[imagePos++] = (byte) '!';
      imageBuf[imagePos++] = (byte) 0xF9;
      imageBuf[imagePos++] = (byte) 0x04;

      imageBuf[imagePos++] = (byte) 0x01;

      imageBuf[imagePos++] = (byte) 0x00;
      imageBuf[imagePos++] = (byte) 0x00;

      imageBuf[imagePos++] = (byte) 0x00;
      imageBuf[imagePos++] = (byte) 0x00;
   }

   private void writeGraphicByte (final int b) {
      assert ((b >> Byte.SIZE) == 0)
         : "The byte value (" + b + ") is out of range [0, 255].";

      // Start new graphic subblock
      if (blockSize <= 0) {
         blockSize = 0;
         blockPos = imagePos;
         imageBuf[imagePos++] = 0;
      }

      imageBuf[imagePos++] = (byte) b;
      blockSize++;

      if (blockSize >= BLOCK_SIZE) {
         imageBuf[blockPos] = (byte) BLOCK_SIZE;
         blockSize = 0;
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final byte[] GIF89A_TITLE = new byte[]{
      'G', 'I', 'F', '8', '9', 'a'
   };

   private static final int BLOCK_SIZE = 255;
   private static final int TITLE_SIZE = GIF89A_TITLE.length;
   private static final int LSD_SIZE   = 7;
   private static final int GP_SIZE    = 128 * 3;
   private static final int ID_SIZE    = 10;
   private static final int GCE_SIZE   = 8;

   private static int findImageSize (final int width,
                                     final int height)
   {
      assert (width > 0 && width < 65536)
         : "The width (" + width + ") is out of range [1, 65535].";
      assert (height > 0 && height < 65536)
         : "The height (" + height + ") is out of range [1, 65535].";

      final int headerSize =
         TITLE_SIZE + LSD_SIZE + GP_SIZE + ID_SIZE + GCE_SIZE;
      final int canvasSize = width * height;
      final int codeSize =
         canvasSize + canvasSize / 126 + ((canvasSize % 126) == 0 ? 0 : 1);
      final int imageSize =
         1 + codeSize + codeSize / BLOCK_SIZE +
         ((codeSize % BLOCK_SIZE) == 0 ? 0 : 1) + 2;
      final int footerSize = 1;

      return (headerSize + imageSize + footerSize);
   }
}
