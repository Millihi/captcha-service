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

package projects.milfie.captcha.view.consumer;

import projects.milfie.captcha.domain.Consumer;
import projects.milfie.captcha.domain.Puzzle;
import projects.milfie.captcha.service.ConsumerServiceLocal;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path ("/")
@RequestScoped
@Produces ({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class ConsumerResource {

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @GET
   @Path ("{id: [0-9]+}.gif")
   @Produces ("image/gif")
   public Response getQuestion
      (@Min (1) @PathParam ("id") final long id)
   {
      final Consumer consumer = consumerService.retrieve (id);

      if (consumer == null) {
         throw new WebApplicationException
            ("Resource id " + id, Response.Status.NOT_FOUND);
      }

      final Puzzle puzzle = consumerService.createPuzzle (consumer);

      final CacheControl cacheControl = new CacheControl ();
      cacheControl.setNoCache (true);

      return Response
         .ok (puzzle.getQuestion ())
         .cacheControl (cacheControl)
         .build ();
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @EJB
   private ConsumerServiceLocal consumerService;
}
