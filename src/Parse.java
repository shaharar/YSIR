import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;

public class Parse {
   private String[] tokens; // the following data structure contains tokens
   private HashSet<Term> terms; // the following data structure contains final terms
   private HashSet<String> stopWords; // the following data structure contains the stop words
   private int currentIdx;
   private Indexer indexer;

   public Parse (){
      terms = new HashSet<Term>();
      stopWords = new HashSet<String>();
      setStopWords();
      currentIdx = 0;
      //tokens = new String[]{"First","50 thousand","about","Aviad","At first","66 1/2 Dollars","35 million U.S dollars","Amit and Aviad","20.6 m Dollars","$120 billion","100 bn Dollars","$2 trillion","$30","40 Dollars","18.24","10,123","10,123,000","7 Trillion","34 2/3", "6-7", "-13", "step-by-step 10-part","70.5%","13.86 percent"};
   }

   // the following function parses the text of a specific document by the defined rules
   public void parseDocText(Document doc) {
       ArrayList <String> months = new ArrayList<String>(Arrays.asList("Jan", "JAN", "January", "JANUARY", //the following data structure contains months valid formats
               "Feb", "FEB", "February", "FEBRUARY",
               "Mar", "MAR", "March", "MARCH",
               "Apr", "APR", "April", "APRIL",
               "May", "MAY",
               "Jun", "JUN", "June", "JUNE",
               "Jul", "JUL", "July", "JULY",
               "Aug", "AUG", "August", "AUGUST",
               "Sep", "SEP", "September", "SEPTEMBER",
               "Oct", "OCT", "October", "OCTOBER",
               "Nov", "NOV", "November", "NOVEMBER",
               "Dec", "DEC", "December", "DECEMBER"));
       String docText = doc.getText();
      tokens = docText.split(" |\\. |\\, |\\: ");
      String token;
      while (currentIdx < tokens.length){
          token = tokens[currentIdx];
         String nextToken = "";
          //numbers
         if (token.matches("^[0-9]*+([,.][0-9]*?)*?$")) {
             if(currentIdx + 1 < tokens.length) {
                 nextToken = tokens[currentIdx + 1];
             }
               // token is a percent
             if (nextToken.equals("percent") || nextToken.equals("percentage")) {
                 percentage(token);
             }

            // token represents one of the following: a number or a price
            else if (nextToken.equalsIgnoreCase("Thousand") || nextToken.equalsIgnoreCase("Million") || nextToken.equalsIgnoreCase("Billion") || nextToken.equalsIgnoreCase("Trillion") || nextToken.contains("/")) {
                // token is a price
               if (((currentIdx + 2 < tokens.length) && (tokens[currentIdx + 2].equals("Dollars"))) || ((currentIdx + 3 < tokens.length) && (tokens[currentIdx + 2].equals("U.S") && tokens[currentIdx + 3].equals("dollars")))) {
                  prices(token);
               }
               // token is a number
               else {
                  numbers(token);
               }
            }
            // token is a price
            else if (currentIdx + 1 < tokens.length && nextToken.equals("Dollars") || ((currentIdx + 2 < tokens.length) && ((nextToken.equals("m") || nextToken.equals("bn")) && tokens[currentIdx + 2].equals("Dollars")))) {
               prices(token);
            }
            // token is a date
            else if (currentIdx + 1 < tokens.length && months.contains(nextToken)) {
               dates(token);
            }
            else{
                numbers(token);
            }
         }

         //symbols
         else if (token.contains("%")) {
             percentage(token);
          }
          else if (token.contains("$")) {
              prices(token);
          }
          else if (token.contains("-")) {
              rangesAndExpressions(token);
          }

          //words
         else{
             if(!(stopWords.contains(token))){ //if token is not a stop word
                 //dates
                 if(months.contains(token)){
                     dates(token);
                 }
                 //ranges
                 else if (token.equalsIgnoreCase("Between") && Pattern.compile("^[0-9] + ([,.][0-9]?)?$").matcher(token).find()){
                     rangesAndExpressions(token);
                 }
                 //just words
                 else {
                     lettersCase(token);
                 }
             }
         }
         currentIdx++;
      }
   }

