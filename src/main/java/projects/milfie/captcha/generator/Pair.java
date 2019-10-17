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

final class Pair {

   public int x = 0;
   public int y = 0;

   public Pair () {
      super ();
   }

   public Pair (final Pair p) {
      assert (p != null) : "Pair is null.";
      this.x = p.x;
      this.y = p.y;
   }

   public Pair (final int x, final int y) {
      this.x = x;
      this.y = y;
   }

   public Pair assign (final Pair p) {
      assert (p != null) : "Pair is null.";
      return this.assign (p.x, p.y);
   }

   public Pair assign (final int x, final int y) {
      this.x = x;
      this.y = y;
      return this;
   }

   public Pair add (final Pair p) {
      assert (p != null) : "Pair is null.";
      this.x += p.x;
      this.y += p.y;
      return this;
   }

   public Pair subtract (final Pair p) {
      assert (p != null) : "Pair is null.";
      this.x -= p.x;
      this.y -= p.y;
      return this;
   }

   public Pair multiply (final Pair p) {
      assert (p != null) : "Pair is null.";
      this.x *= p.x;
      this.y *= p.y;
      return this;
   }

   public Pair divide (final Pair p) {
      assert (p != null) : "Pair is null.";
      this.x /= p.x;
      this.y /= p.y;
      return this;
   }

   public Pair restrictBottom (final Pair bound) {
      return restrictBottom (bound.x, bound.y);
   }

   public Pair restrictBottom (final int xBound, final int yBound) {
      if (this.x < xBound) {
         this.x = xBound;
      }
      if (this.y < yBound) {
         this.y = yBound;
      }
      return this;
   }

   public Pair restrictTop (final Pair bound) {
      return restrictTop (bound.x, bound.y);
   }

   public Pair restrictTop (final int xBound, final int yBound) {
      if (this.x > xBound) {
         this.x = xBound;
      }
      if (this.y > yBound) {
         this.y = yBound;
      }
      return this;
   }

   @Override
   public boolean equals (final Object obj) {
      if (obj == this) {
         return true;
      }
      if (obj == null || obj.getClass () != this.getClass ()) {
         return false;
      }

      final Pair that = (Pair) obj;

      if (this.x != that.x) {
         return false;
      }
      if (this.y != that.y) {
         return false;
      }

      return true;
   }

   @Override
   public int hashCode () {
      int hash = 0;

      hash = ((hash << 5) - hash) + this.x;
      hash = ((hash << 5) - hash) + this.y;

      return hash;
   }

   @Override
   public String toString () {
      return
         "Pair { " +
         "x = " + x + ", " +
         "y = " + y + " }";
   }

   public static final Pair ZERO = new Pair (0, 0);
   public static final Pair ONE  = new Pair (1, 1);

   public static Pair add (final Pair augend,
                           final Pair addend)
   {
      return new Pair (augend).add (addend);
   }

   public static Pair subtract (final Pair minuend,
                                final Pair subtrahend)
   {
      return new Pair (minuend).subtract (subtrahend);
   }

   public static Pair multiply (final Pair multiplicand,
                                final Pair multiplier)
   {
      return new Pair (multiplicand).multiply (multiplier);
   }

   public static Pair divide (final Pair dividend,
                              final Pair divisor)
   {
      return new Pair (dividend).divide (divisor);
   }
}
