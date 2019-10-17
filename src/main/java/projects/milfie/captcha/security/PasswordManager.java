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

package projects.milfie.captcha.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * Hash passwords for storage, and test passwords against password tokens.
 * <p>
 * Instances of this class can be used concurrently by multiple threads.
 *
 * @author erickson
 * @author Milfie <mail@milfie.uu.me>
 * @see <a href="http://stackoverflow.com/a/2861125/3474">StackOverflow</a>
 * @see <a href="https://www.codeproject.com/Articles/704865/Salted-Password-Hashing-Doing-it-Right">Salted Password Hashing - Doing it Right</a>
 */
public class PasswordManager {

   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static boolean isEqualPasswords (final char[] first,
                                           final char[] second)
   {
      if (first == null || second == null) {
         throw new IllegalArgumentException ("Got null argument.");
      }

      final int length = Math.min (first.length, second.length);

      int result = first.length ^ second.length;

      for (int i = 0; i < length; ++i) {
         result |= first[i] ^ second[i];
      }

      return (result == 0);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   /**
    * Create a password manager with default cost.
    */
   public PasswordManager () {
      this (DEFAULT_COST);
   }

   /**
    * Create a password manager with a specified cost.
    *
    * @param cost the exponential computational cost of hashing a password, 0
    *             to 30
    */
   public PasswordManager (final int cost) {
      getIterations (cost);
      this.cost = cost;
      this.random = new SecureRandom ();
   }

   /**
    * Hash a password for storage.
    * <p>
    * NOTE: Given password will be zeroed after this operation.
    *
    * @return a secure authentication token to be stored for later
    *         authentication
    */
   public String createToken (final char[] password) {
      final byte[] head = new byte[]{(byte) cost};
      final byte[] salt = new byte[SIZE / Byte.SIZE];

      random.nextBytes (salt);

      final byte[] hash = createHash (password, salt, getIterations (cost));
      final byte[] body = new byte[salt.length + hash.length];

      System.arraycopy (salt, 0, body, 0, salt.length);
      System.arraycopy (hash, 0, body, salt.length, hash.length);

      return
         TOKEN_HEADER +
         DatatypeConverter.printHexBinary (head) +
         DatatypeConverter.printHexBinary (body);
   }

   /**
    * Checks if the given string is a token.
    *
    * @return true if string starts with token header and has enough capacity,
    *         false otherwise.
    */
   public boolean isToken (final String token) {
      return
         (token != null &&
          token.length () == TOKEN_SIZE &&
          token.startsWith (TOKEN_HEADER));
   }

   /**
    * Checks whether the given token belongs to the given password.
    * <p>
    * NOTE: Given password will be zeroed after this operation.
    *
    * @return true if the password and token match
    */
   public boolean isBelongs (final String token,
                             final char[] password)
   {
      final Matcher matcher = TOKEN_REGEX.matcher (token);

      if (!matcher.matches ()) {
         throw new IllegalArgumentException ("Incorrect token format.");
      }

      final int iterations = getIterations
         (DatatypeConverter.parseHexBinary (matcher.group (1))[0]);
      final byte[] hash = DatatypeConverter
         .parseHexBinary (matcher.group (2));
      final byte[] salt = Arrays.copyOfRange (hash, 0, SIZE / Byte.SIZE);
      final byte[] check = createHash (password, salt, iterations);

      int zero = 0;

      for (int i = 0, j = salt.length; i < check.length; ++i, ++j) {
         zero |= hash[j] ^ check[i];
      }

      return (zero == 0);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final SecureRandom random;
   private final int          cost;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String ALGORITHM    = "PBKDF2WithHmacSHA1";
   private static final int    DEFAULT_COST = 4/*16*/;
   private static final int    SIZE         = 128;
   private static final int    HEAD_SIZE    = 2;
   private static final int    BODY_SIZE    = 4 * SIZE / Byte.SIZE;

   private static final String  TOKEN_HEADER = "JOJO";
   private static final int     TOKEN_SIZE   =
      TOKEN_HEADER.length () + HEAD_SIZE + BODY_SIZE;
   private static final Pattern TOKEN_REGEX  = Pattern.compile
      ('^' +
       TOKEN_HEADER +
       "([0-9]{" + HEAD_SIZE + "})" +
       "([0-9A-F]{" + BODY_SIZE + "})" +
       '$');

   private static int getIterations (final int cost) {
      if (cost < 0 || cost > 30) {
         throw new IllegalArgumentException
            ("Given cost [" + cost + "] is out of range [0:30].");
      }

      return (1 << cost);
   }

   private static byte[] createHash (final char[] password,
                                     final byte[] salt,
                                     final int iterations)
   {
      final byte[] hash;
      final char[] block = pack (password, salt);
      final PBEKeySpec spec = new PBEKeySpec (block, salt, iterations, SIZE);

      try {
         hash =
            SecretKeyFactory
               .getInstance (ALGORITHM)
               .generateSecret (spec)
               .getEncoded ();
      }
      catch (final NoSuchAlgorithmException e) {
         throw new IllegalStateException
            ("Algorithm \"" + ALGORITHM + "\" not found.", e);
      }
      catch (final InvalidKeySpecException e) {
         throw new IllegalStateException
            ("Invalid secret key specification.", e);
      }
      finally {
         spec.clearPassword ();
         Arrays.fill (password, '0');
         Arrays.fill (block, '0');
      }

      return hash;
   }

   private static char[] pack (final char[] password,
                               final byte[] salt)
   {
      final char[] result = new char[password.length + salt.length / 2];

      System.arraycopy (password, 0, result, 0, password.length);

      for (int i = password.length, j = 0; i < result.length; ++i) {
         result[i] = (char) ((salt[j++] << Byte.SIZE) & salt[j++]);
      }

      return result;
   }
}
