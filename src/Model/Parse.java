package Model;

import javafx.beans.binding.IntegerBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class Parse {
    private String[] tokens; // the following data structure contains tokens
    HashMap<String, Term> terms; // the following data structure contains final terms in X docs
//    HashMap<String, HashMap<String, ArrayList <Integer>>> cityPositions;
    HashMap <String , ArrayList <String>> cityDocs;
    private HashSet <String> termsPerDoc; // the following data structure contains final terms in one doc
    private HashSet<String> stopWords; // the following data structure contains the stop words
    private String stopWordsPath;
    private HashMap<String,String> replaceMap;
    private Indexer indexer;
    private CityIndexer cityIndexer;
    private Stemmer stemmer;
    private StringBuilder replaceSb;
    StringBuilder sb;
    String docNo;
    boolean withStemming;
    private int currentIdx;
    int docsTotal;
    int docsInCollection;

/*    int maxPositions = 0;
    String maxCity = "";
    String cityPositions = "";
    String docNumber = "";*/
    int counter;//////////////////////////////////////test

   public Parse (boolean withStemming, String path, String corpusPath){
      terms = new HashMap<>();
//      cityPositions = new HashMap<>();
      cityDocs = new HashMap<>();
      termsPerDoc = new HashSet<>();
      stopWords = new HashSet<String>();
      stopWordsPath = corpusPath + "\\stop_words.txt";
      setStopWords();
      replaceMap = new HashMap<>();
      initReplaceMap();
       try {
           cityIndexer = new CityIndexer(path);
       } catch (IOException e) {
           cityIndexer = null;
       }
       stemmer = new Stemmer();
      indexer = new Indexer(path, withStemming);
      stemmer = new Stemmer();
      sb = new StringBuilder();
      docNo = "";
      this.withStemming = withStemming;
      currentIdx = 0;
      docsTotal = 0;
      docsInCollection = 0;


       counter = 1;//////////////////////////////////////////////////////*******************************
       //tokens = new String[]{"($56)","$2 trillion","First","50 thousand","about","Aviad","At first","66 1/2 Dollars","35 million U.S dollars","Amit and Aviad","20.6 m Dollars","$120 billion","100 bn Dollars","$30","40 Dollars","18.24","10,123","10,123,000","7 Trillion","34 2/3", "6-7", "-13", "step-by-step 10-part","70.5%","13.86 percent"};
   }

   // the following function parses the text of a specific document by the defined rules
   public void parseDocText(String docText, String docID, String city) {

       termsPerDoc = new HashSet<>();
       currentIdx = 0;
       this.docNo = docID;
       ArrayList <Integer> positionsInDoc = new ArrayList<>();
       int position = 0;
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
       docText = replaceChars(docText);
       replaceSb = new StringBuilder();
       tokens = docText.split(" ");
       int documentLength = tokens.length;
       String token;
       int maxTf = 0;
       String frequentTerm = "";

       while (currentIdx < tokens.length) {
          Term term = null;
          token = tokens[currentIdx];
          removeDelimiters(token);
          String nextToken = "";

          //cities
           if (token.equalsIgnoreCase(city) && cityIndexer != null && !city.equals("")){
               positionsInDoc.add(position);
               if (!cityDocs.containsKey(city.toUpperCase())) {
//                   cityDocs.put(city.toUpperCase(), new ArrayList<>);
//                   ArrayList<Integer> positions = new ArrayList<>();
//                   positions.add(position);
//                   cityPositions.get(city.toUpperCase()).put(docID, positions);
                   ArrayList <String> docs = new ArrayList<>();
                   docs.add(docID);
                   cityDocs.put(city.toUpperCase(), docs);
               }
               else if (!cityDocs.get(city.toUpperCase()).contains(docID))
               {
//                   if (cityPositions.get(city.toUpperCase()).containsKey(docID)){
//                       ArrayList <Integer> positions = cityPositions.get(city.toUpperCase()).get(docID);
//                       positions.add(position);
//                   }
//                   else{
//                       ArrayList<Integer> positions = new ArrayList<>();
//                       positions.add(position);
//                       cityPositions.get(city.toUpperCase()).put(docID, positions);
//                   }
                   cityDocs.get(city.toUpperCase()).add(docID);
               }
           }

          //numbers
          else if (token.matches("^[0-9]*+([,.][0-9]*?)*?$")) {
              if (currentIdx + 1 < tokens.length) {
                  nextToken = tokens[currentIdx + 1];
              }
              // token is a percent
              if (nextToken.equals("percent") || nextToken.equals("percentage")) {
                  term = percentage(token);
              }

              // token represents one of the following: a number or a price
              else if (nextToken.equalsIgnoreCase("Thousand") || nextToken.equalsIgnoreCase("Million") || nextToken.equalsIgnoreCase("Billion") || nextToken.equalsIgnoreCase("Trillion") || nextToken.contains("/")) {
                  // token is a price
                  if (((currentIdx + 2 < tokens.length) && (tokens[currentIdx + 2].equals("Dollars"))) || ((currentIdx + 3 < tokens.length) && (tokens[currentIdx + 2].equals("U.S") && tokens[currentIdx + 3].equals("dollars")))) {
                      term = prices(token);
                  }
                  // token is a number
                  else {
                      term = numbers(token);
                  }
              }
              // token is a price
              else if ((currentIdx + 1 < tokens.length && nextToken.equals("Dollars")) || ((currentIdx + 2 < tokens.length) && ((nextToken.equals("m") || nextToken.equals("bn")) && tokens[currentIdx + 2].equals("Dollars")))) {
                  term = prices(token);
              }
              // token is a date
              else if (currentIdx + 1 < tokens.length && months.contains(nextToken)) {
                  term = dates(token);
              } else {
                  term = numbers(token);
              }
          }

          //symbols
          else if (token.contains("%")) {
              term = percentage(token);
          } else if (token.contains("$")) {
              term = prices(token);
          } else if (token.contains("-")) {
              term = rangesAndExpressions(token);
          }

          //words
          else {
        //      if (!(stopWords.contains(token.toLowerCase())) || ((stopWords.contains(token.toLowerCase())) && ((token.equalsIgnoreCase("may")) || (token.equalsIgnoreCase("between")) || (token.equalsIgnoreCase("and"))))) { //if token is not a stop word
               if (token.length() > 2 && token.charAt(token.length() - 2) == '\'' && token.charAt(token.length() - 1) == 's'){
                   token = token.substring(0, token.length() - 2);
               }
                  //dates
                  if (months.contains(token)) {
                      term = dates(token);
                  }
                  //ranges
                  else if (token.equalsIgnoreCase("Between") && Pattern.compile("^[0-9] + ([,.][0-9]?)?$").matcher(token).find()) {
                      term = rangesAndExpressions(token);
                  }

                  //shortcuts
                  else if (token.equalsIgnoreCase("Mr") || token.equalsIgnoreCase("Mrs") || token.equalsIgnoreCase("Dr")){
                      term = shortcuts(token);
                  }

                  //measures
                  else if (token.equalsIgnoreCase("kilogram") || token.equalsIgnoreCase("kilobyte") || token.equalsIgnoreCase("kilobytes") || token.equalsIgnoreCase("kilograms") || token.equalsIgnoreCase("gram") || token.equalsIgnoreCase("byte") || token.equalsIgnoreCase("bytes")){
                      term = measures(token);
                  }

                  //just words
                  else if (!(stopWords.contains(token.toLowerCase())) && !(stopWords.contains(token.toUpperCase()))){
                      term = lettersCase(token);
                  }
                  else {
                      documentLength --;
                  }
/*              }
              else{
                  documentLength --;
              }*/
          }
         if (term != null){
             if (!term.docs.containsKey((docNo))){
                 term.docs.put(docNo, new AtomicInteger(1));
             }
             else {
                 term.updateTf(docNo);
             }
             int tf = term.getTf(docNo);
             if (tf > maxTf){
                 maxTf = tf;
                 frequentTerm = term.getTermStr();
            }
         }
         currentIdx++;
           position++;
      }
      String docCityPositions = "";
       for (Integer pos:positionsInDoc) {
           docCityPositions += pos + " ";
       }

       //////////////////////////////////////////////////////////////////////////////////////////////////////
/*       if (positionsInDoc.size() > maxPositions) {
           maxPositions = positionsInDoc.size();
           maxCity = city;
           cityPositions = docCityPositions;
           docNumber = docID;
       }*/
       //////////////////////////////////////////////////////////////////////////////////////////////////////


       positionsInDoc.clear();
      sb.append(docNo + ": " + termsPerDoc.size() + ", " + documentLength +", " + frequentTerm + ", " + maxTf + ", " + city + " [" + docCityPositions + "]" + "\n");
      docsTotal++;
      docsInCollection++;
      termsPerDoc.clear();






       if (docsTotal > 50000){
           System.out.println("finished parsing, start index "  + counter );///////////////////////////////////////////////////////////////test
           indexer.index(terms, docsInCollection, withStemming);
           terms.clear();
           if (cityIndexer != null){
               cityIndexer.index(cityDocs);
               cityDocs.clear();
           }
           indexer.writeDocsInfoToDisk(sb);
           sb = new StringBuilder();
           System.out.println("index done"+ "\n");////////////////////////////////////////////////////////////test
           docsTotal = 0;


       //    System.out.println("DocNo: " + docNumber + " Max positions: " + maxPositions + "positions: [" + cityPositions + "] " + "City: " + maxCity);
           counter++;
       }
   }

    private void removeDelimiters(String token) {
       if (token.length() == 0){
           return;
       }
       HashSet<String>delimiters = new HashSet<>();
       setDelimiters(delimiters);
       int i = 0;
       char c = '#';
       while (i < token.length() && delimiters.contains(""+ c)){
           c = token.charAt(i);
           i++;
       }
           if (token.length() > 0 && ((token.charAt(token.length() - 1) == '.' || token.charAt(token.length() - 1) == '-'))) {
               token.substring(i - 1, token.length() - 1);
           } else {
               token.substring(i - 1);
           }
    }

    private void setDelimiters(HashSet<String> delimiters){
       delimiters.add(".");
       delimiters.add(",");
       delimiters.add("#");
       delimiters.add("|");
       delimiters.add(":");
       delimiters.add("@");
       delimiters.add("!");
       delimiters.add("^");
       delimiters.add("&");
       delimiters.add("*");
       delimiters.add("'");
       delimiters.add("~");
       delimiters.add("`");
       delimiters.add(";");
    }


    private Term numbers (String token) {
      double num;
      Term term = null;
      //negative number
      if(token.startsWith("-")){
          try{
              num = Double.parseDouble(((token.substring(1)).replace(",",""))) * (-1);
          } catch (Exception e){
              return term;
          }
      }
      else {
          try{
              num = Double.parseDouble(token.replace(",",""));
          } catch (Exception e){
              return term;
          }
      }
             //num has a fraction after it - like '34 2/3'
             if (currentIdx + 1 < tokens.length && tokens[currentIdx + 1].contains("/")) {
                if (terms.containsKey(token + " " + tokens[currentIdx + 1])){
                    term = terms.get(token + " " + tokens[currentIdx + 1]);
                }
                else {
                    term = new Term();
                    term.setTermStr(token + " " + tokens[currentIdx + 1]);
                    terms.put(token + " " + tokens[currentIdx + 1], term);
                }
                 termsPerDoc.add(token + " " + tokens[currentIdx + 1]);
                 currentIdx++;
             }
            //num is less than 1,000
            else if (num < 1000) {
               //Thousand after num - like '50 Thousand'
               if (currentIdx + 1 < tokens.length && tokens[currentIdx + 1].equals("Thousand")) {
                   if (terms.containsKey(token + "K")){
                       term = terms.get(token + "K");
                   }
                   else {
                       term = new Term();
                       term.setTermStr(token + " " + tokens[currentIdx + 1]);
                       terms.put(token + "K", term);
                   }
                   termsPerDoc.add(token + "K");
                   currentIdx++;
               }
               //Million after num - like '50 Million'
               else if (currentIdx + 1 < tokens.length && tokens[currentIdx + 1].equals("Million")) {
                   if (terms.containsKey(token + "M")){
                       term = terms.get(token + "M");
                   }
                   else {
                       term = new Term();
                       term.setTermStr(token + "M");
                       terms.put(token + "M", term);
                   }
                   termsPerDoc.add(token + "M");
                   currentIdx++;
               }
               //Billion after num - like '50 Billion'
               else if (currentIdx + 1 < tokens.length && tokens[currentIdx + 1].equals("Billion")) {
                   if (terms.containsKey(token + "B")){
                       term = terms.get(token + "B");
                   }
                   else {
                       term = new Term();
                       term.setTermStr(token + "B");
                       terms.put(token + "B", term);
                   }
                   termsPerDoc.add(token + "B");
                   currentIdx++;
               }
               //Trillion after num - like '50 Trillion'
               else if (currentIdx + 1 < tokens.length && tokens[currentIdx + 1].equals("Trillion")) {
                   if((num * 1000) % 1000 == 0){
                       if (terms.containsKey((int)(num * 1000) + "B")){
                           term = terms.get((int)(num * 1000) + "B");
                       }
                       else {
                           term = new Term();
                           term.setTermStr((int)(num * 1000) + "B");
                           terms.put((int)(num * 1000) + "B", term);
                       }
                       termsPerDoc.add((int)(num * 1000) + "B");
                   }
                  else{
                       if (terms.containsKey((num * 1000) + "B")){
                           term = terms.get((num * 1000) + "B");
                       }
                       else {
                           term = new Term();
                           term.setTermStr((num * 1000) + "B");
                           terms.put((num * 1000) + "B", term);
                       }
                       termsPerDoc.add((num * 1000) + "B");
                   }
                   currentIdx++;
               }
               //just number - like '123'
               else {
                   if (terms.containsKey(token)){
                       term = terms.get(token);
                   }
                   else {
                       term = new Term();
                       term.setTermStr(token);
                       terms.put(token, term);
                   }
                   termsPerDoc.add(token);
               }
            }
            //num is up to 1,000
            else {
               //num between 1,000 to 999,000
               if (num < 1000000) {
                   if (terms.containsKey((num / 1000) + "K")){
                       term = terms.get((num / 1000) + "K");
                   }
                   else {
                       term = new Term();
                       term.setTermStr((num / 1000) + "K");
                       terms.put((num / 1000) + "K", term);
                   }
                   termsPerDoc.add((num / 1000) + "K");
               }
               //num between 1,000,000 to 999,000,000
               else if (num < 1000000000) {
                   if (terms.containsKey((num / 1000000) + "M")){
                       term = terms.get((num / 1000000) + "M");
                   }
                   else {
                       term = new Term();
                       term.setTermStr((num / 1000000) + "M");
                       terms.put((num / 1000000) + "M", term);
                   }
                   termsPerDoc.add((num / 1000000) + "M");
               }
               //num up to 1,000,000,000
               else {
                   if (terms.containsKey((num / 1000000000) + "B")){
                       term = terms.get((num / 1000000000) + "B");
                   }
                   else {
                       term = new Term();
                       term.setTermStr((num / 1000000000) + "B");
                       terms.put((num / 1000000000) + "B", term);
                   }
                   termsPerDoc.add((num / 1000000000) + "B");
               }
            }
       return term;
   }

   // the following function classifies lower case and upper case tokens and adds final terms to the compatible data structure.
   private Term lettersCase(String token) {
       Term term = null;
       token = removeDashes(token);
       token = stemming(token);
      if (token.charAt(0) >= 97 && token.charAt(0) <= 122) //lower case
      {
         if (terms.containsKey(token.toUpperCase()))
         {
            term = terms.get(token.toUpperCase());
            term.setTermStr(token.toLowerCase());
            termsPerDoc.add(term.getTermStr());
         }
         else {
             if (terms.containsKey(token.toLowerCase())){
                 term = terms.get(token.toLowerCase());
                 termsPerDoc.add(term.getTermStr());
             }
             else {
                 term = new Term();
                 term.setTermStr(token.toLowerCase());
                 terms.put(token.toLowerCase(), term);
                 termsPerDoc.add(token.toLowerCase());
             }
         }
      }
      else { //upper case
          //term doesn't exist in map as lower case
         if (!terms.containsKey(token.toLowerCase())){
             //term doesn't exist in map - it's a new term
             if (!terms.containsKey(token.toUpperCase())){
                 term = new Term();
                 term.setTermStr(token.toUpperCase());
                 terms.put(token.toUpperCase(), term);
                 termsPerDoc.add(token.toUpperCase());
             }
             //term already exists in map as upper case
             else{
                 term = terms.get(token.toUpperCase());
                 termsPerDoc.add(term.getTermStr());
             }
         }
         //term exists in map as lower case
         else{
             term = terms.get(token.toLowerCase());
             termsPerDoc.add(term.getTermStr());
         }
      }
      return term;
   }

    private String removeDashes(String token) {
        int i = 0;
        char c = ' ';
        if (token.length() > 0) {
           c = token.charAt(0);
       }
       while (i < token.length() && c == '-'){
            i++;
            c = token.charAt(i);
       }
       return token.substring(i);
    }

    // the following function adds final terms to the data structure in this format : NUMBER%.
   private Term percentage(String token) {
       Term term = null;
       if (terms.containsKey(token.replaceAll("%", "") + "%")){
           term = terms.get(token.replaceAll("%", "") + "%");
       }
       else {
           term = new Term();
           term.setTermStr(token.replaceAll("%", "") + "%");
           terms.put(token.replaceAll("%", "") + "%", term);
       }
       termsPerDoc.add(token.replaceAll("%", "") + "%");
       if((currentIdx + 1 < tokens.length && tokens[currentIdx+1].equals("percent")) || (currentIdx + 1 < tokens.length && tokens[currentIdx+1].equals("percentage"))){
         currentIdx++;
      }
       return term;
   }

   // the following function adds final terms to the data structure in one of these formats : PRICE Dollars, PRICE M Dollars
   private Term prices(String token) {
       Term term = null;
      double price;
      if (token.startsWith("$"))
      {
          try{
              token = token.replace(",","");
              price = Double.parseDouble(token.replace("$", ""));
          }catch (Exception e){
              term = new Term();
              term.setTermStr(token);
              terms.put(token, term);
              termsPerDoc.add(token);
              return term;
          }
      }
      else
      {
          try{
              price = Double.parseDouble(token.replace(",",""));
          }catch (Exception e){
              term = new Term();
              term.setTermStr(token);
              terms.put(token, term);
              termsPerDoc.add(token);
              return term;
          }
      }

      if (price >= 1000000 || (currentIdx + 1 < tokens.length && tokens[currentIdx + 1].equalsIgnoreCase("million")) || (currentIdx + 1 < tokens.length && tokens[currentIdx + 1].equalsIgnoreCase("billion")) || (currentIdx + 1 < tokens.length && tokens[currentIdx + 1].equalsIgnoreCase("trillion")) || (currentIdx + 1 < tokens.length && tokens[currentIdx + 1].equals("bn")) || (currentIdx + 1 < tokens.length && tokens[currentIdx + 1].equals("m")))
      {
         if ((currentIdx + 1 < tokens.length && tokens[currentIdx + 1].equalsIgnoreCase("million")) || (currentIdx + 1 < tokens.length && tokens[currentIdx + 1].equals("m")))
         {
             if (price == (int)(price)){
                 if (terms.containsKey((int)price + " M" + " Dollars")){
                     term = terms.get((int)price + " M" + " Dollars");
                 }
                 else {
                     term = new Term();
                     term.setTermStr((int)price + " M" + " Dollars");
                     terms.put((int)price + " M" + " Dollars", term);
                 }
                 termsPerDoc.add((int)price + " M" + " Dollars");
             }
            else{
                 if (terms.containsKey(price + " M" + " Dollars")){
                     term = terms.get(price + " M" + " Dollars");
                 }
                 else {
                     term = new Term();
                     term.setTermStr(price + " M" + " Dollars");
                     terms.put(price + " M" + " Dollars", term);
                 }
                 termsPerDoc.add(price + " M" + " Dollars");
             }
            if(currentIdx + 2 < tokens.length && tokens[currentIdx+2].equals("Dollars")) {
                currentIdx = currentIdx + 2;
            }
            else if ((currentIdx + 2 < tokens.length && tokens[currentIdx+2].equals("U.S")) && (currentIdx + 3 < tokens.length && tokens[currentIdx+3].equals("dollars"))) {
                currentIdx = currentIdx + 3;
            }
            else
                currentIdx++;
         }
         else if ((currentIdx + 1 < tokens.length && tokens[currentIdx + 1].equalsIgnoreCase("billion")) || (currentIdx + 1 < tokens.length && tokens[currentIdx + 1].equals("bn")))
         {
             if (price == (int)(price)){
                 if (terms.containsKey((int)(price * 1000) + " M" + " Dollars")){
                     term = terms.get((int)(price * 1000) + " M" + " Dollars");
                 }
                 else {
                     term = new Term();
                     term.setTermStr((int)(price * 1000) + " M" + " Dollars");
                     terms.put((int)(price * 1000) + " M" + " Dollars", term);
                 }
                 termsPerDoc.add((int)(price * 1000) + " M" + " Dollars");
             }
            else{
                 if (terms.containsKey((price * 1000) + " M" + " Dollars")){
                     term = terms.get((price * 1000) + " M" + " Dollars");
                 }
                 else {
                     term = new Term();
                     term.setTermStr((price * 1000) + " M" + " Dollars");
                     terms.put((price * 1000) + " M" + " Dollars", term);
                 }
                 termsPerDoc.add((price * 1000) + " M" + " Dollars");
             }
             if(currentIdx + 2 < tokens.length && tokens[currentIdx+2].equals("Dollars")){
                 currentIdx = currentIdx + 2;
             }
             else if ((currentIdx + 2 < tokens.length && tokens[currentIdx+2].equals("U.S")) && (currentIdx + 3 < tokens.length && tokens[currentIdx+3].equals("dollars"))) {
                 currentIdx = currentIdx + 3;
             }
             else
                 currentIdx++;
         }
         else if (currentIdx + 1 < tokens.length && tokens[currentIdx + 1].equalsIgnoreCase("trillion"))
         {
             if (price == (int)(price)){
                 if (terms.containsKey((int)(price * 1000000) + " M" + " Dollars")){
                     term = terms.get((int)(price * 1000000) + " M" + " Dollars");
                 }
                 else {
                     term = new Term();
                     term.setTermStr((int)(price * 1000000) + " M" + " Dollars");
                     terms.put((int)(price * 1000000) + " M" + " Dollars", term);
                 }
                 termsPerDoc.add((int)(price * 1000000) + " M" + " Dollars");
             }
            else{
                 if (terms.containsKey((price * 1000000) + " M" + " Dollars")){
                     term = terms.get((price * 1000000) + " M" + " Dollars");
                 }
                 else {
                     term = new Term();
                     term.setTermStr((price * 1000000) + " M" + " Dollars");
                     terms.put((price * 1000000) + " M" + " Dollars", term);
                 }
                 termsPerDoc.add((price * 1000000) + " M" + " Dollars");
             }
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
                 if (terms.containsKey((int)(price/1000000) + " M" + " Dollars")){
                     term = terms.get((int)(price/1000000) + " M" + " Dollars");
                 }
                 else {
                     term = new Term();
                     term.setTermStr((int)(price/1000000) + " M" + " Dollars");
                     terms.put((int)(price/1000000) + " M" + " Dollars", term);
                 }
                 termsPerDoc.add((int)(price/1000000) + " M" + " Dollars");
             }
            else{
                 if (terms.containsKey((price / 1000000) + " M" + " Dollars")){
                     term = terms.get((price / 1000000) + " M" + " Dollars");
                 }
                 else {
                     term = new Term();
                     term.setTermStr((price / 1000000) + " M" + " Dollars");
                     terms.put((price / 1000000) + " M" + " Dollars", term);
                 }
                 termsPerDoc.add((price / 1000000) + " M" + " Dollars");
             }
             if(currentIdx + 1 < tokens.length && tokens[currentIdx+1].equals("Dollars")){
                 currentIdx++;
             }
         }
      }
      else
      {
         if (token.startsWith("$") || (currentIdx + 1 < tokens.length && tokens[currentIdx + 1].equals("Dollars"))){
             if (price == (int)(price)){
                 if (terms.containsKey((int)price + " Dollars")){
                     term = terms.get((int)price + " Dollars");
                 }
                 else {
                     term = new Term();
                     term.setTermStr((int)price + " Dollars");
                     terms.put((int)price + " Dollars", term);
                 }
                 termsPerDoc.add((int)price + " Dollars");
             }
            else{
                 if (terms.containsKey(price + " Dollars")){
                     term = terms.get(price + " Dollars");
                 }
                 else {
                     term = new Term();
                     term.setTermStr(price + " Dollars");
                     terms.put(price + " Dollars", term);
                 }
                 termsPerDoc.add(price + " Dollars");
             }
             if(currentIdx + 1 < tokens.length && tokens[currentIdx + 1].equals("Dollars")){
                 currentIdx++;
             }
         }
         else if (currentIdx + 2 < tokens.length && tokens[currentIdx + 2].equals("Dollars"))
         {
             if (price == (int)(price)){
                 if (terms.containsKey((int)price + " " + tokens[currentIdx + 1] + " Dollars")){
                     term = terms.get((int)price + " " + tokens[currentIdx + 1] + " Dollars");
                 }
                 else {
                     term = new Term();
                     term.setTermStr((int)price + " " + tokens[currentIdx + 1] + " Dollars");
                     terms.put((int)price + " " + tokens[currentIdx + 1] + " Dollars", term);
                 }
                 termsPerDoc.add((int)price + " " + tokens[currentIdx + 1] + " Dollars");
             }
            else{
                 if (terms.containsKey(price + " " + tokens[currentIdx + 1] + " Dollars")){
                     term = terms.get(price + " " + tokens[currentIdx + 1] + " Dollars");
                 }
                 else {
                     term = new Term();
                     term.setTermStr(price + " " + tokens[currentIdx + 1] + " Dollars");
                     terms.put(price + " " + tokens[currentIdx + 1] + " Dollars", term);
                 }
                 termsPerDoc.add(price + " " + tokens[currentIdx + 1] + " Dollars");
             }
            currentIdx = currentIdx + 2;
         }
      }
       return term;
   }

   // the following function adds final terms to the data structure in one of these formats : MM-DD, YYYY-MM.
   private Term dates(String token) {
      String month = "";
      Term term = null;
      if (!(Pattern.compile("[0-9]").matcher(token).find())) { //check if the token contains digits, if not - it represents the month
         month = checkMonth(token);
         //'Month YYYY' format -> 'YYYY-MM'
          if (currentIdx + 1 < tokens.length && (tokens[currentIdx + 1].length() == 4) && isNumeric(tokens[currentIdx + 1])) {
             if (terms.containsKey(tokens[currentIdx + 1] + "-" + month)){
                 term = terms.get(tokens[currentIdx + 1] + "-" + month);
             }
             else {
                 term = new Term();
                 term.setTermStr(tokens[currentIdx + 1] + "-" + month);
                 terms.put(tokens[currentIdx + 1] + "-" + month, term);
             }
             termsPerDoc.add(tokens[currentIdx + 1] + "-" + month);
             currentIdx++;
         }
         //'Month DD' format -> 'MM-DD'
         else if (currentIdx + 1 < tokens.length && (tokens[currentIdx + 1].length() <= 2) && isNumeric(tokens[currentIdx + 1])) {
            if (tokens[currentIdx + 1].length() == 1){
                if (terms.containsKey(month + "-" + "0" + tokens[currentIdx + 1])){
                    term = terms.get(month + "-" + "0" + tokens[currentIdx + 1]);
                }
                else {
                    term = new Term();
                    term.setTermStr(month + "-" + "0" + tokens[currentIdx + 1]);
                    terms.put(month + "-" + "0" + tokens[currentIdx + 1], term);
                }
                termsPerDoc.add(month + "-" + "0" + tokens[currentIdx + 1]);
            }
            else{
                if (terms.containsKey(month + "-" + tokens[currentIdx + 1])){
                    term = terms.get(month + "-" + tokens[currentIdx + 1]);
                }
                else {
                    term = new Term();
                    term.setTermStr(month + "-" + tokens[currentIdx + 1]);
                    terms.put(month + "-" + tokens[currentIdx + 1], term);
                }
                termsPerDoc.add(month + "-" + tokens[currentIdx + 1]);
            }

             currentIdx++;
         }
      }
      //'DD Month' format -> 'MM-DD'
      else {
          if(currentIdx + 1 < tokens.length) {
              month = checkMonth(tokens[currentIdx + 1]);
              if (tokens[currentIdx].length() == 1) {
                  if (terms.containsKey(month + "-" + "0" + token)){
                      term = terms.get(month + "-" + "0" + token);
                  }
                  else {
                      term = new Term();
                      term.setTermStr(month + "-" + "0" + token);
                      terms.put(month + "-" + "0" + token, term);
                  }
                  termsPerDoc.add(month + "-" + "0" + token);
              } else {
                  if (terms.containsKey(month + "-" + token)){
                      term = terms.get(month + "-" + token);
                  }
                  else {
                      term = new Term();
                      term.setTermStr(month + "-" + token);
                      terms.put(month + "-" + token, term);
                  }
                  termsPerDoc.add(month + "-" + token);
              }
              currentIdx++;
          }
      }
      return term;
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
   private Term rangesAndExpressions(String token) {
       double firstNum, secondNum;
       Term term = null;
      //'Between number and number' format
      if(token.equals("Between")){
          try{
              firstNum = Double.parseDouble(tokens[currentIdx + 1]); //lower range
          } catch (Exception e){
              return term;
          }
         currentIdx++;
         while(!(Pattern.compile("[0-9]").matcher(tokens[currentIdx + 1]).find())){ //search the second number in the range
            currentIdx++;
         }
          try{
             if(currentIdx + 1 < tokens.length){
                 secondNum = Double.parseDouble(tokens[currentIdx + 1]); //upper range
                 if (terms.containsKey(firstNum + "-" + secondNum)){
                     term = terms.get(firstNum + "-" + secondNum);
                 }
                 else {
                     term = new Term();
                     term.setTermStr(firstNum + "-" + secondNum);
                     terms.put(firstNum + "-" + secondNum, term);
                 }
                 termsPerDoc.add(firstNum + "-" + secondNum);
             }
          } catch (Exception e){
              return term;
          }

         currentIdx++;
      }
      //negative number
       else if((token.charAt(0) == '-') && (token.length() > 1 && Character.isDigit(token.charAt(1)))) {
          int i = 2;
          //check if the negative number is a part of a range (has more than one '-') or just a negative number
          while ((i < token.length()) && (token.charAt(i) != '-') && (Character.isDigit(token.charAt(i)))) {
              i++;
          }
          if (i != token.length() && token.charAt(i) == '-') {
              if (terms.containsKey(token)) {
                  term = terms.get(token);
              } else {
                  term = new Term();
                  term.setTermStr(token);
                  terms.put(token, term);//add it as a range
              }
              termsPerDoc.add(token);
          } else {
              term = numbers(token); //calling to numbers parse function
          }
      }
      else{
          if (terms.containsKey(token)){
              term = terms.get(token);
          }
          else {
              term = new Term();
              term.setTermStr(token);
              terms.put(token, term);
          }
          termsPerDoc.add(token);
      }
      return term;
   }

   //------------------------------------our 2 rules----------------------------------
   //shortcuts - rule#1
   private Term shortcuts (String token) {
       Term term = null;

       if (token.equalsIgnoreCase("Mr")){
           token = "Mister";
       }

       else if (token.equalsIgnoreCase("Mrs") || token.equalsIgnoreCase("Miss") || token.equalsIgnoreCase("Ms")){
           token = "Mistress";
       }

       else if (token.equalsIgnoreCase("Dr")){
           token = "Doctor";
       }

       if (terms.containsKey(token)){
           term = terms.get(token);
           term.setTermStr(token);
       }
       else{
           term = new Term();
           term.setTermStr(token);
           terms.put(token, term);
       }
       termsPerDoc.add(token);

       return term;
   }

    //measures - rule#2
    private Term measures(String token) {
        Term term = null;

        if (token.equalsIgnoreCase("gram")){
            token = "gr";
        }

        else if (token.equalsIgnoreCase("kilogram") || token.equalsIgnoreCase("kilograms")){
            token = "Kg";
        }

        else if (token.equalsIgnoreCase("byte") || token.equalsIgnoreCase("bytes")){
            token = "B";
        }

        else if (token.equalsIgnoreCase("kilobyte") || token.equalsIgnoreCase("kilobytes")){
            token = "KB";
        }

        if (terms.containsKey(token)){
            term = terms.get(token);
            term.setTermStr(token);
        }
        else{
            term = new Term();
            term.setTermStr(token);
            terms.put(token, term);
        }
        termsPerDoc.add(token);

        return term;
    }

    private String stemming (String token){
        if(withStemming){
            return stemmer.stem(token);
        }
        return token;
    }


    public void finished() {
        System.out.println("'finished' called in parse");
       indexer.finished(terms, docsInCollection,withStemming);
       if (cityIndexer != null){
           cityIndexer.finished(cityDocs);
           cityIndexer.writeDictionaryToDisk();
       }
        indexer.writeDocsInfoToDisk(sb);
    }

    private boolean isNumeric(String str)
    {
        try {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private String replaceChars (String textForReplace){
       // StringBuilder sb = new StringBuilder();
        replaceSb = new StringBuilder();
        replaceSb.append(textForReplace);
        int from, to, nextFromKey;
        for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            from = replaceSb.indexOf(key, 0);
            while (from >= 0) {
                to = from + key.length();
                nextFromKey = from + value.length();
                replaceSb.replace(from, to, value);
                from = replaceSb.indexOf(key, nextFromKey);
            }
        }
        return replaceSb.toString();
    }


    private void initReplaceMap(){
        replaceMap.put("----------------------------------------------------------------------","");
        replaceMap.put("\t "," ");
        replaceMap.put("(","");
        replaceMap.put(")","");
        replaceMap.put("]","");
        replaceMap.put("[","");
        replaceMap.put("}","");
        replaceMap.put("{","");
        replaceMap.put("!","");
        replaceMap.put("?","");
        replaceMap.put(":","");
        replaceMap.put("=","");
        //replaceMap.put("@","");
        replaceMap.put("#","");
        replaceMap.put("'","");
        replaceMap.put("|","");
        replaceMap.put(";","");
        replaceMap.put(" -- "," ");
        replaceMap.put("- ","");
        replaceMap.put("-\n","");
        //replaceMap.put(" -","");
        replaceMap.put("\"","");
        replaceMap.put(".\"","");
        replaceMap.put("*","");
        replaceMap.put("\n"," ");
        replaceMap.put("-\n","");
        replaceMap.put(";\n","");
        replaceMap.put("\n\n"," ");
        replaceMap.put(".\n"," ");
        replaceMap.put(". \n"," ");
        replaceMap.put(", "," ");
        replaceMap.put(". "," ");
    }



   public static void main (String [] args){
/*      Parse p = new Parse(false);
       p.stopWordsPath = "D:\\documents\\users\\shaharar\\Downloads\\ST\\stop_words.txt";
       p.setStopWords();
*//*      Document doc = new Document();
      doc.setText("($56) $2 trillion, First, 50 Thousand, about, Aviad, At first. 66 1/2 Dollars, 35 million U.S dollars, Amit and Aviad, 20.6 m Dollars, $120 billion 100 bn Dollars $2 trillion $30 40 Dollars, 18.24 10,123, 10,123,000, 7 Trillion 34 2/3. 6-7 -13 step-by-step 10-part 70.5%, 13.86 percent");
       p.parseDocText(doc.getText());*//*
      for (String term:p.terms.keySet()) {
         System.out.println(term);
      }*/
   }


    // the following function saves defined stop words in the memory, according to the path the user gave.
    private void setStopWords (){
        File stopWordsFile = new File(stopWordsPath);

        try {
            BufferedReader br = new BufferedReader(new FileReader(stopWordsFile));
            String token;
            while((token = br.readLine()) != null){
              //  stopWords.add(token.substring(0,token.indexOf("\r")));
                stopWords.add(token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

       /* stopWords.add("a");
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
        stopWords.add("zero");*/

    }


    public void reset() {
        indexer.reset();
        terms.clear();
        termsPerDoc.clear();
        stopWords.clear();
        replaceMap.clear();
        replaceSb = new StringBuilder();
        sb = new StringBuilder();
        stemmer = new Stemmer();
        //indexer = new Indexer("");
    }
}
