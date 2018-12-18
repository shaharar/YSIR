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
    HashMap <String , ArrayList <String>> cityDocs;
    private HashSet <String> termsPerDoc; // the following data structure contains final terms in one doc
    private HashSet<String> stopWords; // the following data structure contains the stop words
    private HashSet<String>delimiters;
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


    public Parse (boolean withStemming, String path, String corpusPath){
      terms = new HashMap<>();
      cityDocs = new HashMap<>();
      termsPerDoc = new HashSet<>();
      stopWords = new HashSet<String>();
      delimiters = new HashSet<>();
       setDelimiters(delimiters);
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
           token = removeDashes(token);
           token = removeDelimiters(token);
          String nextToken = "";

          //cities
           if (token.equalsIgnoreCase(city) && cityIndexer != null && !city.equals("")){
               positionsInDoc.add(position);
               if (!cityDocs.containsKey(city.toUpperCase())) {
                   ArrayList <String> docs = new ArrayList<>();
                   docs.add(docID);
                   cityDocs.put(city.toUpperCase(), docs);
               }
               else if (!cityDocs.get(city.toUpperCase()).contains(docID))
               {
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
               //relevance - our rule
               if (token.length() > 2 && (token.charAt(token.length() - 2) == '\'' || token.charAt(token.length() - 2) == '`') && token.charAt(token.length() - 1) == 's'){
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
           docCityPositions += pos + "  ";
       }

       positionsInDoc.clear();
      sb.append(docNo + ": " + termsPerDoc.size() + ", " + documentLength +", " + frequentTerm + ", " + maxTf + ", " + city + " [  " + docCityPositions + "]" + "\n");
      docsTotal++;
      docsInCollection++;
      termsPerDoc.clear();

       if (docsTotal > 50000){
           indexer.index(terms, docsInCollection, withStemming);
           terms.clear();
           if (cityIndexer != null){
               cityIndexer.index(cityDocs);
               cityDocs.clear();
           }
           indexer.writeDocsInfoToDisk(sb);
           sb = new StringBuilder();
           docsTotal = 0;
       }
   }


   public HashMap<String,Integer> termsPerQuery (String queryText){
       HashMap<String,Integer> termsPerQuery = new HashMap<>();
       currentIdx = 0;
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
       queryText = replaceChars(queryText);
       replaceSb = new StringBuilder();
       tokens = queryText.split(" ");
       int queryLength = tokens.length;
       String token;

       while (currentIdx < tokens.length) {
           Term term = null;
           token = tokens[currentIdx];
           token = removeDashes(token);
           token = removeDelimiters(token);
           String nextToken = "";

            //numbers
           if (token.matches("^[0-9]*+([,.][0-9]*?)*?$")) {
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
               //relevance - our rule
               if (token.length() > 2 && (token.charAt(token.length() - 2) == '\'' || token.charAt(token.length() - 2) == '`') && token.charAt(token.length() - 1) == 's'){
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
                   queryLength --;
               }
           }
           currentIdx++;
           if (termsPerQuery.containsKey(term.getTermStr())){
               termsPerQuery.replace(term.getTermStr(),termsPerQuery.get(term.getTermStr()) + 1); //update tf
           }
           else{
               termsPerQuery.put(term.getTermStr(),0); //update tf
           }
       }

       return termsPerQuery;
   }




    private String removeDelimiters(String token) {
       if (token.length() == 0){
           return token;
       }
       else {
           if (!delimiters.contains(""+token.charAt(0))){
               return token;
           }
           int i = 0;
           char c;
           while (i < token.length()){
               c = token.charAt(i);
                if(!delimiters.contains("" + c)) {
                    break;
                }
               i++;
           }
           if (i == token.length()){
               return "";
           }
           if ((token.charAt(token.length() - 1) == '.' || token.charAt(token.length() - 1) == '-')) {
               token = token.substring(i - 1, token.length() - 1);
           } else {
               token = token.substring(i);
           }
       }
        return token;
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
       indexer.finished(terms, docsInCollection,withStemming);
       if (cityIndexer != null){
           cityIndexer.finished(cityDocs);
           cityIndexer.writeDictionaryToDisk();
       }
        indexer.writeDocsInfoToDisk(sb);
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

    // the following function saves defined stop words in the memory, according to the path the user gave.
    private void setStopWords (){
        File stopWordsFile = new File(stopWordsPath);

        try {
            BufferedReader br = new BufferedReader(new FileReader(stopWordsFile));
            String token;
            while((token = br.readLine()) != null){
                stopWords.add(token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String removeDashes(String token) {
        if (token.length() == 0){
            return token;
        }
        else {
            //token is negative number - shouldn't remove dashes
            if (token.length() > 1 && token.charAt(0) == '-' && Character.isDigit(token.charAt(1))) {
                return token;
            }

            if (token.charAt(0) != '-') {
                return token;
            }
            int i = 0;
            char c;
            while (i < token.length()) {
                c = token.charAt(i);
                if (c != '-') {
                    break;
                }
                i++;
            }
            if (i == token.length()) {
                return "";
            } else {
                return token.substring(i);
            }
        }
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

    private void setDelimiters(HashSet<String> delimiters){
        delimiters.add(".");
        delimiters.add(",");
        delimiters.add("+");
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
        delimiters.add("/");
    }

    private String replaceChars (String textForReplace){
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

    public int getDocsInCollection() {
        return docsInCollection;
    }

    //clear all the data structures
    public void reset() {
        indexer.reset();
        terms.clear();
        termsPerDoc.clear();
        stopWords.clear();
        replaceMap.clear();
        replaceSb = new StringBuilder();
        sb = new StringBuilder();
        stemmer = new Stemmer();
    }

    public int getDicSize() {
        return indexer.getDicSize();
    }
}
