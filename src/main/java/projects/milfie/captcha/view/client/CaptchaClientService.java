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

import projects.milfie.captcha.service.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService (targetNamespace = "http://captcha.milfie.projects/wsdl")
@SOAPBinding (style = SOAPBinding.Style.RPC)
public interface CaptchaClientService {

   ////////////////////////////////////////////////////////////////////////////
   //  Profile methods.                                                      //
   ////////////////////////////////////////////////////////////////////////////

   @WebMethod
   public ProfileDTO[] findProfiles ();

   @WebMethod
   public ProfileDTO findProfile
      (@WebParam (name = "profileName") final String profileName);

   @WebMethod
   public void saveProfile
      (@WebParam (name = "profile") final ProfileDTO profile)
      throws ProfileAlreadyExistsException,
             ClientNotFoundException,
             ProfileAlreadyModifiedException;

   @WebMethod
   public void deleteProfile
      (@WebParam (name = "profileName") final String profileName)
      throws ProfileNotFoundException,
             ProfileDeleteDefaultException;

   ////////////////////////////////////////////////////////////////////////////
   //  Consumers methods.                                                    //
   ////////////////////////////////////////////////////////////////////////////

   @WebMethod
   public long activateConsumer
      (@WebParam (name = "consumerName") final String consumerName,
       @WebParam (name = "profileName") final String profileName)
      throws ProfileNotFoundException,
             ClientNotFoundException;

   @WebMethod
   public void deactivateConsumer
      (@WebParam (name = "consumerName") final String consumerName);

   @WebMethod
   public boolean commitConsumerAnswer
      (@WebParam (name = "consumerName") final String consumerName,
       @WebParam (name = "answer") final String answer);

   @WebMethod
   public boolean isLastPuzzleSolved
      (@WebParam (name = "consumerName") final String consumerName);
}
