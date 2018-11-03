import java.util.HashSet;

public class Parse {
   private HashSet <String> capitalLetters = new HashSet<String>();
   private HashSet <String> terms = new HashSet<String>();

   public void removeStopWords (String docText){
       
   }
   public void parseDocText (String docText){

   }

   public void numParse (String text){

   }

   public void lettersCase (String token){
       if (token.charAt(0) >= 65 && token.charAt(0) <= 90){
           terms.add(token.toLowerCase());
       }
       else {
           capitalLetters.add(token.toUpperCase());
       }
   }


}