   public void numbers (String token) {
      double num;
      //negative number
      if(token.startsWith("-")){
         num = Double.parseDouble(((token.substring(1)).replace(",",""))) * (-1);
      }
      else {
          num = Double.parseDouble(token.replace(",",""));
      }
             //num has a fraction after it - like '34 2/3'
             if (tokens[currentIdx + 1].contains("/")) {
                 terms.add(token + " " + tokens[currentIdx + 1]);
                 currentIdx++;
             }
            //num is less than 1,000
            else if (num < 1000) {
               //Thousand after num - like '50 Thousand'
               if (tokens[currentIdx + 1].equals("Thousand")) {
                  terms.add(token + "K");
                  currentIdx++;
               }
               //Million after num - like '50 Million'
               else if (tokens[currentIdx + 1].equals("Million")) {
                  terms.add(token + "M");
                   currentIdx++;
               }
               //Billion after num - like '50 Billion'
               else if (tokens[currentIdx + 1].equals("Billion")) {
                  terms.add(token + "B");
                   currentIdx++;
               }
               //Trillion after num - like '50 Trillion'
               else if (tokens[currentIdx + 1].equals("Trillion")) {
                   if((num * 1000) % 1000 == 0){
                       terms.add((int)(num * 1000) + "B");
                   }
                  else{
                      terms.add((num * 1000) + "B");
                   }
                   currentIdx++;
               }
               //just number - like '123'
               else {
                  terms.add(token);
               }
            }
            //num is up to 1,000
            else {
               //num between 1,000 to 999,000
               if (num < 1000000) {
                  terms.add((num / 1000) + "K");
               }
               //num between 1,000,000 to 999,000,000
               else if (num < 1000000000) {
                  terms.add((num / 1000000) + "M");
               }
               //num up to 1,000,000,000
               else {
                  terms.add((num / 1000000000) + "B");
               }
            }
   }

   // the following function classifies lower case and upper case tokens and adds final terms to the compatible data structure.
   public void lettersCase(String token) {
      if (token.charAt(0) >= 97 && token.charAt(0) <= 122) //lower case
      {
         if (terms.contains(token.toUpperCase()))
         {
            terms.remove(token);
         }
         terms.add(token.toLowerCase());
      }
      else { //upper case
         if (!terms.contains(token.toLowerCase())){
            terms.add(token.toUpperCase());
         }
      }
   }

   // the following function adds final terms to the data structure in this format : NUMBER%.
   public void percentage(String token) {
      terms.add(token.replaceAll("%", "") + "%");
      if(tokens[currentIdx+1].equals("percent") || tokens[currentIdx+1].equals("percentage")){
         currentIdx++;
      }
   }

