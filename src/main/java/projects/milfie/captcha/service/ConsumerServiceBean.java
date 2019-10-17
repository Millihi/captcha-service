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

import projects.milfie.captcha.dao.ConsumerDAO;
import projects.milfie.captcha.domain.Client;
import projects.milfie.captcha.domain.Consumer;
import projects.milfie.captcha.domain.Puzzle;
import projects.milfie.captcha.generator.PuzzleGenerator;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Stateless
public class ConsumerServiceBean
   implements ConsumerServiceLocal
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   public Consumer retrieve (@Min (1L) final long id) {
      return
         actualizeConsumer
            (consumerDAO.find (id), System.currentTimeMillis ());
   }

   @Override
   public Consumer retrieve
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName,
       @NotNull @Pattern (regexp = Consumer.NAME_PATTERN)
       final String consumerName)
   {
      return
         actualizeConsumer
            (consumerDAO.findByName (clientName, consumerName),
             System.currentTimeMillis ());
   }

   @Override
   public Puzzle createPuzzle (@NotNull final Consumer consumer) {
      final Puzzle puzzle = puzzleGenerator.generate ();

      consumer.setPuzzle (puzzle);
      consumer.setLastActivity (System.currentTimeMillis ());
      consumer.setLastPuzzleSolved (false);

      consumerDAO.update (consumer);

      return puzzle;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @Inject
   protected PuzzleGenerator puzzleGenerator;

   @Inject
   protected ConsumerDAO consumerDAO;

   protected Consumer actualizeConsumer (final Consumer consumer,
                                         final long currentTime)
   {
      if (consumer == null) {
         return null;
      }

      if (!isActiveConsumer (consumer, currentTime)) {
         consumerDAO.remove (consumer);

         return null;
      }

      consumer.setLastActivity (currentTime);
      actualizeConsumerPuzzle (consumer, currentTime);

      consumerDAO.update (consumer);

      return consumer;
   }

   protected boolean isActiveConsumer (final Consumer consumer,
                                       final long currentTime)
   {
      final long consumerTTL = consumer.getProfile ().getConsumerTTL ();
      final long inactiveTime = currentTime - consumer.getLastActivity ();

      return (inactiveTime < consumerTTL);
   }

   private void actualizeConsumerPuzzle (final Consumer consumer,
                                         final long currentTime)
   {
      if (consumer == null) {
         return;
      }

      final Puzzle puzzle = consumer.getPuzzle ();

      if (puzzle.isEmpty ()) {
         return;
      }

      final long puzzleTTL = consumer.getProfile ().getPuzzleTTL ();
      final long livedTime = currentTime - puzzle.getCreationTime ();

      if (livedTime >= puzzleTTL) {
         consumer.setPuzzle (Puzzle.EMPTY);
      }
   }
}
