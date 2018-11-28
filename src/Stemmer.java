import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

public class Stemmer {

    SnowballStemmer stemmer;

    public Stemmer() {
        stemmer = new englishStemmer();
    }

    public String stem (String token){
        stemmer.setCurrent(token);
        if(stemmer.stem()){
            return stemmer.getCurrent();
        }
        return null;
    }
}
