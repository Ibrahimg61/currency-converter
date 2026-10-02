package rateshift.ui.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;

import rateshift.ui.Theme;

/** Flat, rounded button with a hover effect. */
public class FlatButton extends JButton {

    private static final long serialVersionUID = 1L;

    /** Appearance of the button. */
    public enum Style {
        /** Subtle surface with text. */
        SUBTLE,
        /** Round accent button with a swap icon. */
        SWAP
    }

    private final Style style;
    private boolean hover;

    public FlatButton(String text) {
        this(text, Style.SUBTLE);
    }

    public FlatButton(String text, Style style) {
        super(text);
        this.style = style;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setFont(Theme.font(12, java.awt.Font.BOLD));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(6, 14, 6, 14));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Painting.smooth(g);
        g2.setColor(background());
        int arc = style == Style.SWAP ? getHeight() : 14;
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));
        if (style == Style.SWAP) {
            paintSwapIcon(g2);
        }
        g2.dispose();
        setForeground(isEnabled() ? Theme.TEXT : Theme.MUTED);
        super.paintComponent(g);
    }

    private Color background() {
        if (!isEnabled()) {
            return new Color(255, 255, 255, 15);
        }
        if (style == Style.SWAP) {
            return hover ? Theme.ACCENT_ALT : Theme.ACCENT;
        }
        return hover ? Theme.CARD_HOVER : new Color(255, 255, 255, 30);
    }

    /** Two opposing arrows; drawn by hand because not every font contains ⇅. */
    private void paintSwapIcon(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        g2.drawLine(cx - 4, cy - 7, cx - 4, cy + 7);
        g2.drawLine(cx - 4, cy - 7, cx - 8, cy - 3);
        g2.drawLine(cx - 4, cy - 7, cx, cy - 3);
        g2.drawLine(cx + 4, cy + 7, cx + 4, cy - 7);
        g2.drawLine(cx + 4, cy + 7, cx + 8, cy + 3);
        g2.drawLine(cx + 4, cy + 7, cx, cy + 3);
    }
}
