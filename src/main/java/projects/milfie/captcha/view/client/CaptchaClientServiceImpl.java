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

package projects.milfie.captcha.view.client;

import projects.milfie.captcha.domain.Profile;
import projects.milfie.captcha.service.*;

import java.security.Principal;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;

@RequestScoped
@WebService
   (serviceName = "CaptchaClientService",
    portName = "CaptchaPort",
    endpointInterface =
       "projects.milfie.captcha.view.client.CaptchaClientService",
    targetNamespace = "http://captcha.milfie.projects/wsdl")
public class CaptchaClientServiceImpl
   implements CaptchaClientService
{

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   public ProfileDTO[] findProfiles () {
      return
         clientService
            .findProfiles (this.getClientName ())
            .stream ()
            .map (ProfileDTO::new)
            .toArray (ProfileDTO[]::new);
   }

   @Override
   public ProfileDTO findProfile (final String profileName) {
      return
         new ProfileDTO
            (clientService.findProfile
               (this.getClientName (), profileName));
   }

   @Override
   public void saveProfile (final ProfileDTO profile)
      throws ProfileAlreadyExistsException,
             ClientNotFoundException,
             ProfileAlreadyModifiedException
   {
      final String clientName = this.getClientName ();
      final String profileName = profile.getProfileName ();

      Profile p = clientService.findProfile (clientName, profileName);

      if (p == null) {
         p = clientService.createProfile (clientName, profileName);
      }

      p.setConsumerTTL (profile.getConsumerTTL ());
      p.setPuzzleTTL (profile.getPuzzleTTL ());

      clientService.updateProfile (p);
   }

   @Override
   public void deleteProfile (final String profileName)
      throws ProfileNotFoundException,
             ProfileDeleteDefaultException
   {
      clientService.deleteProfile
         (this.getClientName (), profileName);
   }

   @Override
   public long activateConsumer (final String consumerName,
                                 final String profileName)
      throws ProfileNotFoundException,
             ClientNotFoundException
   {
      return
         clientService.activateConsumer
            (this.getClientName (), profileName, consumerName);
   }

   @Override
   public void deactivateConsumer (final String consumerName) {
      clientService
         .deactivateConsumer
            (this.getClientName (), consumerName);
   }

   @Override
   public boolean commitConsumerAnswer (final String consumerName,
                                        final String answer)
   {
      return
         clientService.commitConsumerAnswer
            (this.getClientName (), consumerName, answer);
   }

   @Override
   public boolean isLastPuzzleSolved (final String consumerName) {
      return
         clientService.isLastPuzzleSolved
            (this.getClientName (), consumerName);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @EJB
   private ClientServiceLocal clientService;

   @Resource
   private WebServiceContext wsContext;

   private String getClientName () {
      return getClientPrincipal ().getName ();
   }

   private Principal getClientPrincipal () {
      final Principal principal = wsContext.getUserPrincipal ();

      if (principal == null) {
         throw new WebServiceException ("Authorization required.");
      }

      return principal;
   }
}
