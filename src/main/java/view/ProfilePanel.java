package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.settings.SettingsViewModel;
import view.components.LabelHoverZoom;
import view.components.OutlinedLabel;
import view.components.SettingsGroup;
import view.style.FontLoader;
import view.style.IconLoader;
import view.style.Theme;

import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends JPanel {
    private static final double BASE_W = 651.0;
    private static final double BASE_H = 394.0;
    private final double s;

    private final OutlinedLabel titleFlash;
    private final OutlinedLabel heading;
    private final SettingsGroup[] groups = new SettingsGroup[2];

    /** Hook for later: e.g. setRowAction(1, 0, ...) for CHANGE USERNAME. */
    public void setRowAction(int group, int row, Runnable r) { groups[group].setAction(row, r); }

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

    public ProfilePanel(int width, ViewManagerModel viewManagerModel) {
        s = width / BASE_W;
        int height = sc(BASE_H);

        setLayout(null);
        setPreferredSize(new Dimension(width, height));

        // --- Flash title (top-left, back to settings) ---
        titleFlash = new OutlinedLabel("FlashShare");
        Font f1 = FontLoader.loadDamion((float) (26f * s));
        styleAndFit(titleFlash, 0.9, f1, 2, 3);
        titleFlash.setLocation(0, 0);
        LabelHoverZoom.attach(titleFlash, f1, (float) (0.9 * s), sc(2), sc(3), sc(8), 1.08,
                () -> viewManagerModel.navigate(SettingsViewModel.VIEW_NAME, false));

        // --- Heading (centered) ---
        heading = new OutlinedLabel("PROFILE");
        styleAndFit(heading, 1.2, FontLoader.loadAdlam((float) (36f * s)), 1, 2);
        heading.setLocation((width - heading.getWidth()) / 2, sc(4));

        // --- Pill groups ---
        String[][] rows = {
                {"USERNAME", "PASSWORD"},
                {"CHANGE USERNAME", "CHANGE PASSWORD"}
        };
        int groupW = (int) Math.round(width * 0.50);
        int groupH = (int) Math.round(height * 0.135);
        int gap    = (int) Math.round(height * 0.035);
        int x  = (width - groupW) / 2;
        int y0 = (int) Math.round(height * 0.25);
        Font rowFont = FontLoader.loadAdlam((float) (groupH * 0.26)).deriveFont(Font.BOLD);

        for (int i = 0; i < rows.length; i++) {
            groups[i] = new SettingsGroup(rows[i][0], rows[i][1], rowFont);
            groups[i].setBounds(x, y0 + i * (groupH + gap), groupW, groupH);
            add(groups[i]);
        }

        // --- Cards image (bottom-right) ---
        ImageIcon cardsRaw = IconLoader.load("cards2.jpeg");
        int cardsW = (int) Math.round(width * 0.14);
        int cardsH = cardsRaw.getIconHeight() * cardsW / cardsRaw.getIconWidth();
        Image cardsScaled = cardsRaw.getImage().getScaledInstance(cardsW, cardsH, Image.SCALE_SMOOTH);
        JLabel cardsLabel = new JLabel(new ImageIcon(cardsScaled));
        cardsLabel.setSize(cardsW, cardsH);
        cardsLabel.setLocation(width - sc(30) - cardsW, height - sc(30) - cardsH);

        add(titleFlash);
        add(heading);
        add(cardsLabel);

        Theme.addListener(this::applyTheme);
        applyTheme();
    }

    private void applyTheme() {
        setBackground(Theme.background());
        titleFlash.setForeground(Theme.flashFill());
        titleFlash.setStrokeColor(Theme.flashStroke());
        heading.setForeground(Theme.headingFill());
        heading.setStrokeColor(Theme.headingStroke());
        repaint();
    }
}
