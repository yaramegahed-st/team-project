package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
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

    private static final double MAX_TEXT_FRACTION = 0.84;   // widest the title may be, as a fraction of the tile width
    private static final float MIN_SCALE = 0.35f;           // the title never shrinks below this fraction of its normal size

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

        // outlined label, centered (a little below the middle).
        // A title that is too wide for the tile gets a smaller font; if it would still not fit at the
        // smallest allowed size, it is cut off with "..." and the full name is shown as a tooltip.
        FontRenderContext frc = g2.getFontRenderContext();
        float maxWidth = (float) (w * MAX_TEXT_FRACTION);
        Font titleFont = font;
        float titleStroke = strokeWidth;
        String shown = text;

        TextLayout tl = new TextLayout(shown, titleFont, frc);
        if (tl.getAdvance() > maxWidth) {
            float scale = Math.max(MIN_SCALE, maxWidth / tl.getAdvance());
            titleFont = font.deriveFont(font.getSize2D() * scale);
            titleStroke = strokeWidth * scale;                    // keep the outline in proportion to the text
            shown = fitWithEllipsis(text, titleFont, frc, maxWidth);
            tl = new TextLayout(shown, titleFont, frc);
        }
        setToolTipText(shown.equals(text) ? null : text);

        float tx = (w - tl.getAdvance()) / 2f;
        float ty = h * 0.55f + (tl.getAscent() - tl.getDescent()) / 2f;
        Shape outline = tl.getOutline(AffineTransform.getTranslateInstance(tx, ty));

        g2.setStroke(new BasicStroke(titleStroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
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

    /** Returns s if it fits in maxWidth, otherwise the longest start of s that fits once "..." is added. */
    private static String fitWithEllipsis(String s, Font f, FontRenderContext frc, float maxWidth) {
        if (new TextLayout(s, f, frc).getAdvance() <= maxWidth) return s;
        String cut = s;
        while (cut.length() > 1) {
            cut = cut.substring(0, cut.length() - 1);
            String candidate = cut.stripTrailing() + "...";
            if (new TextLayout(candidate, f, frc).getAdvance() <= maxWidth) return candidate;
        }
        return "...";
    }
}
