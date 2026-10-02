package rateshift.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import rateshift.ui.component.FlatButton;
import rateshift.ui.component.GradientCard;

/** Large result display with the rate in both directions and a copy button. */
final class ResultCard extends GradientCard {

    private static final long serialVersionUID = 1L;
    private static final String PLACEHOLDER = "–";
    private static final String COPY_TEXT = "Copy";
    private static final Color SOFT_WHITE = new Color(255, 255, 255, 215);

    private final JLabel resultLabel = new JLabel(PLACEHOLDER);
    private final JLabel targetLabel = new JLabel(" ");
    private final JLabel rateLabel = new JLabel(" ");
    private final JLabel inverseLabel = new JLabel(" ");
    private final FlatButton copyButton = new FlatButton(COPY_TEXT);

    ResultCard() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));

        resultLabel.setFont(Theme.font(40, Font.BOLD));
        resultLabel.setForeground(Color.WHITE);
        style(targetLabel, 15, Font.BOLD, new Color(255, 255, 255, 200));
        style(rateLabel, 13, Font.PLAIN, SOFT_WHITE);
        style(inverseLabel, 13, Font.PLAIN, new Color(255, 255, 255, 170));
        copyButton.addActionListener(e -> copyResult());

        JLabel caption = new JLabel("RESULT");
        style(caption, 11, Font.BOLD, new Color(255, 255, 255, 190));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(caption, BorderLayout.WEST);
        top.add(copyButton, BorderLayout.EAST);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        add(alignLeft(top));
        add(Box.createVerticalStrut(6));
        add(alignLeft(resultLabel));
        add(alignLeft(targetLabel));
        add(Box.createVerticalStrut(14));
        add(alignLeft(rateLabel));
        add(Box.createVerticalStrut(2));
        add(alignLeft(inverseLabel));
    }

    void show(String result, String target, String rate, String inverseRate) {
        resultLabel.setText(result);
        targetLabel.setText(target);
        rateLabel.setText(rate);
        inverseLabel.setText(inverseRate);
    }

    private void copyResult() {
        String text = resultLabel.getText();
        if (PLACEHOLDER.equals(text)) {
            return;
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
        copyButton.setText("Copied ✓");
        Timer reset = new Timer(1200, e -> copyButton.setText(COPY_TEXT));
        reset.setRepeats(false);
        reset.start();
    }

    private static void style(JLabel label, int size, int fontStyle, Color color) {
        label.setFont(Theme.font(size, fontStyle));
        label.setForeground(color);
    }

    private static JComponent alignLeft(JComponent component) {
        component.setAlignmentX(LEFT_ALIGNMENT);
        return component;
    }
}
