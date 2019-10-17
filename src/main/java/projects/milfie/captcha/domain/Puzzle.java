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

package projects.milfie.captcha.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Embeddable
public class Puzzle
   implements Serializable
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static final String ANSWER_PATTERN = "^[a-zA-Z0-9]+$";

   public static final Puzzle EMPTY = new Puzzle ();

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public Puzzle (@NotNull
                  final byte[] question,
                  @NotNull @Pattern (regexp = Puzzle.ANSWER_PATTERN)
                  final String answer)
   {
      this.creationTime = System.currentTimeMillis ();
      this.question = question;
      this.answer = answer;
   }

   @Transient
   public boolean isEmpty () {
      return (creationTime <= 0L);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  creationTime
   @Min (0L)
   @Column (name = "CREATION_TIME", nullable = false)
   private long creationTime = 0L;

   public long getCreationTime () {
      return creationTime;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  question
   @Transient
   private transient byte[] question = null;

   public byte[] getQuestion () {
      return question;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  answer
   @NotNull
   @Column (name = "ANSWER", nullable = false)
   private String answer = "";

   public String getAnswer () {
      return answer;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   protected Puzzle () {
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final long serialVersionUID = 201905220431L;
}
