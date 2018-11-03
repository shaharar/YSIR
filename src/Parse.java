import java.util.HashSet;

public class Parse {
    private String [] tokens; // the following data structure contains tokens
    private HashSet <String> capitalLetters = new HashSet<String>(); // the following data structure contains terms which start at capital letters all along the corpus
    private HashSet <String> terms = new HashSet<String>(); // the following data structure contains final terms

   public void removeStopWords (String docText){
       
   }
   public void parseDocText (String docText){

   }

   public void numParse (String text){

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
