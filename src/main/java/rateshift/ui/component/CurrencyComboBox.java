package rateshift.ui.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collection;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

import rateshift.model.CurrencyInfo;
import rateshift.ui.Theme;

/** Currency picker showing code and name; typing jumps by prefix (e.g. "US" → USD). */
public final class CurrencyComboBox extends JComboBox<CurrencyInfo> {

    private static final long serialVersionUID = 1L;
    private static final long TYPE_AHEAD_RESET_MS = 900;

    public CurrencyComboBox() {
        setUI(new FlatComboBoxUI());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder());
        setFont(Theme.font(16, Font.BOLD));
        setForeground(Theme.TEXT);
        setMaximumRowCount(10);
        setPreferredSize(new Dimension(150, 38));
        setRenderer(new Renderer());
        setKeySelectionManager(new PrefixSelectionManager());
    }

    /** Replaces the entries and selects {@code preferredCode} if present. */
    public void setCurrencies(Collection<String> codes, String preferredCode) {
        removeAllItems();
        for (String code : codes) {
            addItem(CurrencyInfo.of(code));
        }
        select(preferredCode);
    }

    public void select(String code) {
        for (int i = 0; i < getItemCount(); i++) {
            if (getItemAt(i).code().equals(code)) {
                setSelectedIndex(i);
                return;
            }
        }
    }

    public Optional<CurrencyInfo> selectedCurrency() {
        return Optional.ofNullable((CurrencyInfo) getSelectedItem());
    }

    private static final class Renderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean selected, boolean focus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
            CurrencyInfo currency = (CurrencyInfo) value;
            boolean inPopup = index >= 0;
            if (currency == null) {
                label.setText("");
            } else if (inPopup) {
                String nameColor = Theme.hex(selected ? Theme.TEXT : Theme.MUTED);
                label.setText("<html><b>" + currency.code() + "</b> &nbsp;<span style='color:#" + nameColor + "'>"
                        + escape(currency.name()) + "</span></html>");
            } else {
                label.setText(currency.code());
            }
            label.setFont(Theme.font(inPopup ? 14 : 16, Font.PLAIN));
            label.setOpaque(inPopup);
            label.setBackground(selected ? Theme.ACCENT : Theme.CARD);
            label.setForeground(Theme.TEXT);
            label.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            return label;
        }

        private static String escape(String text) {
            return text.replace("&", "&amp;").replace("<", "&lt;");
        }
    }

    private static final class PrefixSelectionManager implements KeySelectionManager {
        private final StringBuilder typed = new StringBuilder();
        private long lastKeyTime;

        @Override
        public int selectionForKey(char key, ComboBoxModel<?> model) {
            long now = System.currentTimeMillis();
            if (now - lastKeyTime > TYPE_AHEAD_RESET_MS) {
                typed.setLength(0);
            }
            lastKeyTime = now;
            typed.append(Character.toUpperCase(key));
            String prefix = typed.toString();
            for (int i = 0; i < model.getSize(); i++) {
                if (model.getElementAt(i).toString().toUpperCase().startsWith(prefix)) {
                    return i;
                }
            }
            return -1;
        }
    }
}
