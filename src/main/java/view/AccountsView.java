package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginViewModel;
import interface_adapter.signup.SignupViewModel;
import view.style.FontLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

/**
 * The first screen a logged-out user sees: an "Accounts" heading with SIGN UP and LOGIN buttons.
 * Colours and proportions come from the Figma mockup. Everything is positioned as a fraction of the
 * panel size (see doLayout), so it stays correct if the window size changes.
 */
public class AccountsView extends JPanel {
    private static final Color BACKGROUND = Color.decode("#8FB5CF");
    private static final Color PILL = Color.decode("#D9D9D9");
    private static final Color PILL_HOVER = Color.decode("#C9C9C9");
    private static final Color PILL_PRESSED = Color.decode("#B5B5B5");

    // Measured from the mockup (a 1226 x 591 frame). Horizontal values are fractions of the panel width.
    // Pill height scales with the width too, so the pills keep the mockup's shape in any window size.
    private static final double TITLE_X = 0.2675, TITLE_W = 0.4790;
    private static final double SIGNUP_X = 0.0759, LOGIN_X = 0.5579, BUTTON_W = 0.3524;
    private static final double PILL_H = 137.0 / 1226.0;
    // Vertical values are fractions of the panel height: the title's top edge, and the buttons' bottom edge.
    private static final double TITLE_TOP = 0.1015;
    private static final double BUTTON_BOTTOM = 0.8714;

    private static final double CORNER = 0.84;           // corner diameter as a fraction of the pill height
    private static final double FONT_RATIO = 0.392;      // font size as a fraction of the pill height

    private final Pill title = new Pill("Accounts", false);
    private final Pill signUp = new Pill("SIGN UP", true);
    private final Pill logIn = new Pill("LOGIN", true);
    private float fontSize = -1;

    public AccountsView(ViewManagerModel viewManagerModel) {
        setLayout(null);                       // children are placed in doLayout()
        setBackground(BACKGROUND);

        signUp.addActionListener(e -> viewManagerModel.navigate(SignupViewModel.VIEW_NAME, true));
        logIn.addActionListener(e -> viewManagerModel.navigate(LoginViewModel.VIEW_NAME, true));

        add(title);
        add(signUp);
        add(logIn);
    }

    @Override
    public void doLayout() {
        final int w = getWidth();
        final int h = getHeight();

        final int pillH = (int) Math.round(w * PILL_H);
        final int buttonY = (int) Math.round(h * BUTTON_BOTTOM) - pillH;
        title.setBounds(px(w, TITLE_X), (int) Math.round(h * TITLE_TOP), px(w, TITLE_W), pillH);
        signUp.setBounds(px(w, SIGNUP_X), buttonY, px(w, BUTTON_W), pillH);
        logIn.setBounds(px(w, LOGIN_X), buttonY, px(w, BUTTON_W), pillH);

        final float size = (float) (pillH * FONT_RATIO);
        if (Math.abs(size - fontSize) > 0.5f) {              // only reload the font when the size really changed
            fontSize = size;
            final Font font = FontLoader.loadAdlam(size);
            title.setFont(font);
            signUp.setFont(font);
            logIn.setFont(font);
        }
    }

    private static int px(int total, double fraction) {
        return (int) Math.round(total * fraction);
    }

    /** A rounded light-gray pill with centred black text. Non-clickable when used as the heading. */
    private static class Pill extends JButton {
        private boolean hovering;

        Pill(String text, boolean clickable) {
            super(text);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setForeground(Color.BLACK);

            if (clickable) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovering = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovering = false; repaint(); }
                });
            }
            else {
                setFocusable(false);
                setRequestFocusEnabled(false);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            final Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

            final int w = getWidth();
            final int h = getHeight();

            Color fill = PILL;
            if (getCursor().getType() == Cursor.HAND_CURSOR) {            // only the clickable pills react
                if (getModel().isPressed()) fill = PILL_PRESSED;
                else if (hovering) fill = PILL_HOVER;
            }
            g2.setColor(fill);
            final int arc = (int) Math.round(h * CORNER);
            g2.fillRoundRect(0, 0, w, h, arc, arc);

            // centre the actual ink of the text, so the letters look centred in the pill
            final FontRenderContext frc = g2.getFontRenderContext();
            final GlyphVector glyphs = getFont().createGlyphVector(frc, getText());
            final Rectangle2D ink = glyphs.getVisualBounds();
            final float x = (float) ((w - ink.getWidth()) / 2 - ink.getX());
            final float y = (float) ((h - ink.getHeight()) / 2 - ink.getY());

            g2.setColor(getForeground());
            g2.drawGlyphVector(glyphs, x, y);
            g2.dispose();
        }
    }
}
