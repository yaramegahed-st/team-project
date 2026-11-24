package view.style;

import javax.swing.*;

public class IconLoader {
    private static final String DIR = "src/main/resources/icons/";

    public static ImageIcon load(String name) {
        return new ImageIcon(DIR + name);        // file-path version for now
    }
}
