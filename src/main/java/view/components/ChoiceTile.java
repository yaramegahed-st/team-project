package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import view.style.Theme;


public class ChoiceTile extends JComponent {
    private final String[] lines;
    private final Font font;
    private final float strokeWidth;
    private boolean hovering = false;
    private Runnable onClick;

    public ChoiceTile(String[] lines, Font font, float strokeWidth) {
        this.lines = lines;
        this.font = font;
        this.strokeWidth = strokeWidth;
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovering = true;  repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovering = false; repaint(); }
            @Override public void mouseClicked(MouseEvent e) { if (onClick != null) onClick.run(); }
        });
    }

    public void setOnClick(Runnable r) { this.onClick = r; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int arc = (int) (Math.min(w, h) * 0.28);

        Color base = Theme.pill();
        g2.setColor(hovering ? base.brighter() : base);
        g2.fillRoundRect(0, 0, w, h, arc, arc);

        // centered, outlined text lines (same look as PillButton)
        FontRenderContext frc = g2.getFontRenderContext();
        float lineH = font.getSize2D() * 1.2f;
        float top = (h - lineH * lines.length) / 2f;

        for (int i = 0; i < lines.length; i++) {
            TextLayout tl = new TextLayout(lines[i], font, frc);
            float x = (w - tl.getAdvance()) / 2f;
            float y = top + i * lineH + (lineH + tl.getAscent() - tl.getDescent()) / 2f;
            Shape outline = tl.getOutline(AffineTransform.getTranslateInstance(x, y));

            if (!Theme.isDark()) {
                g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(Color.BLACK);
                g2.draw(outline);
            }
            g2.setColor(Theme.pillText());
            g2.fill(outline);
        }
        g2.dispose();
    }
}
