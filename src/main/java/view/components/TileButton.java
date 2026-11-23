package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import view.style.Theme;

public class TileButton extends JComponent {
    private String text;
    private String access = "Private";

    public String getText()            { return text; }
    public void setText(String t)      { this.text = t; repaint(); }
    public String getAccess()          { return access; }
    public void setAccess(String a)    { this.access = a; }

    private final Font font;
    private final float strokeWidth;
    private boolean hovering = false;

    private Runnable onOpen;    // clicking the tile
    private Runnable onMenu;    // clicking the three dots

    private String kind;                                   // e.g. "Folder" or "Deck"; null = no label

    public void setKind(String k) { this.kind = k; repaint(); }
    public String getKind()       { return kind; }

    public TileButton(String text, Font font, float strokeWidth) {
        this.text = text;
        this.font = font;
        this.strokeWidth = strokeWidth;
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovering = true;  repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovering = false; repaint(); }
            @Override public void mouseClicked(MouseEvent e) {
                boolean inDots = e.getX() < getWidth() * 0.28 && e.getY() < getHeight() * 0.28;
                Runnable r = inDots ? onMenu : onOpen;
                if (r != null) r.run();
            }
        });
    }

    public void setOnOpen(Runnable r) { this.onOpen = r; }
    public void setOnMenu(Runnable r) { this.onMenu = r; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // rounded tile
        Color base = Theme.tileFill();
        g2.setColor(hovering ? base.brighter() : base);
        g2.fillRoundRect(0, 0, w, h, (int) (h * 0.45), (int) (h * 0.45));

        // three-dot menu, top-left
        int d = Math.max(2, (int) Math.round(h * 0.045));
        int dx = (int) (w * 0.14) - d / 2;
        int dy = (int) (h * 0.12);
        g2.setColor(Color.WHITE);
        for (int i = 0; i < 3; i++) {
            g2.fillOval(dx, dy + i * (int) (d * 2.2), d, d);
        }

        // outlined label, centered (a little below the middle)
        TextLayout tl = new TextLayout(text, font, g2.getFontRenderContext());
        float tx = (w - tl.getAdvance()) / 2f;
        float ty = h * 0.55f + (tl.getAscent() - tl.getDescent()) / 2f;
        Shape outline = tl.getOutline(AffineTransform.getTranslateInstance(tx, ty));

        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(Color.BLACK);
        g2.draw(outline);
        g2.setColor(Color.decode("#B5FFFF"));
        g2.fill(outline);

        if (kind != null) {
            Font small = font.deriveFont(font.getSize2D() * 0.45f);
            g2.setFont(small);
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(new Color(214, 238, 245, 200));        // soft light tone, slightly see-through
            g2.drawString(kind, (w - fm.stringWidth(kind)) / 2, (int) (h * 0.86));
        }

        g2.dispose();
    }
}
