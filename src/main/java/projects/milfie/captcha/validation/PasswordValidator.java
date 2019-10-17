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

package projects.milfie.captcha.validation;

import projects.milfie.captcha.domain.Client;

import java.nio.CharBuffer;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator
   implements ConstraintValidator<Password, char[]>
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static boolean isValid (final char[] value) {
      return
         (value != null &&
          PASSWORD_REGEX.matcher (CharBuffer.wrap (value)).matches ());
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   public void initialize (final Password constraintAnnotation) {
   }

   @Override
   public boolean isValid (final char[] value,
                           final ConstraintValidatorContext context)
   {
      return (value == null || PasswordValidator.isValid (value));
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final Pattern PASSWORD_REGEX =
      Pattern.compile (Client.PASSWORD_PATTERN);
}
