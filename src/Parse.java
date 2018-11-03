import java.util.HashSet;

public class Parse {
    private String [] tokens; // the following data structure contains tokens
    private HashSet <String> capitalLetters = new HashSet<String>(); // the following data structure contains terms which start at capital letters all along the corpus
    private HashSet <String> terms = new HashSet<String>(); // the following data structure contains final terms

   public void removeStopWords (String docText){
       
   }
   public void parseDocText (String docText){

   }

   public void numbersCase (String token, int nextIdx){
      double num = Double.parseDouble(token);
      //num is less than 1,000
      if(num < 1000){
         //num has a fraction after it - like '34 2/3'
         if(tokens[nextIdx].contains("/")){
            terms.add(token + tokens[nextIdx]);
         }
         //Thousand after num - like '50 Thousand'
         else if(tokens[nextIdx].equals("Thousand")){
            terms.add(token+"K");
         }
         //Million after num - like '50 Million'
         else if(tokens[nextIdx].equals("Million")){
            terms.add(token+"M");
         }
         //Billion after num - like '50 Billion'
         else if(tokens[nextIdx].equals("Billion")){
            terms.add(token+"B");
         }
         //Trillion after num - like '50 Trillion'
         else if(tokens[nextIdx].equals("Trillion")){
            terms.add((num*1000)+"B");
         }
         //just number - like '123'
         else {
            terms.add(token);
         }
      }
      //num is up to 1,000
      else{
         //num between 1,000 to 999,000
         if (num < 1000000){
            terms.add((num/1000) +"K");
         }
         //num between 1,000,000 to 999,000,000
         else if (num < 1000000000){
            terms.add((num/1000000) +"M");
         }
         //num up to 1,000,000,000
         else{
            terms.add((num/1000000000) +"B");
         }
      }
   }

   // the following function classifies lower case and upper case tokens and adds final terms to the compatible data structure
   public void lettersCase (String token){
       if (token.charAt(0) >= 65 && token.charAt(0) <= 90) //lower case
       {
           terms.add(token.toLowerCase());
       }
       else { //upper case
           capitalLetters.add(token.toUpperCase());
       }
   }

   // the following function adds final terms to the data structure in this format : NUMBER%
   public void percentage (String token){
       terms.add(token + "%");
   }

   // the following function adds final terms to the data structure in one of these formats : PRICE Dollars, PRICE M Dollars
   public void prices (String token, int idx){

   }


}
