package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

public class OutlinedLabel extends JLabel {
    private Color strokeColor = Color.BLACK;
    private float strokeWidth = 2.0f;
    private Color shadowColor = new Color(0, 0, 0, 120);
    private int shadowOffsetX = 4;
    private int shadowOffsetY = 4;

    //constructor
    public OutlinedLabel(String text) {
        super(text);
        setOpaque(false);
    }

    //creates a stroke color
    public void setStrokeColor(Color color) {
        this.strokeColor = color;
        repaint();
    }

    //creates a stroke thickness
    public void setStrokeWidth(float width) {
        this.strokeWidth = width;
        repaint();
    }

    //creates an under-shadow
    public void setShadowColor(Color color) {
        this.shadowColor = color;
        repaint();
    }

    //creates a thickness for the undershadow
    public void setShadowOffset(int x, int y) {
        this.shadowOffsetX = x;
        this.shadowOffsetY = y;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        Font font = getFont();
        FontMetrics fm = g2.getFontMetrics(font);

        TextLayout layout = new TextLayout(getText(), font,
                g2.getFontRenderContext());

        // Center the text (use x = 0 instead if you want it left-aligned)
        float x = (getWidth() - (float) layout.getAdvance()) / 2f;
        float y = (getHeight() - fm.getHeight()) / 2f + fm.getAscent();

        Shape outline = layout.getOutline(
                AffineTransform.getTranslateInstance(x, y)
        );

        BasicStroke stroke = new BasicStroke(
                strokeWidth,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        );

        // 1. Shadow (drawn first, offset down/right)
        g2.translate(shadowOffsetX, shadowOffsetY);
        g2.setColor(shadowColor);
        g2.setStroke(stroke);
        g2.fill(outline);
        g2.draw(outline);
        g2.translate(-shadowOffsetX, -shadowOffsetY);

        // 2. Stroke (outline)
        g2.setColor(strokeColor);
        g2.setStroke(stroke);
        g2.draw(outline);

        // 3. Fill with the normal font color
        g2.setColor(getForeground());
        g2.fill(outline);

        g2.dispose();
    }
}
