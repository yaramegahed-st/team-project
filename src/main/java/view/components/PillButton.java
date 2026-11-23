package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import view.style.Theme;

public class PillButton extends JButton {
    private final Font font;
    private final float strokeWidth;
    private boolean hovering = false;

    // 'fill' is kept so existing calls still compile, but the colour now comes from Theme
    public PillButton(String text, Font font, Color fill, float strokeWidth) {
        super(text);
        this.font = font;
        this.strokeWidth = strokeWidth;

        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovering = true;  repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovering = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // pill background, read from the theme every time it paints
        Color base = Theme.pill();
        g2.setColor(hovering ? base.brighter() : base);
        g2.fillRoundRect(0, 0, w, h, h, h);                 // corner diameter = height gives fully rounded ends

        // centered text as a shape, so it can have an outline
        FontRenderContext frc = g2.getFontRenderContext();
        TextLayout tl = new TextLayout(getText(), font, frc);
        float x = (w - tl.getAdvance()) / 2f;
        float y = (h - (tl.getAscent() + tl.getDescent())) / 2f + tl.getAscent();
        Shape outline = tl.getOutline(AffineTransform.getTranslateInstance(x, y));

        // dark outline only in light mode (it would look heavy on a light pill)
        if (!Theme.isDark()) {
            g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(Color.BLACK);
            g2.draw(outline);
        }

        g2.setColor(Theme.pillText());
        g2.fill(outline);

        g2.dispose();
    }
}