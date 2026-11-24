package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.home.HomeViewModel;
import interface_adapter.settings.SettingsViewModel;
import view.components.LabelHoverZoom;
import view.components.OutlinedLabel;
import view.components.SpinningIconButton;
import view.style.FontLoader;
import view.style.IconLoader;
import view.style.Theme;

import javax.swing.*;
import java.awt.*;

public class AboutPanel extends JPanel {
    private static final double BASE_W = 651.0;
    private static final double BASE_H = 394.0;
    private final double s;

    private final JPanel box;
    private final OutlinedLabel title;
    private final OutlinedLabel titleFlash;
    private final SpinningIconButton settingsButton;
    private final java.util.List<OutlinedLabel> bodyLabels = new java.util.ArrayList<>();

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

    public AboutPanel(int width, ViewManagerModel viewManagerModel) {
        s = width / BASE_W;
        int height = sc(BASE_H);

        setLayout(null);
        setPreferredSize(new Dimension(width, height));

        int margin = sc(10);
        int settingsSlot = sc(40);
        int lift = sc(8);

        // --- Flash title (top-left) ---
        titleFlash = new OutlinedLabel("FlashShare");
        Font f1 = FontLoader.loadDamion((float) (26f * s));
        styleAndFit(titleFlash, 0.9, f1, 2, 3);
        titleFlash.setLocation(0, 0);
        LabelHoverZoom.attach(titleFlash, f1, (float) (0.9 * s), sc(2), sc(3), sc(8), 1.08,
                () -> viewManagerModel.navigate(HomeViewModel.VIEW_NAME, false));

        // --- Title (centered at the top) ---
        title = new OutlinedLabel("About");
        styleAndFit(title, 1.2, FontLoader.loadAdlam((float) (44f * s)), 0, 0);
        title.setLocation((width - title.getWidth()) / 2, sc(4));
        add(title);

        // --- Container (centered, below the title) ---
        int boxW = width - 2 * sc(35);
        int boxH = sc(165);
        int boxY = title.getY() + title.getHeight() + sc(8);

        box = new JPanel(null);
        box.setBounds((width - boxW) / 2, boxY, boxW, boxH);
        add(box);

        // --- Text inside the container ---
        Font bodyFont = FontLoader.loadAdlam((float) (15f * s));
        int maxTextW = boxW - 2 * sc(25);
        int y = sc(14);
        y = addWrapped(box, "This application was designed for a school project......", bodyFont, y, maxTextW);
        y += sc(30);                                                    // gap between paragraphs
        addWrapped(box, "Here we\u2019ll explain the mechanics of how everything will work on the app for new users",
                bodyFont, y, maxTextW);

        // --- Settings button (top-right corner) ---
        int iconSize = settingsSlot - sc(6);
        ImageIcon rawIcon = IconLoader.load("settings2.jpeg");
        Image scaledIcon = rawIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        settingsButton = new SpinningIconButton(scaledIcon);
        settingsButton.setSize(settingsSlot, settingsSlot);
        settingsButton.setToolTipText("Settings");
        settingsButton.setLocation(width - margin - settingsSlot,
                margin - lift + (titleFlash.getHeight() - settingsSlot) / 2);
        settingsButton.addActionListener(e ->
                viewManagerModel.navigate(SettingsViewModel.VIEW_NAME, true));

        add(titleFlash);
        add(settingsButton);

        Theme.addListener(this::applyTheme);
        applyTheme();
    }

    // Wraps text into centered lines inside parent; returns the y position below the last line
    private int addWrapped(JPanel parent, String text, Font font, int y, int maxW) {
        FontMetrics fm = getFontMetrics(font);
        StringBuilder line = new StringBuilder();

        for (String word : text.split(" ")) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(test) > maxW && line.length() > 0) {
                y = addLine(parent, line.toString(), font, y, fm);
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(test);
            }
        }
        if (line.length() > 0) y = addLine(parent, line.toString(), font, y, fm);
        return y;
    }

    private int addLine(JPanel parent, String text, Font font, int y, FontMetrics fm) {
        OutlinedLabel l = new OutlinedLabel(text);
        styleAndFit(l, 0.8, font, 0, 0);
        int pad = sc(8);
        l.setLocation((parent.getWidth() - l.getWidth()) / 2, y - pad);  // pad offset keeps the text itself at y
        parent.add(l);
        bodyLabels.add(l);
        return y + fm.getHeight() + sc(4);                               // line spacing
    }

    private void applyTheme() {
        setBackground(Theme.background());
        titleFlash.setForeground(Theme.flashFill());
        titleFlash.setStrokeColor(Theme.flashStroke());
        title.setForeground(Theme.headingFill());
        title.setStrokeColor(Theme.headingStroke());
        box.setBackground(Theme.containerFill());
        box.setBorder(BorderFactory.createLineBorder(Theme.containerBorder(), 1));
        for (OutlinedLabel l : bodyLabels) {
            l.setForeground(Theme.bodyFill());
            l.setStrokeColor(Theme.bodyStroke());
        }
        repaint();
    }
}