   // the following function adds final terms to the data structure in one of these formats : PRICE Dollars, PRICE M Dollars
   public void prices(String token) {
      double price;
      if (token.startsWith("$"))
      {
         price = Double.parseDouble(token.replace("$", ""));
      }
      else
      {
         price = Double.parseDouble(token);
      }

      if (price >= 1000000 || tokens[currentIdx + 1].equalsIgnoreCase("million") || tokens[currentIdx + 1].equalsIgnoreCase("billion") || tokens[currentIdx + 1].equalsIgnoreCase("trillion") || tokens[currentIdx + 1].equals("bn") || tokens[currentIdx + 1].equals("m") )
      {
         if (tokens[currentIdx + 1].equalsIgnoreCase("million") || tokens[currentIdx + 1].equals("m"))
         {
             if (price == (int)(price)){
                 terms.add((int)price + " M" + " Dollars");
             }
            else
                terms.add(price + " M" + " Dollars");
            if(currentIdx + 2 < tokens.length && tokens[currentIdx+2].equals("Dollars")) {
                currentIdx = currentIdx + 2;
            }
            else if ((currentIdx + 2 < tokens.length && tokens[currentIdx+2].equals("U.S")) && (currentIdx + 3 < tokens.length && tokens[currentIdx+3].equals("dollars"))) {
                currentIdx = currentIdx + 3;
            }
            else
                currentIdx++;
         }
         else if (tokens[currentIdx + 1].equalsIgnoreCase("billion") || tokens[currentIdx + 1].equals("bn"))
         {
             if (price == (int)(price)){
                 terms.add((int)(price * 1000) + " M" + " Dollars");
             }
            else
                terms.add((price * 1000) + " M" + " Dollars");
             if(currentIdx + 2 < tokens.length && tokens[currentIdx+2].equals("Dollars")){
                 currentIdx = currentIdx + 2;
             }
             else if ((currentIdx + 2 < tokens.length && tokens[currentIdx+2].equals("U.S")) && (currentIdx + 3 < tokens.length && tokens[currentIdx+3].equals("dollars"))) {
                 currentIdx = currentIdx + 3;
             }
             else
                 currentIdx++;
         }
         else if (tokens[currentIdx + 1].equalsIgnoreCase("trillion"))
         {
             if (price == (int)(price)){
                 terms.add((int)(price * 1000) + " M" + " Dollars");
             }
            else
                terms.add((price * 1000000) + " M" + " Dollars");
             if(currentIdx + 2 < tokens.length && tokens[currentIdx+2].equals("Dollars")){
                 currentIdx = currentIdx + 2;
             }
             else if ((currentIdx + 2 < tokens.length && tokens[currentIdx+2].equals("U.S")) && (currentIdx + 3 < tokens.length && tokens[currentIdx+3].equals("dollars"))) {
                 currentIdx = currentIdx + 3;
             }
             else
                 currentIdx++;
         }
         else
         {
             if (price == (int)(price)){
                 terms.add((int)(price/1000000) + " M" + " Dollars");
             }
            else
                terms.add((price / 1000000) + " M" + " Dollars");
             if(currentIdx + 1 < tokens.length && tokens[currentIdx+1].equals("Dollars")){
                 currentIdx++;
             }
         }
      }
      else
      {
         if (token.startsWith("$") || tokens[currentIdx + 1].equals("Dollars")){
             if (price == (int)(price)){
                 terms.add((int)price + " Dollars");
             }
            else
                terms.add(price + " Dollars");
             if(tokens[currentIdx + 1].equals("Dollars")){
                 currentIdx++;
             }
         }
         else if (tokens[currentIdx + 2].equals("Dollars"))
         {
             if (price == (int)(price)){
                 terms.add((int)price + " " + tokens[currentIdx + 1] + " Dollars");
             }
            else
                terms.add(price + " " + tokens[currentIdx + 1] + " Dollars");
            currentIdx = currentIdx + 2;
         }
      }
   }

   // the following function adds final terms to the data structure in one of these formats : MM-DD, YYYY-MM.
   public void dates(String token) {
      String month = "";
      if (!(Pattern.compile("[0-9]").matcher(token).find())) { //check if the token contains digits, if not - it represents the month
         month = checkMonth(token);
         //'Month YYYY' format -> 'YYYY-MM'
         if (currentIdx + 1 < tokens.length && (tokens[currentIdx + 1].length() == 4)) {
            terms.add(tokens[currentIdx + 1] + "-" + month);
            currentIdx++;
         }
         //'Month DD' format -> 'MM-DD'
         else if (currentIdx + 1 < tokens.length && (tokens[currentIdx + 1].length() <= 2)) {
            if (tokens[currentIdx + 1].length() == 1){
               terms.add(month + "-" + "0" + tokens[currentIdx + 1]);
            }
            else{
               terms.add(month + "-" + tokens[currentIdx + 1]);
            }

             currentIdx++;
         }
      }
      //'DD Month' format -> 'MM-DD'
      else {
          if(currentIdx + 1 < tokens.length) {
              month = checkMonth(tokens[currentIdx + 1]);
              if (tokens[currentIdx].length() == 1) {
                  terms.add(month + "-" + "0" + token);
              } else {
                  terms.add(month + "-" + token);
              }
              currentIdx++;
          }
      }
   }

