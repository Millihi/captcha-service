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

package projects.milfie.captcha.generator;

///////////////////////////////////////////////////////////////////////////////
//  Purpose:
//     Grammar rules for word generator.
enum Grammar {
   W_RULES ("W", new String[]
      {"%C%%T%", "%C%%T%", "%C%%X%", "%C%%D%%F%", "%C%%V%%F%%T%",
       "%C%%D%%F%%U%", "%C%%T%%U%", "%I%%T%", "%I%%C%%T%", "%A%"}),
   A_RULES ("A", new String[]
      {"%K%%V%%K%%V%tion"}),
   K_RULES ("K", new String[]
      {"b", "c", "d", "f", "g", "j", "l", "m", "n", "p", "qu", "r", "s",
       "t", "v", "s%P%"}),
   I_RULES ("I", new String[]
      {"ex", "in", "un", "re", "de"}),
   T_RULES ("T", new String[]
      {"%V%%F%", "%V%%E%e"}),
   U_RULES ("U", new String[]
      {"er", "ish", "ly", "en", "ing", "ness", "ment", "able", "ive"}),
   C_RULES ("C", new String[]
      {"b", "c", "ch", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p",
       "qu", "r", "s", "sh", "t", "th", "v", "w", "y", "s%P%", "%R%r",
       "%L%l"}),
   E_RULES ("E", new String[]
      {"b", "c", "ch", "d", "f", "g", "dg", "l", "m", "n", "p", "r", "s",
       "t", "th", "v", "z"}),
   F_RULES ("F", new String[]
      {"b", "tch", "d", "ff", "g", "gh", "ck", "ll", "m", "n", "n", "ng",
       "p", "r", "ss", "sh", "t", "tt", "th", "x", "y", "zz", "r%R%",
       "s%P%", "l%L%"}),
   P_RULES ("P", new String[]
      {"p", "t", "k", "c"}),
   Q_RULES ("Q", new String[]
      {"b", "d", "g"}),
   L_RULES ("L", new String[]
      {"b", "f", "k", "p", "s"}),
   R_RULES ("R", new String[]
      {"%P%", "%Q%", "f", "th", "sh"}),
   V_RULES ("V", new String[]
      {"a", "e", "i", "o", "u"}),
   D_RULES ("D", new String[]
      {"aw", "ei", "ow", "ou", "ie", "ea", "ai", "oy"}),
   X_RULES ("X", new String[]
      {"e", "i", "o", "aw", "ow", "oy"});

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public String getKey () {
      return key;
   }

   public String[] getRules () {
      return rules;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static String getInitialRule () {
      return INITIAL_RULE;
   }

   public static String getKeyPattern () {
      return KEY_PATTERN;
   }

   public static String[] getRules (final String key) {
      for (final Grammar grm : Grammar.values ()) {
         if (key.equals (grm.key)) {
            return grm.rules;
         }
      }
      throw new IllegalStateException ("Unknown key [" + key + "].");
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private Grammar (final String key,
                    final String[] rules)
   {
      this.key = key;
      this.rules = rules;
   }

   private final String   key;
   private final String[] rules;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String INITIAL_RULE = "%W%";
   private static final String KEY_PATTERN  = "%(\\w+)%";
}
