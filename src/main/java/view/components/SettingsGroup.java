package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import view.style.Theme;

public class SettingsGroup extends JComponent {
    private final String[] labels = new String[2];
    private final Runnable[] actions = new Runnable[2];
    private final Font font;
    private int hoverRow = -1;

    public SettingsGroup(String top, String bottom, Font font) {
        labels[0] = top;
        labels[1] = bottom;
        this.font = font;

        MouseAdapter ma = new MouseAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int row = rowAt(e.getY());
                if (row != hoverRow) {
                    hoverRow = row;
                    setCursor(Cursor.getPredefinedCursor(actions[row] != null ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
                    repaint();
                }
            }
            @Override public void mouseExited(MouseEvent e) { hoverRow = -1; repaint(); }
            @Override public void mouseClicked(MouseEvent e) {
                Runnable r = actions[rowAt(e.getY())];
                if (r != null) r.run();
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    private int rowAt(int y) { return y < getHeight() / 2 ? 0 : 1; }

    public void setLabel(int row, String text) { labels[row] = text; repaint(); }
    public void setAction(int row, Runnable r) { actions[row] = r; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        Shape pill = new RoundRectangle2D.Float(0, 0, w, h, h, h);
        Color text = Theme.pillText();

        g2.setColor(Theme.pill());
        g2.fill(pill);

        // subtle highlight on the hovered row (only rows that do something)
        if (hoverRow >= 0 && actions[hoverRow] != null) {
            g2.setClip(pill);
            g2.setColor(new Color(text.getRed(), text.getGreen(), text.getBlue(), 28));
            g2.fillRect(0, hoverRow * (h / 2), w, h / 2);
            g2.setClip(null);
        }

        // divider
        int inset = (int) (h * 0.30);
        g2.setColor(new Color(text.getRed(), text.getGreen(), text.getBlue(), 130));
        g2.drawLine(inset, h / 2, w - inset, h / 2);

        // row labels
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(text);
        for (int i = 0; i < 2; i++) {
            int cy = (int) (h * (0.25 + 0.5 * i));
            g2.drawString(labels[i], inset, cy + (fm.getAscent() - fm.getDescent()) / 2);
        }
        g2.dispose();
    }
}
