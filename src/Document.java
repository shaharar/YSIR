import java.io.File;
import java.util.Date;

public class Document {

    private String text; //<TEXT>
    private String content; //from <Doc> to <Doc>
    private File file; //parent file
    private String docNo; //<DOCNO>
    private Date date; //<DATE1>
    private String header; //<TI>
    private int maxTF; //max term's frequency
    private int numOfUniqueTerms;

    //Constructor
    public Document() {
    }

    //Getters
    public String getText() {
        return text;
    }

    public String getContent() {
        return content;
    }

    public File getFile() {
        return file;
    }

    public String getDocNo() {
        return docNo;
    }

    public Date getDate() {
        return date;
    }

    public String getHeader() {
        return header;
    }

    //Setters
    public void setText(String text) {
        this.text = text;
    }
}
