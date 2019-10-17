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

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Documented
@Retention (RUNTIME)
@Constraint (validatedBy = {PasswordValidator.class})
@Target (value = {METHOD, FIELD, PARAMETER})
public @interface Password {

   String message () default "Invalid password.";

   Class<?>[] groups () default {};

   Class<? extends Payload>[] payload () default {};
}
