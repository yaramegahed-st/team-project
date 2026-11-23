package view.style;

import java.awt.*;

public class Easing {

    // --- Constant speed ---
    public static double linear(double t) { return t; }

    // --- Quadratic (gentle) ---
    public static double easeInQuad(double t)    { return t * t; }
    public static double easeOutQuad(double t)   { return 1 - (1 - t) * (1 - t); }
    public static double easeInOutQuad(double t) {
        return t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2;
    }

    // --- Cubic (stronger) ---
    public static double easeInCubic(double t)    { return t * t * t; }
    public static double easeOutCubic(double t)   { return 1 - Math.pow(1 - t, 3); }
    public static double easeInOutCubic(double t) {
        return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
    }

    // --- Sine (smoothest) ---
    public static double easeInSine(double t)    { return 1 - Math.cos(t * Math.PI / 2); }
    public static double easeOutSine(double t)   { return Math.sin(t * Math.PI / 2); }
    public static double easeInOutSine(double t) { return -(Math.cos(Math.PI * t) - 1) / 2; }

    // --- Exponential (dramatic: very fast, then a long slow settle) ---
    public static double easeInExpo(double t)  { return t == 0 ? 0 : Math.pow(2, 10 * t - 10); }
    public static double easeOutExpo(double t) { return t == 1 ? 1 : 1 - Math.pow(2, -10 * t); }

    // --- Back (overshoots, or pulls back first) ---
    public static double easeInBack(double t) {
        double c1 = 1.70158, c3 = c1 + 1;
        return c3 * t * t * t - c1 * t * t;
    }
    public static double easeOutBack(double t) {
        double c1 = 1.70158, c3 = c1 + 1;
        return 1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2);
    }
    public static double easeInOutBack(double t) {
        double c2 = 1.70158 * 1.525;
        return t < 0.5
                ? (Math.pow(2 * t, 2) * ((c2 + 1) * 2 * t - c2)) / 2
                : (Math.pow(2 * t - 2, 2) * ((c2 + 1) * (2 * t - 2) + c2) + 2) / 2;
    }

    // --- Bounce (like a ball landing) ---
    public static double easeOutBounce(double t) {
        double n1 = 7.5625, d1 = 2.75;
        if (t < 1 / d1)        return n1 * t * t;
        else if (t < 2 / d1)   { t -= 1.5 / d1;   return n1 * t * t + 0.75; }
        else if (t < 2.5 / d1) { t -= 2.25 / d1;  return n1 * t * t + 0.9375; }
        else                   { t -= 2.625 / d1; return n1 * t * t + 0.984375; }
    }

    // --- Elastic (springs past the target and wobbles) ---
    public static double easeOutElastic(double t) {
        if (t == 0 || t == 1) return t;
        double c4 = (2 * Math.PI) / 3;
        return Math.pow(2, -10 * t) * Math.sin((10 * t - 0.75) * c4) + 1;
    }

    // Fast wobble that dies out. t goes 0 -> 1. Returns a pixel offset.
    public static double shake(double t, double frequency, double amplitude) {
        return Math.sin(t * frequency) * (1 - t) * amplitude;
    }

    // Smooth looping scale factor around 1.0. Use elapsed time in ms.
    public static double pulse(double elapsedMs, double periodMs, double strength) {
        return 1 + strength * Math.sin(2 * Math.PI * elapsedMs / periodMs);
    }

    // Blend between two colors. t goes 0 -> 1.
    public static Color lerpColor(Color a, Color b, double t) {
        t = Math.max(0, Math.min(1, t));
        int r  = (int) Math.round(a.getRed()   + (b.getRed()   - a.getRed())   * t);
        int gr = (int) Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) Math.round(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t);
        int al = (int) Math.round(a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t);
        return new Color(r, gr, bl, al);
    }
}
