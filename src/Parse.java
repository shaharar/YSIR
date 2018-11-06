import java.io.*;
import java.util.HashSet;
import java.util.regex.Pattern;

public class Parse {
   private String[] tokens; // the following data structure contains tokens
   private HashSet<String> capitalLetters = new HashSet<String>(); // the following data structure contains terms which start at capital letters all along the corpus
   private HashSet<String> terms = new HashSet<String>(); // the following data structure contains final terms
   private HashSet<String> stopWords = new HashSet<String>(); // the following data structure contains the stop words

   public void parseDocText(String docText) {
      tokens = docText.split(" |\\. |\\, ");
      String token;
      for (int i = 0; i < tokens.length; i++) {
          token = tokens[i];
          //numbers
         if (Pattern.compile("^[0-9] + ([,.][0-9]?)?$").matcher(token).find()){
             String prevToken = tokens[i-1];
             String nextToken = tokens[i+1];
             

         }
         //symbols
         else if (token.contains("%")) {
             percentage(token);
          }
          else if (token.contains("$")) {
              prices(token,i+1);
          }
          else if (token.contains("-")) {
              rangesAndExpressions(token,i+1);
          }


         else{
             if(!(stopWords.contains(token))){ //if token is not a stop word


             }
         }
      }
   }

   private void setStopWords (){
       ClassLoader cl = getClass().getClassLoader();
       File stopWordsFile = new File (cl.getResource("../resources/stopWords.txt").getFile());
       try (BufferedReader br = new BufferedReader(new FileReader(stopWordsFile))) {
           String token;
           while((token = br.readLine()) != null){
               stopWords.add(token);
           }
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }


   public void numbers (String token, int nextIdx) {
      double num = Double.parseDouble(token);
      //num is less than 1,000
      if (num < 1000) {
         //num has a fraction after it - like '34 2/3'
         if (tokens[nextIdx].contains("/")) {
            terms.add(token + tokens[nextIdx]);
         }
         //Thousand after num - like '50 Thousand'
         else if (tokens[nextIdx].equals("Thousand")) {
            terms.add(token + "K");
         }
         //Million after num - like '50 Million'
         else if (tokens[nextIdx].equals("Million")) {
            terms.add(token + "M");
         }
         //Billion after num - like '50 Billion'
         else if (tokens[nextIdx].equals("Billion")) {
            terms.add(token + "B");
         }
         //Trillion after num - like '50 Trillion'
         else if (tokens[nextIdx].equals("Trillion")) {
            terms.add((num * 1000) + "B");
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

   // the following function classifies lower case and upper case tokens and adds final terms to the compatible data structure
   public void lettersCase(String token) {
      if (token.charAt(0) >= 65 && token.charAt(0) <= 90) //lower case
      {
         terms.add(token.toLowerCase());
      } else { //upper case
         capitalLetters.add(token.toUpperCase());
      }
   }

   // the following function adds final terms to the data structure in this format : NUMBER%
   public void percentage(String token) {
      terms.add(token.replaceAll("%", "") + "%");
   }

   // the following function adds final terms to the data structure in one of these formats : PRICE Dollars, PRICE M Dollars
   public void prices(String token, int idx) {
      double price;
      if (token.startsWith("$"))
      {
         price = Double.parseDouble(token.replaceFirst("$", ""));
      }
      else
      {
         price = Double.parseDouble(token);
      }
      if (price >= 1000000 || tokens[idx].equals("million") || tokens[idx].equals("billion") || tokens[idx].equals("trillion") || tokens[idx].equals("bn") || tokens[idx].equals("m") )
      {
         if (tokens[idx].equals("million") || token.contains("m") )
         {
            terms.add(price + "M" + "Dollars");
         }
         else if (tokens[idx].equals("billion") || token.contains("bn"))
         {
            terms.add((price * 1000) + "M" + "Dollars");
         }
         else if (tokens[idx].equals("trillion"))
         {
            terms.add((price * 1000000) + "M" + "Dollars");
         }
         else
         {
            terms.add((price / 1000000) + "M" + "Dollars");
         }
      }
      else
      {
         if (token.startsWith("$")){
            price = Double.parseDouble(token.replaceFirst("$", ""));
            terms.add(price + "Dollars");
         }
         else if (tokens[idx].equals("Dollars"))
         {
            terms.add(price + "Dollars");
         }
         else if (tokens[idx + 1].equals("Dollars"))
         {
            terms.add(price + tokens[idx + 1] + "Dollars");
         }
      }
   }

   // the following function adds final terms to the data structure in one of these formats : MM-DD, YYYY-MM
   public void dates(String token, int nextIdx) {
      String month = "";
      if (!(Pattern.compile("[0-9]").matcher(token).find())) { //check if the token contains digits, if not - it represents the month
         month = checkMonth(token);
         //'Month YYYY' format -> 'YYYY-MM'
         if ((tokens[nextIdx].length() == 4)) {
            terms.add(tokens[nextIdx] + "-" + month);
         }
         //'Month DD' format -> 'MM-DD'
         else if ((tokens[nextIdx].length() <= 2)) {
            terms.add(month + "-" + tokens[nextIdx]);
         }
      }
      //'DD Month' format -> 'MM-DD'
      else {
         month = checkMonth(tokens[nextIdx]);
         terms.add(month + "-" + token);
      }
   }

   //help function for 'dates' - converts the Month from letters to digits.
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

   public void rangesAndExpressions(String token, int nextIdx) {
      //'Word-word' or 'Word-word-word' format
      if (!(Pattern.compile("[0-9]").matcher(token).find()))
         terms.add(token);
      //'Between number and number' format
      if(token.equals("Between")){
         double firstNum = Double.parseDouble(tokens[nextIdx]); //lower range
         nextIdx++;
         while(!(Pattern.compile("[0-9]").matcher(tokens[nextIdx]).find())){ //search the second number in the range
            nextIdx++;
         }
         double secondNum = Double.parseDouble(tokens[nextIdx]); //upper range
         terms.add(firstNum + "-" + secondNum);
      }

      //'Number-number'



      //'Number-word' or 'Word-Number'




   }
}