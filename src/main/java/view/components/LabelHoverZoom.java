package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LabelHoverZoom {
    private static final double EASE = 0.2;

    /** Call after the label is styled, sized and positioned. Base values must match what you gave the label. */
    public static void attach(OutlinedLabel label, Font baseFont, float baseStroke,
                              int baseShadowX, int baseShadowY, int basePad,
                              double hoverScale, Runnable onClick) {

        // remember the resting center so the label grows outward from it
        final double cx = label.getX() + label.getWidth() / 2.0;
        final double cy = label.getY() + label.getHeight() / 2.0;

        final double[] scale = {1.0};
        final double[] target = {1.0};

        Timer timer = new Timer(16, null);
        timer.addActionListener(e -> {
            scale[0] += (target[0] - scale[0]) * EASE;
            if (Math.abs(target[0] - scale[0]) < 0.001) {
                scale[0] = target[0];
                timer.stop();
            }

            double k = scale[0];
            Font f = baseFont.deriveFont((float) (baseFont.getSize2D() * k));
            label.setFont(f);
            label.setStrokeWidth((float) (baseStroke * k));
            label.setShadowOffset((int) Math.round(baseShadowX * k), (int) Math.round(baseShadowY * k));

            FontMetrics fm = label.getFontMetrics(f);
            int pad = (int) Math.round(basePad * k);
            int w = fm.stringWidth(label.getText()) + 2 * pad;
            int h = fm.getHeight() + 2 * pad;
            label.setBounds((int) Math.round(cx - w / 2.0), (int) Math.round(cy - h / 2.0), w, h);
            label.repaint();
        });

        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { target[0] = hoverScale; timer.start(); }
            @Override public void mouseExited(MouseEvent e)  { target[0] = 1.0;        timer.start(); }
            @Override public void mouseClicked(MouseEvent e) { if (onClick != null) onClick.run(); }
        });
    }
}
