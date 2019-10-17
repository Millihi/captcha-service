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

import org.junit.Test;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PasswordManagerTest {

   public PasswordManagerTest () {
      this.manager = new PasswordManager ();
      this.random = new Random ();
      this.passwords = new TreeSet<> ();
      this.tokens = new TreeSet<> ();
   }

   @Test
   public void testIsEqualPasswords ()
      throws Exception
   {
      for (int probe = 0; probe < PROBE_COUNT; ++probe) {
         final String password0 = Long.toHexString (random.nextLong ());
         final String password1 = getAnotherRandomPasswordFor (password0);

         final char[] pch0 = password0.toCharArray ();
         final char[] pch1 = password0.toCharArray ();
         final char[] pch2 = password1.toCharArray ();

         assertTrue
            ("The same passwords aren't equal #1.",
             PasswordManager.isEqualPasswords (pch0, pch0));
         assertTrue
            ("The same passwords aren't equal #2.",
             PasswordManager.isEqualPasswords (pch0, pch1));
         assertFalse
            ("The differ passwords are equal.",
             PasswordManager.isEqualPasswords (pch1, pch2));
      }
   }

   @Test
   public void testIsToken ()
      throws Exception
   {
      for (final String token : CORRECT_TOKENS) {
         assertTrue
            ("The token [" + token + "] is not the token!",
             manager.isToken (token));
      }
      for (final String token : INCORRECT_TOKENS) {
         assertFalse
            ("The token [" + token + "] is the token!",
             manager.isToken (token));
      }
   }

   @Test
   public void testConcreteTokenCreation ()
      throws Exception
   {
      for (final String password : PASSWORDS) {
         char[] passwordChars = password.toCharArray ();
         final String token = manager.createToken (passwordChars);

         assertFalse
            ("The password [" + password + "] isn't zeroed!",
             isEquals (passwordChars, password.toCharArray ()));

         assertFalse
            ("The token is null!",
             token == null);
         assertFalse
            ("The token is empty!",
             token.isEmpty ());
         assertTrue
            ("The token [" + token + "] is not the token!",
             manager.isToken (token));

         passwordChars = password.toCharArray ();

         assertTrue
            ("The token [" + token + "] is not belongs " +
             "to the password [" + password + "]!",
             manager.isBelongs (token, passwordChars));
         assertFalse
            ("The password [" + password + "] isn't zeroed!",
             isEquals (passwordChars, password.toCharArray ()));
      }
   }

   @Test
   public void testRandomTokenCreation ()
      throws Exception
   {
      for (int probe = 0; probe < PROBE_COUNT; ++probe) {
         final String password0 = Long.toHexString (random.nextLong ());
         final String password1 = getAnotherRandomPasswordFor (password0);
         final String token = manager.createToken (password0.toCharArray ());

         passwords.add (password0);
         tokens.add (token);

         assertFalse
            ("The token is null!",
             token == null);
         assertFalse
            ("The token is empty!",
             token.isEmpty ());
         assertTrue
            ("The token [" + token + "] is not the token!",
             manager.isToken (token));
         assertTrue
            ("The token [" + token + "] is not belongs " +
             "to the password [" + password0 + "]!",
             manager.isBelongs (token, password0.toCharArray ()));
         assertFalse
            ("The token belongs to another password:\n" +
             "   token is \"" + token + "\";\n" +
             "   \"" + password0 + "\" ~ \"" + password1 + "\"!",
             manager.isBelongs (token, password1.toCharArray ()));
      }

      assertTrue
         ("Duplicate tokens found!",
          passwords.size () == tokens.size ());
   }

   private final Set<String>     passwords;
   private final Set<String>     tokens;
   private final Random          random;
   private final PasswordManager manager;

   private String getAnotherRandomPasswordFor (final String password0) {
      String password1;
      do {
         password1 = Long.toHexString (random.nextLong ());
      }
      while (password0.equals (password1));

      return password1;
   }

   private static final int PROBE_COUNT = 1000;

   private static final String[] PASSWORDS = {
      "nimda", "bewolleh", "qwerty", "12345678", "ds;,./';ll[]]ss;"
   };

   private static final String[] CORRECT_TOKENS = {
      "JOJO000000000000000000000000000000000000000000000000000000000000000000",
      "JOJO99AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA000000000000000000000FFFFFF0000",
      "JOJO01000000000FFFFFFFFFFF0000000000000AAAAAAAAAAAAAAAAAAAAAAAAAFFFFFF"
   };
   private static final String[] INCORRECT_TOKENS = {
      "JOJ0000000000000000000000000000000000000000000000000000000000000000000",
      "JOJO00000000000000000000000000000000000000000000000000000000000000000",
      "JOJO0000000000000000000000000000000000000000000000000000000000000000000"
   };

   private static boolean isEquals (final char[] first,
                                    final char[] second)
   {
      if (first.length != second.length) {
         return false;
      }

      int result = 0;

      for (int i = 0; i < first.length; ++i) {
         result |= first[i] ^ second[i];
      }

      return (result == 0);
   }
}
