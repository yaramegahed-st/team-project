package view;

import javax.swing.*;
import java.awt.*;
import view.style.*;
import view.components.*;

public class LoadingPanel extends JPanel {
    private static final int DURATION_MS = 1200;
    private static final double BASE_W = 651.0;   // the width your layout was designed for
    private static final double BASE_H = 394.0;

    private final double s;                       // scale factor

    private final OutlinedLabel title;
    private final JLabel imageLabel;

    private final Point titleStart, titleEnd, imageStart, imageEnd;

    // helper: scale a design-time value to the current size
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
        title.setFont(FontLoader.loadDamion((float) (80f * s)));   // font scales too
        title.setForeground(Color.decode("#B5FFFF"));
        title.setStrokeColor(Color.decode("#4E54F9"));
        title.setStrokeWidth((float) (2.5f * s));                  // outline scales
        title.setShadowColor(new Color(0, 20, 40, 150));
        title.setShadowOffset(sc(4), sc(6));                       // shadow scales
        int titleW = sc(441), titleH = sc(139);
        title.setSize(titleW, titleH);

        int titleX = (width - titleW) / 2;                         // horizontally centered
        titleStart = new Point(titleX, sc(58));
        titleEnd   = new Point(titleX, sc(116));

        // --- Image ---
        ImageIcon icon = new ImageIcon("src/main/resources/icons/streak.jpeg");
        int targetWidth = sc(345);
        int targetHeight = icon.getIconHeight() * targetWidth / icon.getIconWidth();
        Image scaled = icon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        imageLabel = new JLabel(new ImageIcon(scaled));
        imageLabel.setSize(targetWidth, targetHeight);

        int imageX = (width - targetWidth) / 2;                    // horizontally centered
        imageStart = new Point(imageX, sc(300));
        imageEnd   = new Point(imageX, sc(208));

        title.setLocation(titleStart);
        imageLabel.setLocation(imageStart);

        add(title);
        add(imageLabel);
    }

    public void startAnimation() {
        long startTime = System.nanoTime();

        //creates timer
        Timer timer = new Timer(10, null);
        //starts action: this uses a lambda expression (e ->) which is a shortcut to place your action
        // implementation inside the action listener call
        timer.addActionListener(e -> {
            double elapsedMs = (System.nanoTime() - startTime) / 1_000_000.0;
            double t = Math.min(1.0, elapsedMs / DURATION_MS);

            //here is where you can adjust the animation to your liking
            double eased = Easing.easeOutBounce(t);    // ease-out cubic: fast start, soft landing

            title.setLocation(lerp(titleStart, titleEnd, eased));
            imageLabel.setLocation(lerp(imageStart, imageEnd, eased));

            if (t >= 1.0) {
                timer.stop();
            }
        });
        timer.start();
    }

    //function that creates a point that is some distance between 2 points (linear interpolation)
    private static Point lerp(Point a, Point b, double t) {
        return new Point(
                (int) Math.round(a.x + (b.x - a.x) * t),
                (int) Math.round(a.y + (b.y - a.y) * t)
        );
    }
}
