import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;

public class Parse {
   private String[] tokens; // the following data structure contains tokens
   private HashSet<String> terms = new HashSet<String>(); // the following data structure contains final terms
   private HashSet<String> stopWords = new HashSet<String>(); // the following data structure contains the stop words
   private int currentIdx = 0;

   // the following function parses the text of a specific document by the defined rules
   public void parseDocText(String docText) {
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
      tokens = docText.split(" |\\. |\\, ");
      String token;
      while (currentIdx < tokens.length){
          token = tokens[currentIdx];
          //numbers
         if (Pattern.compile("^[0-9] + ([,.][0-9]?)?$").matcher(token).find()) {
            String nextToken = tokens[currentIdx + 1];
            // token is a percent
            if (nextToken.equals("percent") || nextToken.equals("percentage")) {
               percentage(token);
            }
            // token represents one of the following: a number or a price
            if (nextToken.equalsIgnoreCase("Thousand") || nextToken.equalsIgnoreCase("Million") || nextToken.equalsIgnoreCase("Billion") || nextToken.equalsIgnoreCase("Trillion") || nextToken.contains("/")) {
               // token is a price
               if (tokens[currentIdx + 2].equals("Dollars") || (tokens[currentIdx + 2].equals("U.S") && tokens[currentIdx + 3].equals("dollars"))) {
                  prices(token);
               }
               // token is a number
               else {
                  numbers(token);
               }
            }
            // token is a price
            if (nextToken.equals(nextToken.equals("Dollars") || ((nextToken.equals("m") || nextToken.equals("bn")) && tokens[currentIdx + 2].equals("Dollars")))) {
               prices(token);
            }
            // token is a date
            if (months.contains(nextToken)) {
               dates(token);
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
                 if(months.contains(token)){
                     dates(token);
                 }
                 else if (token.equalsIgnoreCase("Between") && Pattern.compile("^[0-9] + ([,.][0-9]?)?$").matcher(token).find()){
                     rangesAndExpressions(token);
                 }
                 else {
                     lettersCase(token);
                 }
             }
         }
         currentIdx++;
      }
   }
// the following function saves defined stop words in the memory
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


   public void numbers (String token) {
      double num;
      //negative number
      if(token.startsWith("-")){
         num = Double.parseDouble(token.substring(1)) * (-1);
      }
      else
         num = Double.parseDouble(token);
      //num is less than 1,000
      if (num < 1000) {
         //num has a fraction after it - like '34 2/3'
         if (tokens[currentIdx + 1].contains("/")) {
            terms.add(token + tokens[currentIdx + 1]);
         }
         //Thousand after num - like '50 Thousand'
         else if (tokens[currentIdx + 1].equals("Thousand")) {
            terms.add(token + "K");
         }
         //Million after num - like '50 Million'
         else if (tokens[currentIdx + 1].equals("Million")) {
            terms.add(token + "M");
         }
         //Billion after num - like '50 Billion'
         else if (tokens[currentIdx + 1].equals("Billion")) {
            terms.add(token + "B");
         }
         //Trillion after num - like '50 Trillion'
         else if (tokens[currentIdx + 1].equals("Trillion")) {
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

   // the following function classifies lower case and upper case tokens and adds final terms to the compatible data structure.
   public void lettersCase(String token) {
      if (token.charAt(0) >= 65 && token.charAt(0) <= 90) //lower case
      {
         if (terms.contains(token.toUpperCase()))
         {
            terms.remove(token);
            terms.add(token.toLowerCase());
         }
      } else { //upper case
         if (!terms.contains(token.toLowerCase())){
            terms.add(token.toUpperCase());
         }
      }
   }

   // the following function adds final terms to the data structure in this format : NUMBER%.
   public void percentage(String token) {
      terms.add(token.replaceAll("%", "") + "%");
   }

   // the following function adds final terms to the data structure in one of these formats : PRICE Dollars, PRICE M Dollars
   public void prices(String token) {
      double price;
      if (token.startsWith("$"))
      {
         price = Double.parseDouble(token.replaceFirst("$", ""));
      }
      else
      {
         price = Double.parseDouble(token);
      }
      if (price >= 1000000 || tokens[currentIdx + 1].equalsIgnoreCase("million") || tokens[currentIdx + 1].equalsIgnoreCase("billion") || tokens[currentIdx + 1].equalsIgnoreCase("trillion") || tokens[currentIdx + 1].equals("bn") || tokens[currentIdx + 1].equals("m") )
      {
         if (tokens[currentIdx + 1].equalsIgnoreCase("million") || token.contains("m") )
         {
            terms.add(price + "M" + "Dollars");
         }
         else if (tokens[currentIdx + 1].equalsIgnoreCase("billion") || token.contains("bn"))
         {
            terms.add((price * 1000) + "M" + "Dollars");
         }
         else if (tokens[currentIdx + 1].equalsIgnoreCase("trillion"))
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
            terms.add(price + "Dollars");
         }
         else if (tokens[currentIdx + 2].equals("Dollars"))
         {
            terms.add(price + tokens[currentIdx + 1] + "Dollars");
         }
         else if (tokens[currentIdx + 1].equals("Dollars"))
         {
            terms.add(price + "Dollars");
         }
      }
   }

   // the following function adds final terms to the data structure in one of these formats : MM-DD, YYYY-MM.
   public void dates(String token) {
      String month = "";
      if (!(Pattern.compile("[0-9]").matcher(token).find())) { //check if the token contains digits, if not - it represents the month
         month = checkMonth(token);
         //'Month YYYY' format -> 'YYYY-MM'
         if ((tokens[currentIdx + 1].length() == 4)) {
            terms.add(tokens[currentIdx + 1] + "-" + month);
         }
         //'Month DD' format -> 'MM-DD'
         else if ((tokens[currentIdx + 1].length() <= 2)) {
            terms.add(month + "-" + tokens[currentIdx + 1]);
         }
      }
      //'DD Month' format -> 'MM-DD'
      else {
         month = checkMonth(tokens[currentIdx + 1]);
         terms.add(month + "-" + token);
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
      //'Word-word' or 'Word-word-word' format
      if (!(Pattern.compile("[0-9]").matcher(token).find()))
         terms.add(token);
      //'Between number and number' format
      if(token.equals("Between")){
         double firstNum = Double.parseDouble(tokens[currentIdx + 1]); //lower range
         currentIdx++;
         while(!(Pattern.compile("[0-9]").matcher(tokens[currentIdx + 1]).find())){ //search the second number in the range
            currentIdx++;
         }
         double secondNum = Double.parseDouble(tokens[currentIdx + 1]); //upper range
         terms.add(firstNum + "-" + secondNum);
      }
      //negative number
       if((token.charAt(0) == '-') && (Character.isDigit(token.charAt(1)))){
         int i=2;
         //check if the negative number is a part of a range (has more than one '-') or just a negative number
         while ((i < token.length()) && (token.charAt(i) != '-') && (Character.isDigit(token.charAt(i)))) {
            i++;
         }
         if(token.charAt(i) == '-'){
            terms.add(token); //add it as a range
         }
         else{
            numbers(token); //calling to numbers parse function
         }
      }

      //'Number-number'



      //'Number-word' or 'Word-Number'

   }

   public static void main (String [] args){

   }
}