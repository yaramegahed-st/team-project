package view.style;

import java.awt.*;
import java.io.File;

public class FontLoader {

    public static Font loadDamion(float size) {
        return load("src/main/resources/fonts/Damion-Regular.ttf", size);
    }

    public static Font loadAdlam(float size) {
        return load("src/main/resources/fonts/ADLaMDisplay-Regular.ttf", size);
    }

    // shared loading logic
    private static Font load(String path, float size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File(path))
                    .deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font(Font.SERIF, Font.PLAIN, (int) size);   // fallback if loading fails
        }
    }
}
