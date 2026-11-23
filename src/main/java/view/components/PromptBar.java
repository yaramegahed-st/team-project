package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.function.Consumer;
import view.style.Theme;

public class PromptBar extends JPanel {
    private static Color fill() { return Theme.promptFill(); }
    private static Color border() { return Theme.promptBorder(); }

    private final JTextArea area;
    private final Font font;
    private Consumer<String> onSubmit;
    private Runnable onAttach;

    public PromptBar(int w, int h, Font font, String placeholder) {
        this.font = font;
        setLayout(null);
        setOpaque(false);
        setSize(w, h);

        // --- text area with an outlined placeholder ---
        area = new JTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Insets in = getInsets();
                    TextLayout tl = new TextLayout(placeholder, getFont(), g2.getFontRenderContext());
                    Shape outline = tl.getOutline(AffineTransform.getTranslateInstance(
                            in.left, in.top + g2.getFontMetrics(getFont()).getAscent()));
                    g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(Color.BLACK);
                    g2.draw(outline);
                    g2.setColor(Color.WHITE);
                    g2.fill(outline);
                    g2.dispose();
                }
            }
        };
        area.setFont(font);
        area.setOpaque(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(null);
        area.setForeground(Color.decode("#1F3A4D"));
        area.setCaretColor(Color.decode("#1F3A4D"));

        // Enter submits, Shift+Enter makes a new line
        area.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "submit");
        area.getInputMap().put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break");
        area.getActionMap().put("submit", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { submit(); }
        });

        JScrollPane scroll = new JScrollPane(area,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        int pad = (int) (h * 0.30);
        scroll.setBounds(pad, (int) (h * 0.14), w - 2 * pad, (int) (h * 0.46));
        add(scroll);

        // --- "+" button, bottom-left ---
        int dPlus = (int) (h * 0.19);
        IconButton plus = new IconButton(false, dPlus, () -> { if (onAttach != null) onAttach.run(); });
        plus.setBounds((int) (h * 0.46) - dPlus / 2, (int) (h * 0.85) - dPlus / 2, dPlus, dPlus);
        add(plus);

        // --- submit button, bottom-right ---
        int dSend = (int) (h * 0.25);
        IconButton send = new IconButton(true, dSend, this::submit);
        send.setBounds(w - (int) (h * 0.53) - dSend / 2, (int) (h * 0.77) - dSend / 2, dSend, dSend);
        add(send);
    }

    public void setOnSubmit(Consumer<String> c) { this.onSubmit = c; }
    public void setOnAttach(Runnable r)         { this.onAttach = r; }
    public String getText()                     { return area.getText(); }
    public void clear()                         { area.setText(""); }
    public void focusInput()                    { area.requestFocusInWindow(); }

    private void submit() {
        String text = area.getText().trim();
        if (!text.isEmpty() && onSubmit != null) onSubmit.accept(text);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g2.setColor(fill());
        g2.fillRoundRect(0, 0, w - 1, h - 1, h, h);          // fully rounded ends
        g2.setColor(border());
        g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);
        g2.dispose();
    }

    /** Small round button: a "+" (white disc) or a check (white ring). */
    private static class IconButton extends JComponent {
        private final boolean check;
        private boolean hover = false;

        IconButton(boolean check, int size, Runnable onClick) {
            this.check = check;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) { if (onClick != null) onClick.run(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int d = Math.min(getWidth(), getHeight());
            Color white = hover ? new Color(255, 255, 255) : new Color(255, 255, 255, 235);

            if (check) {
                float sw = Math.max(1.5f, d * 0.07f);
                g2.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(white);
                g2.drawOval((int) sw, (int) sw, d - 2 * (int) sw, d - 2 * (int) sw);
                Path2D p = new Path2D.Float();
                p.moveTo(d * 0.28, d * 0.52);
                p.lineTo(d * 0.44, d * 0.68);
                p.lineTo(d * 0.74, d * 0.34);
                g2.draw(p);
            } else {
                g2.setColor(white);
                g2.fillOval(0, 0, d, d);
                g2.setColor(fill());
                g2.setStroke(new BasicStroke(Math.max(1.5f, d * 0.12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                float c = d / 2f, r = d * 0.24f;
                g2.draw(new java.awt.geom.Line2D.Float(c - r, c, c + r, c));
                g2.draw(new java.awt.geom.Line2D.Float(c, c - r, c, c + r));
            }
            g2.dispose();
        }
    }
}
