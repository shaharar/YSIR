package Model;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class WordsSemantic {
    private String baseUrl;
    private HashMap<String,ArrayList<String>> semanticWords;
    private Object[] jsonResults;

    public WordsSemantic() {
        baseUrl = "https://api.datamuse.com/words?ml=";
        semanticWords = new HashMap<>();
    }

    public HashMap<String,ArrayList<String>> connectToApi (String query){
        String url;
        String[] queryTerms = query.split(" ");
        for(String term : queryTerms){
            Response response = null;
            url =  baseUrl + term;
            Request request = new Request.Builder().url(url).build();
            OkHttpClient client = new OkHttpClient();
            try {
                response = client.newCall(request).execute();
            }catch (IOException e){
                e.printStackTrace();
            }
            Object obj = null;
            try{
                String responseStr = response.body().string();
                try {
                    obj = new JSONParser().parse(responseStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                response.close();
            } catch (IOException e){
                e.printStackTrace();
            }
            if(obj != null) {
                int idx = 0;
                String word = "";
                jsonResults = ((JSONArray) obj).toArray();
                semanticWords.put(term, new ArrayList<>());
                for (Object o : jsonResults) {
                    JSONObject jo = (JSONObject) (o);
                    word = (String) (jo).get("word");
                    semanticWords.get(term).add(idx, word);
                    if (idx == 2) {
                        break;
                    }
                    idx++;
                }
            }
        }
        return semanticWords;
    }
}
