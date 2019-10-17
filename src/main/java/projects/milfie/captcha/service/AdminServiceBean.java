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
import projects.milfie.captcha.domain.Profile;
import projects.milfie.captcha.domain.Role;
import projects.milfie.captcha.security.PasswordManager;
import projects.milfie.captcha.validation.Password;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Stateless
@RolesAllowed ({Role.ADMIN_NAME})
public class AdminServiceBean
   extends ClientServiceBean
   implements AdminServiceLocal
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   public Collection<Client> findAll () {
      return clientDAO.findAll ();
   }

   @Override
   public Client find
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String name)
   {
      return clientDAO.find (name);
   }

   @Override
   public Client create
      (@NotNull final Client client,
       @NotNull @Password final char[] password)
      throws ClientAlreadyExistsException
   {
      if (clientDAO.find (client.getName ()) != null) {
         Arrays.fill (password, '0');
         throw new ClientAlreadyExistsException
            ("Client \"" + client.getName () + "\" already exists.");
      }

      client.setPassword (passwordManager.createToken (password));

      final Profile profile = new Profile (Profile.DEFAULT_NAME, client);

      clientDAO.persist (client);
      profileDAO.persist (profile);

      return client;
   }

   @Override
   public Client update (@NotNull final Client client)
      throws ClientAlreadyModifiedException
   {
      try {
         return clientDAO.update (client);
      }
      catch (final OptimisticLockException e) {
         throw new ClientAlreadyModifiedException (e);
      }
   }

   @Override
   public Client changePassword
      (@NotNull final Client client,
       @NotNull @Password final char[] password)
      throws ClientAlreadyModifiedException
   {
      client.setPassword (passwordManager.createToken (password));

      return this.update (client);
   }

   @Override
   public void remove (@NotNull final Client client) {
      final Collection<Consumer> clientConsumers =
         consumerDAO.findByClient (client.getName ());

      for (final Consumer consumer : clientConsumers) {
         consumerDAO.remove (consumer);
      }

      final Collection<Profile> clientProfiles =
         profileDAO.findByClient (client.getName ());

      for (final Profile profile : clientProfiles) {
         profileDAO.remove (profile);
      }

      clientDAO.remove (client);
   }

   public Collection<Consumer> findConsumers
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName)
   {
      return consumerDAO.findByClient (clientName);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @Inject
   private PasswordManager passwordManager;
}
