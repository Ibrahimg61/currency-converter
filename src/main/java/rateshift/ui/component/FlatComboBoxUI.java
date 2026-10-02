package rateshift.ui.component;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import rateshift.ui.Theme;

/** Flat combo box without a background of its own; it sits on a {@link Card}. */
final class FlatComboBoxUI extends BasicComboBoxUI {

    @Override
    protected JButton createArrowButton() {
        JButton button = new JButton() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = Painting.smooth(g);
                g2.setColor(Theme.MUTED);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                g2.drawLine(cx - 4, cy - 2, cx, cy + 2);
                g2.drawLine(cx, cy + 2, cx + 4, cy - 2);
                g2.dispose();
            }
        };
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(24, 24));
        return button;
    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        // The background comes from the surrounding card.
    }

    @Override
    protected ComboPopup createPopup() {
        BasicComboPopup popup = new BasicComboPopup(comboBox) {
            private static final long serialVersionUID = 1L;

            @Override
            protected JScrollPane createScroller() {
                JScrollPane scroller = super.createScroller();
                scroller.setBorder(BorderFactory.createEmptyBorder());
                scroller.getViewport().setBackground(Theme.CARD);
                scroller.getVerticalScrollBar().setUnitIncrement(16);
                return scroller;
            }
        };
        popup.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        popup.getList().setBackground(Theme.CARD);
        return popup;
    }
}
