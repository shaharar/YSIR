import java.util.HashSet;

public class Parse {
   private HashSet <String> capitalLetters = new HashSet<String>(); // the following data structure contains terms which start at capital letters all along the corpus
   private HashSet <String> terms = new HashSet<String>(); // the following data structure contains final terms

   public void removeStopWords (String docText){
       
   }
   public void parseDocText (String docText){

   }

   public void numParse (String text){

   }

   // the following function classifies lower case and upper case tokens
   public void lettersCase (String token){
       if (token.charAt(0) >= 65 && token.charAt(0) <= 90) //lower case
       {
           terms.add(token.toLowerCase());
       }
       else { //upper case
           capitalLetters.add(token.toUpperCase());
       }
   }


}
