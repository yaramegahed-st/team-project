package view;

import javax.swing.*;
import java.awt.*;
import view.style.*;
import view.components.*;
import interface_adapter.*;

public class SettingsPanel extends JPanel {
    private static final double BASE_W = 651.0;
    private static final double BASE_H = 394.0;
    private final double s;

    private final OutlinedLabel titleFlash;
    private final OutlinedLabel heading;
    private final SettingsGroup[] groups = new SettingsGroup[4];

    private Runnable onTitleClicked;
    public void setOnTitleClicked(Runnable r) { this.onTitleClicked = r; }

    /** Hook for later: e.g. setRowAction(1, 0, () -> ...) for SIGN OUT's group, top row. */
    public void setRowAction(int group, int row, Runnable r) { groups[group].setAction(row, r); }

    private int sc(double v) { return (int) Math.round(v * s); }

    // Sizes a label to fit its text; colours come from Theme (see applyTheme)
    private void styleAndFit(OutlinedLabel label, double strokeW, Font f, int shadowX, int shadowY) {
        label.setFont(f);
        label.setStrokeWidth((float) (strokeW * s));
        label.setShadowColor(new Color(0, 20, 40, 150));
        label.setShadowOffset(sc(shadowX), sc(shadowY));
        FontMetrics fm = label.getFontMetrics(f);
        int pad = sc(8);
        label.setSize(fm.stringWidth(label.getText()) + 2 * pad, fm.getHeight() + 2 * pad);
    }

    public SettingsPanel(int width, ViewManagerModel viewManagerModel) {
        s = width / BASE_W;
        int height = sc(BASE_H);

        setLayout(null);
        setPreferredSize(new Dimension(width, height));

        // --- Flash title (top-left) ---
        titleFlash = new OutlinedLabel("FlashShare");
        styleAndFit(titleFlash, 0.9, FontLoader.loadDamion((float) (26f * s)), 2, 3);
        titleFlash.setLocation(0, 0);
        titleFlash.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        titleFlash.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (onTitleClicked != null) onTitleClicked.run();
            }
        });

        // --- Heading (centered) ---
        heading = new OutlinedLabel("Settings");
        styleAndFit(heading, 1.2, FontLoader.loadAdlam((float) (36f * s)), 1, 2);
        heading.setLocation((width - heading.getWidth()) / 2, sc(4));

        // --- Four pill groups ---
        String[][] rows = {
                {"PROFILE", "DARK MODE"},
                {"ADD ACCOUNT", "SIGN OUT"},
                {"MANAGE PERMISSIONS", "MANAGE DECKS"},
                {"WRITE A REVIEW", "SUPPORT"}
        };
        int groupW = (int) Math.round(width * 0.50);
        int groupH = (int) Math.round(height * 0.135);
        int gap    = (int) Math.round(height * 0.035);
        int x  = (width - groupW) / 2;
        int y0 = (int) Math.round(height * 0.25);
        Font rowFont = FontLoader.loadAdlam((float) (groupH * 0.26)).deriveFont(Font.BOLD);

        for (int i = 0; i < 4; i++) {
            groups[i] = new SettingsGroup(rows[i][0], rows[i][1], rowFont);
            groups[i].setBounds(x, y0 + i * (groupH + gap), groupW, groupH);
            add(groups[i]);
        }

        // only the dark mode row does anything for now
        groups[0].setAction(1, () -> Theme.setDark(!Theme.isDark()));

        // --- Cards image (bottom-right) ---
        ImageIcon cardsRaw = new ImageIcon("src/main/resources/icons/cards2.jpeg");
        int cardsW = (int) Math.round(width * 0.14);
        int cardsH = cardsRaw.getIconHeight() * cardsW / cardsRaw.getIconWidth();
        Image cardsScaled = cardsRaw.getImage().getScaledInstance(cardsW, cardsH, Image.SCALE_SMOOTH);
        JLabel cardsLabel = new JLabel(new ImageIcon(cardsScaled));
        cardsLabel.setSize(cardsW, cardsH);
        cardsLabel.setLocation(width - sc(30) - cardsW, height - sc(30) - cardsH);

        add(titleFlash);
        add(heading);
        add(cardsLabel);

        Theme.addListener(this::applyTheme);   // restyle whenever the theme changes
        applyTheme();                          // and once now
    }

    private void applyTheme() {
        setBackground(Theme.background());
        titleFlash.setForeground(Theme.flashFill());
        titleFlash.setStrokeColor(Theme.flashStroke());
        heading.setForeground(Theme.headingFill());
        heading.setStrokeColor(Theme.headingStroke());
        groups[0].setLabel(1, Theme.isDark() ? "LIGHT MODE" : "DARK MODE");   // the row names what it will switch to
        repaint();
    }
}