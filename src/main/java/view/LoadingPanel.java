package view;

import view.components.OutlinedLabel;
import view.style.Easing;
import view.style.FontLoader;
import view.style.IconLoader;

import javax.swing.*;
import java.awt.*;

public class LoadingPanel extends JPanel {
    private static final int DURATION_MS = 1200;
    private static final double BASE_W = 651.0;   // the width the layout was designed for
    private static final double BASE_H = 394.0;

    private final double s;                       // scale factor

    private final OutlinedLabel title;
    private final JLabel imageLabel;

    private final Point titleStart, titleEnd, imageStart, imageEnd;

    private int sc(double v) {
        return (int) Math.round(v * s);
    }

    public LoadingPanel(int width) {              // pass the frame width
        s = width / BASE_W;
        int height = sc(BASE_H);

        setLayout(null);
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.decode("#004369"));

        // --- Title ---
        title = new OutlinedLabel("FlashShare");
        title.setFont(FontLoader.loadDamion((float) (80f * s)));
        title.setForeground(Color.decode("#B5FFFF"));
        title.setStrokeColor(Color.decode("#4E54F9"));
        title.setStrokeWidth((float) (2.5f * s));
        title.setShadowColor(new Color(0, 20, 40, 150));
        title.setShadowOffset(sc(4), sc(6));
        int titleW = sc(441), titleH = sc(139);
        title.setSize(titleW, titleH);

        int titleX = (width - titleW) / 2;                          // horizontally centered
        titleStart = new Point(titleX, sc(58));
        titleEnd   = new Point(titleX, sc(116));

        // --- Image ---
        ImageIcon icon = IconLoader.load("streak.jpeg");
        int targetWidth = sc(345);
        int targetHeight = icon.getIconHeight() * targetWidth / icon.getIconWidth();   // keeps aspect ratio
        Image scaled = icon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        imageLabel = new JLabel(new ImageIcon(scaled));
        imageLabel.setSize(targetWidth, targetHeight);

        int imageX = (width - targetWidth) / 2;                     // horizontally centered
        imageStart = new Point(imageX, sc(300));                    // just below the visible area
        imageEnd   = new Point(imageX, sc(208));

        title.setLocation(titleStart);
        imageLabel.setLocation(imageStart);

        add(title);
        add(imageLabel);
    }

    public void startAnimation() {
        long startTime = System.nanoTime();

        javax.swing.Timer timer = new javax.swing.Timer(10, null);
        timer.addActionListener(e -> {
            double elapsedMs = (System.nanoTime() - startTime) / 1_000_000.0;
            double t = Math.min(1.0, elapsedMs / DURATION_MS);
            double eased = Easing.easeOutBounce(t);

            title.setLocation(lerp(titleStart, titleEnd, eased));
            imageLabel.setLocation(lerp(imageStart, imageEnd, eased));

            if (t >= 1.0) {
                timer.stop();
            }
        });
        timer.start();
    }

    // linear interpolation between two points
    private static Point lerp(Point a, Point b, double t) {
        return new Point(
                (int) Math.round(a.x + (b.x - a.x) * t),
                (int) Math.round(a.y + (b.y - a.y) * t)
        );
    }
}