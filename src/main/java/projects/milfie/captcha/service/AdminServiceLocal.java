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

package projects.milfie.captcha.service;

import projects.milfie.captcha.domain.Client;
import projects.milfie.captcha.domain.Consumer;
import projects.milfie.captcha.validation.Password;

import java.util.Collection;
import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Local
public interface AdminServiceLocal
   extends ClientServiceLocal
{

   public Collection<Client> findAll ();

   public Client find
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String name);

   public Client create
      (@NotNull final Client client,
       @NotNull @Password final char[] password)
      throws ClientAlreadyExistsException;

   public Client update
      (@NotNull final Client client)
      throws ClientAlreadyModifiedException;

   public Client changePassword
      (@NotNull final Client client,
       @NotNull @Password final char[] password)
      throws ClientAlreadyModifiedException;

   public void remove
      (@NotNull final Client client);

   public Collection<Consumer> findConsumers
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName);
}