   // help function for 'dates' - the following function converts the String representation for a particular month from letters to digits.
   private String checkMonth(String month) {
      if (month.equals("Jan") || month.equals("JAN") || month.equals("January") || month.equals("JANUARY"))
         return "01";
      else if (month.equals("Feb") || month.equals("FEB") || month.equals("February") || month.equals("FEBRUARY"))
         return "02";
      else if (month.equals("Mar") || month.equals("MAR") || month.equals("March") || month.equals("MARCH"))
         return "03";
      else if (month.equals("Apr") || month.equals("APR") || month.equals("April") || month.equals("APRIL"))
         return "04";
      else if (month.equals("May") || month.equals("MAY"))
         return "05";
      else if (month.equals("Jun") || month.equals("JUN") || month.equals("June") || month.equals("JUNE"))
         return "06";
      else if (month.equals("Jul") || month.equals("JUL") || month.equals("July") || month.equals("JULY"))
         return "07";
      else if (month.equals("Aug") || month.equals("AUG") || month.equals("August") || month.equals("AUGUST"))
         return "08";
      else if (month.equals("Sep") || month.equals("SEP") || month.equals("September") || month.equals("SEPTEMBER"))
         return "09";
      else if (month.equals("Oct") || month.equals("OCT") || month.equals("October") || month.equals("OCTOBER"))
         return "10";
      else if (month.equals("Nov") || month.equals("NOV") || month.equals("November") || month.equals("NOVEMBER"))
         return "11";
      else if (month.equals("Dec") || month.equals("DEC") || month.equals("December") || month.equals("DECEMBER"))
         return "12";
      return "";
   }

   // the following function adds final terms to the data structure in one of these formats : Word-Word, Word-Word-Word, Word-Number, Number-Word, Number-Number, Between Number and Number.
   public void rangesAndExpressions(String token) {
/*      //'Word-word' or 'Word-word-word' format
      if (!(Pattern.compile("[0-9]").matcher(token).find()))
         terms.add(token);*/
      //'Between number and number' format
      if(token.equals("Between")){
         double firstNum = Double.parseDouble(tokens[currentIdx + 1]); //lower range
         currentIdx++;
         while(!(Pattern.compile("[0-9]").matcher(tokens[currentIdx + 1]).find())){ //search the second number in the range
            currentIdx++;
         }
         double secondNum = Double.parseDouble(tokens[currentIdx + 1]); //upper range
         terms.add(firstNum + "-" + secondNum);
         currentIdx++;
      }
      //negative number
       else if((token.charAt(0) == '-') && (Character.isDigit(token.charAt(1)))){
         int i=2;
         //check if the negative number is a part of a range (has more than one '-') or just a negative number
         while ((i < token.length()) && (token.charAt(i) != '-') && (Character.isDigit(token.charAt(i)))) {
            i++;
         }
         if(i != token.length() && token.charAt(i) == '-'){
            terms.add(token); //add it as a range
         }
         else{
            numbers(token); //calling to numbers parse function
         }
      }
      else{
          terms.add(token);
      }


      //'Number-word' or 'Word-Number'

   }

   public static void main (String [] args){
      Parse p = new Parse();
      Document doc = new Document();
      doc.setText("First, 50 Thousand, about, Aviad, At first. 66 1/2 Dollars, 35 million U.S dollars, Amit and Aviad, 20.6 m Dollars, $120 billion 100 bn Dollars $2 trillion $30 40 Dollars, 18.24 10,123, 10,123,000, 7 Trillion 34 2/3. 6-7 -13 step-by-step 10-part 70.5%, 13.86 percent");
       p.parseDocText(doc);
      for (String term:p.terms) {
         System.out.println(term);
      }
   }


