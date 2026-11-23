package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SpinningIconButton extends JButton {
    private static final double HOVER_ANGLE = Math.toRadians(90);   // how far it turns
    private static final double EASE = 0.15;                        // 0-1: higher = snappier

    private final Image icon;
    private double angle = 0;           // current rotation (radians)
    private double target = 0;          // where we're heading
    private final Timer timer;

    public SpinningIconButton(Image icon) {
        this.icon = icon;

        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        timer = new Timer(16, e -> {
            angle += (target - angle) * EASE;           // move a fraction of the remaining distance
            if (Math.abs(target - angle) < 0.001) {     // close enough: snap and stop
                angle = target;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { target = HOVER_ANGLE; timer.start(); }
            @Override public void mouseExited(MouseEvent e)  { target = 0;           timer.start(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int iw = icon.getWidth(null), ih = icon.getHeight(null);

        g2.rotate(angle, w / 2.0, h / 2.0);
        g2.drawImage(icon, (w - iw) / 2, (h - ih) / 2, null);
        g2.dispose();
    }
}
