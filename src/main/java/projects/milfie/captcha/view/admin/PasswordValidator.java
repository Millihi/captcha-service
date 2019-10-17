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

package projects.milfie.captcha.view.admin;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import static projects.milfie.captcha.security.PasswordManager.isEqualPasswords;
import static projects.milfie.captcha.validation.PasswordValidator.isValid;

@FacesValidator
public class PasswordValidator
   implements Validator
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   public void validate (final FacesContext context,
                         final UIComponent component,
                         final Object value)
      throws ValidatorException
   {
      if (context == null || component == null) {
         throw new NullPointerException ();
      }

      final UIInput passwordInput = (UIInput) component;
      final char[] password = (char[]) value;

      if (password == null || password.length <= 0) {
         return;
      }

      if (!isValid (password)) {
         passwordInput.setValid (false);

         throw new ValidatorException
            (new FacesMessage ("Password is invalid."));
      }

      final UIInput confirmInput = (UIInput)
         component.getAttributes ().get ("confirm");
      final String confirm = (String) confirmInput.getSubmittedValue ();

      if (confirm == null || confirm.isEmpty ()) {
         return;
      }

      if (!isEqualPasswords (password, confirm.toCharArray ())) {
         confirmInput.setValid (false);
         passwordInput.setValid (false);

         throw new ValidatorException
            (new FacesMessage ("Passwords are not equal."));
      }
   }
}