    // the following function saves defined stop words in the memory
    private void setStopWords (){
        stopWords.add("a");
        stopWords.add("a's");
        stopWords.add("able");
        stopWords.add("about");
        stopWords.add("above");
        stopWords.add("according");
        stopWords.add("accordingly");
        stopWords.add("across");
        stopWords.add("actually");
        stopWords.add("after");
        stopWords.add("afterwards");
        stopWords.add("again");
        stopWords.add("against");
        stopWords.add("ain't");
        stopWords.add("all");
        stopWords.add("allow");
        stopWords.add("allows");
        stopWords.add("almost");
        stopWords.add("alone");
        stopWords.add("along");
        stopWords.add("already");
        stopWords.add("also");
        stopWords.add("although");
        stopWords.add("always");
        stopWords.add("am");
        stopWords.add("among");
        stopWords.add("amongst");
        stopWords.add("an");
        stopWords.add("and");
        stopWords.add("another");
        stopWords.add("any");
        stopWords.add("anybody");
        stopWords.add("anyhow");
        stopWords.add("anyone");
        stopWords.add("anything");
        stopWords.add("anyway");
        stopWords.add("anyways");
        stopWords.add("anywhere");
        stopWords.add("apart");
        stopWords.add("appear");
        stopWords.add("appreciate");
        stopWords.add("appropriate");
        stopWords.add("are");
        stopWords.add("aren't");
        stopWords.add("around");
        stopWords.add("as");
        stopWords.add("aside");
        stopWords.add("ask");
        stopWords.add("asking");
        stopWords.add("associated");
        stopWords.add("at");
        stopWords.add("available");
        stopWords.add("away");
        stopWords.add("awfully");
        stopWords.add("b");
        stopWords.add("be");
        stopWords.add("became");
        stopWords.add("because");
        stopWords.add("become");
        stopWords.add("becomes");
        stopWords.add("becoming");
        stopWords.add("been");
        stopWords.add("before");
        stopWords.add("beforehand");
        stopWords.add("behind");
        stopWords.add("being");
        stopWords.add("believe");
        stopWords.add("below");
        stopWords.add("beside");
        stopWords.add("besides");
        stopWords.add("best");
        stopWords.add("better");
        stopWords.add("between");
        stopWords.add("beyond");
        stopWords.add("both");
        stopWords.add("brief");
        stopWords.add("but");
        stopWords.add("by");
        stopWords.add("c");
        stopWords.add("c'mon");
        stopWords.add("c's");
        stopWords.add("came");
        stopWords.add("can");
        stopWords.add("can't");
        stopWords.add("cannot");
        stopWords.add("cant");
        stopWords.add("cause");
        stopWords.add("causes");
        stopWords.add("certain");
        stopWords.add("certainly");
        stopWords.add("changes");
        stopWords.add("clearly");
        stopWords.add("co");
        stopWords.add("com");
        stopWords.add("come");
        stopWords.add("comes");
        stopWords.add("concerning");
        stopWords.add("consequently");
        stopWords.add("consider");
        stopWords.add("considering");
        stopWords.add("contain");
        stopWords.add("containing");
        stopWords.add("contains");
        stopWords.add("corresponding");
        stopWords.add("could");
        stopWords.add("couldn't");
        stopWords.add("course");
        stopWords.add("currently");
        stopWords.add("d");
        stopWords.add("definitely");
        stopWords.add("described");
        stopWords.add("despite");
        stopWords.add("did");
        stopWords.add("didn't");
        stopWords.add("different");
        stopWords.add("do");
        stopWords.add("does");
        stopWords.add("doesn't");
        stopWords.add("doing");
        stopWords.add("don't");
        stopWords.add("done");
        stopWords.add("down");
        stopWords.add("downwards");
        stopWords.add("during");
        stopWords.add("e");
        stopWords.add("each");
        stopWords.add("edu");
        stopWords.add("eg");
        stopWords.add("eight");
        stopWords.add("either");
        stopWords.add("else");
        stopWords.add("elsewhere");
        stopWords.add("enough");
        stopWords.add("entirely");
        stopWords.add("especially");
        stopWords.add("et");
        stopWords.add("etc");
        stopWords.add("even");
        stopWords.add("ever");
        stopWords.add("every");
        stopWords.add("everybody");
        stopWords.add("everyone");
        stopWords.add("everything");
        stopWords.add("everywhere");
        stopWords.add("ex");
        stopWords.add("exactly");
        stopWords.add("example");
        stopWords.add("except");
        stopWords.add("f");
        stopWords.add("far");
        stopWords.add("few");
        stopWords.add("fifth");
        stopWords.add("first");
        stopWords.add("five");
        stopWords.add("followed");
        stopWords.add("following");
        stopWords.add("follows");
        stopWords.add("for");
        stopWords.add("former");
        stopWords.add("formerly");
        stopWords.add("forth");
        stopWords.add("four");
        stopWords.add("from");
        stopWords.add("further");
        stopWords.add("furthermore");
        stopWords.add("g");
        stopWords.add("get");
        stopWords.add("gets");
        stopWords.add("getting");
        stopWords.add("given");
        stopWords.add("gives");
        stopWords.add("go");
        stopWords.add("goes");
        stopWords.add("going");
        stopWords.add("gone");
        stopWords.add("got");
        stopWords.add("gotten");
        stopWords.add("greetings");
        stopWords.add("h");
        stopWords.add("had");
        stopWords.add("hadn't");
        stopWords.add("happens");
        stopWords.add("hardly");
        stopWords.add("has");
        stopWords.add("hasn't");
        stopWords.add("have");
        stopWords.add("haven't");
        stopWords.add("having");
        stopWords.add("he");
        stopWords.add("he's");
        stopWords.add("hello");
        stopWords.add("help");
        stopWords.add("hence");
        stopWords.add("her");
        stopWords.add("here");
        stopWords.add("here's");
        stopWords.add("hereafter");
        stopWords.add("hereby");
        stopWords.add("herein");
        stopWords.add("hereupon");
        stopWords.add("hers");
        stopWords.add("herself");
        stopWords.add("hi");
        stopWords.add("him");
        stopWords.add("himself");
        stopWords.add("his");
        stopWords.add("hither");
        stopWords.add("hopefully");
        stopWords.add("how");
        stopWords.add("howbeit");
        stopWords.add("however");
        stopWords.add("i");
        stopWords.add("i'd");
        stopWords.add("i'll");
        stopWords.add("i'm");
        stopWords.add("i've");
        stopWords.add("ie");
        stopWords.add("if");
        stopWords.add("ignored");
        stopWords.add("immediate");
        stopWords.add("in");
        stopWords.add("inasmuch");
        stopWords.add("inc");
        stopWords.add("indeed");
        stopWords.add("indicate");
        stopWords.add("indicated");
        stopWords.add("indicates");
        stopWords.add("inner");
        stopWords.add("insofar");
        stopWords.add("instead");
        stopWords.add("into");
        stopWords.add("inward");
        stopWords.add("is");
        stopWords.add("isn't");
        stopWords.add("it");
        stopWords.add("it'd");
        stopWords.add("it'll");
        stopWords.add("it's");
        stopWords.add("its");
        stopWords.add("itself");
        stopWords.add("j");
        stopWords.add("just");
        stopWords.add("k");
        stopWords.add("keep");
        stopWords.add("keeps");
        stopWords.add("kept");
        stopWords.add("know");
        stopWords.add("knows");
        stopWords.add("known");
        stopWords.add("l");
        stopWords.add("last");
        stopWords.add("lately");
        stopWords.add("later");
        stopWords.add("latter");
        stopWords.add("latterly");
        stopWords.add("least");
        stopWords.add("less");
        stopWords.add("lest");
        stopWords.add("let");
        stopWords.add("let's");
        stopWords.add("like");
        stopWords.add("liked");
        stopWords.add("likely");
        stopWords.add("little");
        stopWords.add("look");
        stopWords.add("looking");
        stopWords.add("looks");
        stopWords.add("ltd");
        stopWords.add("m");
        stopWords.add("mainly");
        stopWords.add("many");
        stopWords.add("may");
        stopWords.add("maybe");
        stopWords.add("me");
        stopWords.add("mean");
        stopWords.add("meanwhile");
        stopWords.add("merely");
        stopWords.add("might");
        stopWords.add("more");
        stopWords.add("moreover");
        stopWords.add("most");
        stopWords.add("mostly");
        stopWords.add("much");
        stopWords.add("must");
        stopWords.add("my");
        stopWords.add("myself");
        stopWords.add("n");
        stopWords.add("name");
        stopWords.add("namely");
        stopWords.add("nd");
        stopWords.add("near");
        stopWords.add("nearly");
        stopWords.add("necessary");
        stopWords.add("need");
        stopWords.add("needs");
        stopWords.add("neither");
        stopWords.add("never");
        stopWords.add("nevertheless");
        stopWords.add("new");
        stopWords.add("next");
        stopWords.add("nine");
        stopWords.add("no");
        stopWords.add("nobody");
        stopWords.add("non");
        stopWords.add("none");
        stopWords.add("noone");
        stopWords.add("nor");
        stopWords.add("normally");
        stopWords.add("not");
        stopWords.add("nothing");
        stopWords.add("novel");
        stopWords.add("now");
        stopWords.add("nowhere");
        stopWords.add("o");
        stopWords.add("obviously");
        stopWords.add("of");
        stopWords.add("off");
        stopWords.add("often");
        stopWords.add("oh");
        stopWords.add("ok");
        stopWords.add("okay");
        stopWords.add("old");
        stopWords.add("on");
        stopWords.add("once");
        stopWords.add("one");
        stopWords.add("ones");
        stopWords.add("only");
        stopWords.add("onto");
        stopWords.add("or");
        stopWords.add("other");
        stopWords.add("others");
        stopWords.add("otherwise");
        stopWords.add("ought");
        stopWords.add("our");
        stopWords.add("ours");
        stopWords.add("ourselves");
        stopWords.add("out");
        stopWords.add("outside");
        stopWords.add("over");
        stopWords.add("overall");
        stopWords.add("own");
        stopWords.add("p");
        stopWords.add("particular");
        stopWords.add("particularly");
        stopWords.add("per");
        stopWords.add("perhaps");
        stopWords.add("placed");
        stopWords.add("please");
        stopWords.add("plus");
        stopWords.add("possible");
        stopWords.add("presumably");
        stopWords.add("probably");
        stopWords.add("provides");
        stopWords.add("q");
        stopWords.add("que");
        stopWords.add("quite");
        stopWords.add("qv");
        stopWords.add("r");
        stopWords.add("rather");
        stopWords.add("rd");
        stopWords.add("re");
        stopWords.add("really");
        stopWords.add("reasonably");
        stopWords.add("regarding");
        stopWords.add("regardless");
        stopWords.add("regards");
        stopWords.add("relatively");
        stopWords.add("respectively");
        stopWords.add("right");
        stopWords.add("s");
        stopWords.add("said");
        stopWords.add("same");
        stopWords.add("saw");
        stopWords.add("say");
        stopWords.add("saying");
        stopWords.add("says");
        stopWords.add("second");
        stopWords.add("secondly");
        stopWords.add("see");
        stopWords.add("seeing");
        stopWords.add("seem");
        stopWords.add("seemed");
        stopWords.add("seeming");
        stopWords.add("seems");
        stopWords.add("seen");
        stopWords.add("self");
        stopWords.add("selves");
        stopWords.add("sensible");
        stopWords.add("sent");
        stopWords.add("serious");
        stopWords.add("seriously");
        stopWords.add("seven");
        stopWords.add("several");
        stopWords.add("shall");
        stopWords.add("she");
        stopWords.add("should");
        stopWords.add("shouldn't");
        stopWords.add("since");
        stopWords.add("six");
        stopWords.add("so");
        stopWords.add("some");
        stopWords.add("somebody");
        stopWords.add("somehow");
        stopWords.add("someone");
        stopWords.add("something");
        stopWords.add("sometime");
        stopWords.add("sometimes");
        stopWords.add("somewhat");
        stopWords.add("somewhere");
        stopWords.add("soon");
        stopWords.add("sorry");
        stopWords.add("specified");
        stopWords.add("specify");
        stopWords.add("specifying");
        stopWords.add("still");
        stopWords.add("sub");
        stopWords.add("such");
        stopWords.add("sup");
        stopWords.add("sure");
        stopWords.add("t");
        stopWords.add("t's");
        stopWords.add("take");
        stopWords.add("taken");
        stopWords.add("tell");
        stopWords.add("tends");
        stopWords.add("th");
        stopWords.add("than");
        stopWords.add("thank");
        stopWords.add("thanks");
        stopWords.add("thanx");
        stopWords.add("that");
        stopWords.add("that's");
        stopWords.add("thats");
        stopWords.add("the");
        stopWords.add("their");
        stopWords.add("theirs");
        stopWords.add("them");
        stopWords.add("themselves");
        stopWords.add("then");
        stopWords.add("thence");
        stopWords.add("there");
        stopWords.add("there's");
        stopWords.add("thereafter");
        stopWords.add("thereby");
        stopWords.add("therefore");
        stopWords.add("therein");
        stopWords.add("theres");
        stopWords.add("thereupon");
        stopWords.add("these");
        stopWords.add("they");
        stopWords.add("they'd");
        stopWords.add("they'll");
        stopWords.add("they're");
        stopWords.add("they've");
        stopWords.add("think");
        stopWords.add("third");
        stopWords.add("this");
        stopWords.add("thorough");
        stopWords.add("thoroughly");
        stopWords.add("those");
        stopWords.add("though");
        stopWords.add("three");
        stopWords.add("through");
        stopWords.add("throughout");
        stopWords.add("thru");
        stopWords.add("thus");
        stopWords.add("to");
        stopWords.add("together");
        stopWords.add("too");
        stopWords.add("took");
        stopWords.add("toward");
        stopWords.add("towards");
        stopWords.add("tried");
        stopWords.add("tries");
        stopWords.add("truly");
        stopWords.add("try");
        stopWords.add("trying");
        stopWords.add("twice");
        stopWords.add("two");
        stopWords.add("u");
        stopWords.add("un");
        stopWords.add("under");
        stopWords.add("unfortunately");
        stopWords.add("unless");
        stopWords.add("unlikely");
        stopWords.add("until");
        stopWords.add("unto");
        stopWords.add("up");
        stopWords.add("upon");
        stopWords.add("us");
        stopWords.add("use");
        stopWords.add("used");
        stopWords.add("useful");
        stopWords.add("uses");
        stopWords.add("using");
        stopWords.add("usually");
        stopWords.add("uucp");
        stopWords.add("v");
        stopWords.add("value");
        stopWords.add("various");
        stopWords.add("very");
        stopWords.add("via");
        stopWords.add("viz");
        stopWords.add("vs");
        stopWords.add("w");
        stopWords.add("want");
        stopWords.add("wants");
        stopWords.add("was");
        stopWords.add("wasn't");
        stopWords.add("way");
        stopWords.add("we");
        stopWords.add("we'd");
        stopWords.add("we'll");
        stopWords.add("we're");
        stopWords.add("we've");
        stopWords.add("welcome");
        stopWords.add("well");
        stopWords.add("went");
        stopWords.add("were");
        stopWords.add("weren't");
        stopWords.add("what");
        stopWords.add("what's");
        stopWords.add("whatever");
        stopWords.add("when");
        stopWords.add("whence");
        stopWords.add("whenever");
        stopWords.add("where");
        stopWords.add("where's");
        stopWords.add("whereafter");
        stopWords.add("whereas");
        stopWords.add("whereby");
        stopWords.add("wherein");
        stopWords.add("whereupon");
        stopWords.add("wherever");
        stopWords.add("whether");
        stopWords.add("which");
        stopWords.add("while");
        stopWords.add("whither");
        stopWords.add("who");
        stopWords.add("who's");
        stopWords.add("whoever");
        stopWords.add("whole");
        stopWords.add("whom");
        stopWords.add("whose");
        stopWords.add("why");
        stopWords.add("will");
        stopWords.add("willing");
        stopWords.add("wish");
        stopWords.add("with");
        stopWords.add("within");
        stopWords.add("without");
        stopWords.add("won't");
        stopWords.add("wonder");
        stopWords.add("would");
        stopWords.add("would");
        stopWords.add("wouldn't");
        stopWords.add("x");
        stopWords.add("y");
        stopWords.add("yes");
        stopWords.add("yet");
        stopWords.add("you");
        stopWords.add("you'd");
        stopWords.add("you'll");
        stopWords.add("you're");
        stopWords.add("you've");
        stopWords.add("your");
        stopWords.add("yours");
        stopWords.add("yourself");
        stopWords.add("yourselves");
        stopWords.add("z");
        stopWords.add("zero");

        /*        ClassLoader cl = getClass().getClassLoader();
        File stopWordsFile = new File (cl.getResource("../resources/stopWords.txt").getFile());
        try (BufferedReader br = new BufferedReader(new FileReader(stopWordsFile))) {
            String token;
            while((token = br.readLine()) != null){
                stopWords.add(token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
