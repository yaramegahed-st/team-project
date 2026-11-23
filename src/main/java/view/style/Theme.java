package view.style;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Theme {
    private static boolean dark = false;
    private static final List<Runnable> listeners = new ArrayList<>();

    public static boolean isDark() { return dark; }

    public static void setDark(boolean d) {
        dark = d;
        for (Runnable r : new ArrayList<>(listeners)) r.run();    // tell every subscribed panel to restyle
    }

    public static void addListener(Runnable r) { listeners.add(r); }

    // Palette (approximated from your mockups; adjust the hex values to taste)
    public static Color background()    { return Color.decode(dark ? "#1B4568" : "#F1FBFE"); }
    public static Color pill()          { return Color.decode(dark ? "#F1FBFE" : "#43687F"); }
    public static Color pillText()      { return Color.decode(dark ? "#1B4568" : "#FFFFFF"); }
    public static Color headingFill()   { return Color.decode(dark ? "#F1FBFE" : "#8AD1D1"); }
    public static Color headingStroke() { return Color.decode(dark ? "#0F2A40" : "#1F3A4D"); }
    public static Color flashFill()     { return Color.decode(dark ? "#DDF3F7" : "#386782"); }
    public static Color flashStroke()   { return Color.decode(dark ? "#8AD1D1" : "#C7C7C7"); }
    public static Color containerFill()   { return Color.decode(dark ? "#F1FBFE" : "#C9DFEA"); }
    public static Color containerBorder() { return Color.decode("#43687F"); }
    public static Color tileFill()        { return Color.decode(dark ? "#1B4568" : "#43687F"); }
    public static Color aboutStroke()     { return Color.decode(dark ? "#8AD1D1" : "#000000"); }
    public static Color bodyFill()        { return Color.decode(dark ? "#1B4568" : "#8AD1D1"); }   // text inside containers
    public static Color bodyStroke()      { return Color.decode(dark ? "#1B4568" : "#1F3A4D"); }
    public static Color promptFill()      { return Color.decode(dark ? "#C9DFEA" : "#A0DADA"); }
    public static Color textColor()       { return Color.decode(dark ? "#DDF3F7" : "#43687F"); }   // plain labels on the background
    public static Color promptBorder() { return Color.decode(dark ? "#8AD1D1" : "#2E4A5A"); }
}
