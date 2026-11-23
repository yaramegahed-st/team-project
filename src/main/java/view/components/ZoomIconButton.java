package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ZoomIconButton extends JButton {
    private static final double EASE = 0.2;          // 0-1: higher = snappier

    private final Image icon;
    private final int baseW, baseH;                  // normal (unhovered) size of the image
    private final double hoverScale;                 // e.g. 1.12 = 12% larger on hover
    private double scale = 1.0;
    private double target = 1.0;
    private final Timer timer;

    public ZoomIconButton(Image icon, int baseW, int baseH, double hoverScale) {
        this.icon = icon;
        this.baseW = baseW;
        this.baseH = baseH;
        this.hoverScale = hoverScale;

        // the button is sized for the biggest state so the enlarged image is never clipped
        setSize((int) Math.ceil(baseW * hoverScale) + 2,
                (int) Math.ceil(baseH * hoverScale) + 2);

        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        timer = new Timer(16, e -> {
            scale += (target - scale) * EASE;
            if (Math.abs(target - scale) < 0.001) {
                scale = target;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { target = hoverScale; timer.start(); }
            @Override public void mouseExited(MouseEvent e)  { target = 1.0;        timer.start(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = (int) Math.round(baseW * scale);
        int h = (int) Math.round(baseH * scale);
        g2.drawImage(icon, (getWidth() - w) / 2, (getHeight() - h) / 2, w, h, null);   // grows around the center
        g2.dispose();
    }
}
