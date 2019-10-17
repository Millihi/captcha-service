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
import projects.milfie.captcha.domain.Puzzle;

import javax.ejb.Local;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Local
public interface ConsumerServiceLocal {

   public Consumer retrieve
      (@Min (1L) final long consumerId);

   public Consumer retrieve
      (@NotNull @Pattern (regexp = Client.NAME_PATTERN)
       final String clientName,
       @NotNull @Pattern (regexp = Consumer.NAME_PATTERN)
       final String consumerName);

   public Puzzle createPuzzle
      (@NotNull final Consumer consumer);
}
