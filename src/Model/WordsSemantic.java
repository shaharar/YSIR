/*
package Model;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WordsSemantic {
    private String baseUrl;
    private HashMap<String,ArrayList<String>> semanticWords;
    private List<Object> jsonResults;

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
                try{
                    String responseStr = response.body().string();
                    obj = new JSONParser().parse(responseStr);
                    response.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
                if(obj != null){
                    int idx = 0;
                    String word = "";
                    jsonResults = ((JSONArray) obj).toList();
                    semanticWords.put(term, new ArrayList<>());
                    for (Object o : jsonResults){
                        JSONObject jo = (JSONObject)(o);
                        word = (String)(jo).get("word");
                        semanticWords.get(term).add(idx, word);
                        if(idx == 5){
                            break;
                        }
                        idx++;
                    }
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        return semanticWords;
    }
}
*/
