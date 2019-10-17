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

final class CoordinateTransformer {

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public CoordinateTransformer () {
      this.randomSrc = new Random ();
   }

   public int getCanvasHeight () {
      return canvasHeight;
   }

   public void setCanvasHeight (final int height) {
      if (height <= 0) {
         throw new IllegalArgumentException ("Given height is negative.");
      }
      this.canvasHeight = height;
   }

   public double getScalingFactor () {
      return scalingFactor;
   }

   public void setScalingFactor (final double factor) {
      if (factor < 0.0 || factor > 1.0) {
         throw new IllegalArgumentException ("Scaling factor out of range.");
      }
      scalingFactor = factor;
   }

   public double getRotationFactor () {
      return rotationFactor;
   }

   public void setRotationFactor (final double factor) {
      rotationFactor = factor;
   }

   public double getScribleFactor () {
      return scribleFactor;
   }

   public void setScribleFactor (final double factor) {
      scribleFactor = factor;
   }

   public double getSpacingFactor () {
      return spacingFactor;
   }

   public void setSpacingFactor (final double factor) {
      spacingFactor = factor;
   }

   public void initialize (final int width, final int height) {
      final double angle =
         rotationFactor * randomSrc.nextGaussian () * Math.PI / 2.0;
      rotSin = Math.sin (angle);
      rotCos = Math.cos (angle);

      scale = canvasHeight / height *
              (1.0 - scalingFactor * randomSrc.nextDouble ());

      centerX = width / 2.0;
      centerY = height / 2.0;

      offsetX = 0.0;
      offsetY = Math.max (0.0, canvasHeight - height * scale) *
                randomSrc.nextDouble ();
   }

   public void transformCoord (final Pair p) {
      final int x = transformXCoord (p);
      final int y = transformYCoord (p);
      p.x = x;
      p.y = y;
   }

   public int transformXCoord (final Pair p) {
      return (int) Math.round
         (offsetX + (rotCos * (p.x - centerX) -
                     rotSin * (p.y - centerY) + centerX +
                     scribleFactor * randomSrc.nextGaussian ()) * scale);
   }

   public int transformYCoord (final Pair p) {
      return (int) Math.round
         (offsetY + (rotSin * (p.x - centerX) +
                     rotCos * (p.y - centerY) + centerY +
                     scribleFactor * randomSrc.nextGaussian ()) * scale);
   }

   public int transformWidth (final int width) {
      return (int) Math.round (scale * (width + spacingFactor));
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final Random randomSrc;

   private int    canvasHeight   = 0;
   private double scalingFactor  = 1.0;
   private double rotationFactor = 0.0;
   private double scribleFactor  = 0.0;
   private double spacingFactor  = 1.0;

   private double centerX = 0.0;
   private double centerY = 0.0;
   private double offsetX = 0.0;
   private double offsetY = 0.0;
   private double scale   = 0.0;
   private double rotSin  = 0.0;
   private double rotCos  = 0.0;
}
