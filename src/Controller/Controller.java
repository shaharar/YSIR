package Controller;

import Model.Model;
import View.View;

public class Controller {

    View view;
    Model model;

    public void run(String corpusPath, String postingPath) {
        model.run(corpusPath,postingPath);
    }

    public void stemming(boolean selected) {
        model.stemming(selected);
    }

    public void reset(String postingPath) {
        model.reset(postingPath);
    }
}
