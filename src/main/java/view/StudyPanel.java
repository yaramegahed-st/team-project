package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import view.style.*;
import view.components.*;
import interface_adapter.*;

public class StudyPanel extends JPanel {
    private static final double BASE_W = 651.0;
    private static final double BASE_H = 394.0;
    private final double s;
    private final int panelWidth;

    private final OutlinedLabel titleFlash;
    private final OutlinedLabel heading;
    private final Font headingFont;
    private final JLabel info = new JLabel("", SwingConstants.CENTER);
    private List<Integer> positions = new java.util.ArrayList<>();

    private Runnable onTitleClicked;
    public void setOnTitleClicked(Runnable r) { this.onTitleClicked = r; }
    public List<Integer> getPositions() { return positions; }

    private int sc(double v) { return (int) Math.round(v * s); }

    private void styleAndFit(OutlinedLabel label, double strokeW, Font f, int shadowX, int shadowY) {
        label.setFont(f);
        label.setStrokeWidth((float) (strokeW * s));
        label.setShadowColor(new Color(0, 20, 40, 150));
        label.setShadowOffset(sc(shadowX), sc(shadowY));
        FontMetrics fm = label.getFontMetrics(f);
        int pad = sc(8);
        label.setSize(fm.stringWidth(label.getText()) + 2 * pad, fm.getHeight() + 2 * pad);
    }

    public StudyPanel(int width, ViewManagerModel viewManagerModel) {
        this.panelWidth = width;
        s = width / BASE_W;
        int height = sc(BASE_H);

        setLayout(null);
        setPreferredSize(new Dimension(width, height));

        // --- Flash title (top-left, click to go back) ---
        titleFlash = new OutlinedLabel("FlashShare");
        styleAndFit(titleFlash, 0.9, FontLoader.loadDamion((float) (26f * s)), 2, 3);
        titleFlash.setLocation(0, 0);
        titleFlash.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        titleFlash.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (onTitleClicked != null) onTitleClicked.run();
            }
        });

        // --- Heading (text is set by startSession) ---
        headingFont = FontLoader.loadAdlam((float) (30f * s));
        heading = new OutlinedLabel(" ");
        styleAndFit(heading, 1.2, headingFont, 1, 2);

        // --- Info line ---
        info.setFont(FontLoader.loadAdlam((float) (18f * s)));
        info.setBounds(0, sc(120), width, sc(40));

        add(titleFlash);
        add(heading);
        add(info);

        Theme.addListener(this::applyTheme);
        applyTheme();
    }

    /** Call before sliding to this panel. */
    public void startSession(String deckName, String mode, List<Integer> positions) {
        this.positions = positions;

        heading.setText("Studying: " + deckName);
        FontMetrics fm = heading.getFontMetrics(headingFont);
        int pad = sc(8);
        heading.setSize(fm.stringWidth(heading.getText()) + 2 * pad, fm.getHeight() + 2 * pad);
        heading.setLocation((panelWidth - heading.getWidth()) / 2, sc(50));

        info.setText(mode + " · " + positions.size() + (positions.size() == 1 ? " card" : " cards"));
        // later: load the cards at these positions and show the first one
        repaint();
    }

    private void applyTheme() {
        setBackground(Theme.background());
        titleFlash.setForeground(Theme.flashFill());
        titleFlash.setStrokeColor(Theme.flashStroke());
        heading.setForeground(Theme.headingFill());
        heading.setStrokeColor(Theme.headingStroke());
        info.setForeground(Theme.textColor());
        repaint();
    }
}
