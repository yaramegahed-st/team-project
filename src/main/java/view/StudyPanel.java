package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.home.HomeViewModel;
import view.components.LabelHoverZoom;
import view.components.OutlinedLabel;
import view.style.FontLoader;
import view.style.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Shows the chosen study session. A shell for now: it displays the selection, not the cards. */
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

        // --- Flash title (top-left, click to go home) ---
        titleFlash = new OutlinedLabel("FlashShare");
        Font f1 = FontLoader.loadDamion((float) (26f * s));
        styleAndFit(titleFlash, 0.9, f1, 2, 3);
        titleFlash.setLocation(0, 0);
        LabelHoverZoom.attach(titleFlash, f1, (float) (0.9 * s), sc(2), sc(3), sc(8), 1.08,
                () -> viewManagerModel.navigate(HomeViewModel.VIEW_NAME, false));

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

    /** Call before navigating to this view. */
    public void startSession(String deckName, String mode, List<Integer> positions) {
        this.positions = positions;

        heading.setText("Studying: " + deckName);
        FontMetrics fm = heading.getFontMetrics(headingFont);
        int pad = sc(8);
        heading.setSize(fm.stringWidth(heading.getText()) + 2 * pad, fm.getHeight() + 2 * pad);
        heading.setLocation((panelWidth - heading.getWidth()) / 2, sc(50));

        info.setText(mode + " \u00b7 " + positions.size() + (positions.size() == 1 ? " card" : " cards"));
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