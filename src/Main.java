import java.io.IOException;

public class Main {

    public static void main (String [] args) throws IOException {
        ReadFile rf = new ReadFile(false, "d:\\documents\\users\\shaharar\\Downloads\\test");
        try {
            long startTime 	= System.nanoTime();
            rf.getFilesFromDir("d:\\documents\\users\\shaharar\\Downloads\\corpus\\corpus");
            long finishTime 	= System.nanoTime();
            long totalTime = (long)((finishTime - startTime)/1000000.0);
            System.out.println("Total time:  " + totalTime/60000.0 + " min");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
