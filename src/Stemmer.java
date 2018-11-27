import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.EnglishStemmer;

public class Stemmer {

    SnowballProgram stemmer;

    public Stemmer() {
        stemmer = new EnglishStemmer();
    }

    public String stem (String term){
        stemmer.setCurrent(term);
        if(stemmer.stem()){
            return stemmer.getCurrent();
        }
        return null;
    }
}
