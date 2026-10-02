package de.waehrungsumrechner.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.OptionalDouble;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.waehrungsumrechner.format.AmountParser;
import de.waehrungsumrechner.format.Formats;
import de.waehrungsumrechner.model.CurrencyInfo;
import de.waehrungsumrechner.model.ExchangeRates;
import de.waehrungsumrechner.model.RateHistory;
import de.waehrungsumrechner.service.ExchangeRateService;
import de.waehrungsumrechner.ui.component.Card;
import de.waehrungsumrechner.ui.component.CurrencyComboBox;
import de.waehrungsumrechner.ui.component.FlatButton;

/** Hauptfenster: Eingabe, Währungswahl, Ergebnis und Kursverlauf. */
public final class MainWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_FROM = "EUR";
    private static final String DEFAULT_TO = "USD";

    private final transient ExchangeRateService service;
    private transient ExchangeRates rates;
    private transient RateHistory history = RateHistory.empty();
    private boolean adjusting;

    private final JTextField amountField = new JTextField("100");
    private final CurrencyComboBox fromBox = new CurrencyComboBox();
    private final CurrencyComboBox toBox = new CurrencyComboBox();
    private final ResultCard resultCard = new ResultCard();
    private final HistoryCard historyCard = new HistoryCard();
    private final JLabel statusLabel = new JLabel("Lade Kurse …");
    private final FlatButton refreshButton = new FlatButton("Aktualisieren");

    public MainWindow(ExchangeRateService service) {
        super("Währungsumrechner");
        this.service = service;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Theme.BACKGROUND);
        add(buildContent(), BorderLayout.CENTER);
        setMinimumSize(new Dimension(520, 700));
        setSize(560, 760);
        setLocationRelativeTo(null);

        // Gespeicherte Kurse sofort anzeigen, dann live aktualisieren.
        service.cachedRates().ifPresent(cached -> applyRates(cached, service.history()));
        refresh();
    }

    // ---- Aufbau ----------------------------------------------------------

    private JComponent buildContent() {
        JPanel root = new JPanel();
        root.setOpaque(false);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(28, 28, 24, 28));

        root.add(buildHeader());
        root.add(Box.createVerticalStrut(22));
        root.add(buildAmountCard());
        root.add(Box.createVerticalStrut(12));
        root.add(buildPickers());
        root.add(Box.createVerticalStrut(12));
        root.add(fixedHeight(resultCard, 210));
        root.add(Box.createVerticalStrut(12));
        root.add(alignLeft(historyCard));
        root.add(Box.createVerticalStrut(12));
        root.add(alignLeft(mutedLabel("Kurse: open.er-api.com · Basis EUR · alle Angaben ohne Gewähr", Font.PLAIN)));

        amountField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { render(); }

            @Override
            public void removeUpdate(DocumentEvent e) { render(); }

            @Override
            public void changedUpdate(DocumentEvent e) { render(); }
        });
        fromBox.addActionListener(e -> render());
        toBox.addActionListener(e -> render());
        return root;
    }

    private JComponent buildHeader() {
        JLabel title = new JLabel("Währungsumrechner");
        title.setFont(Theme.font(24, Font.BOLD));
        title.setForeground(Theme.TEXT);
        statusLabel.setFont(Theme.font(12, Font.PLAIN));
        statusLabel.setForeground(Theme.MUTED);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(alignLeft(title));
        titles.add(Box.createVerticalStrut(4));
        titles.add(alignLeft(statusLabel));

        refreshButton.addActionListener(e -> refresh());
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(titles, BorderLayout.CENTER);
        header.add(refreshButton, BorderLayout.EAST);
        return fixedHeight(header, 52);
    }

    private JComponent buildAmountCard() {
        amountField.setOpaque(false);
        amountField.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        amountField.setFont(Theme.font(28, Font.BOLD));
        amountField.setForeground(Theme.TEXT);
        amountField.setCaretColor(Theme.ACCENT);
        amountField.setSelectionColor(Theme.ACCENT);
        amountField.setSelectedTextColor(Color.WHITE);

        Card card = new Card();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        card.add(caption("BETRAG"), BorderLayout.NORTH);
        card.add(amountField, BorderLayout.CENTER);
        return fixedHeight(card, 86);
    }

    private JComponent buildPickers() {
        FlatButton swap = new FlatButton("", FlatButton.Style.SWAP);
        swap.setPreferredSize(new Dimension(44, 44));
        swap.addActionListener(e -> swapCurrencies());

        JPanel pickers = new JPanel(new GridBagLayout());
        pickers.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        pickers.add(pickerCard("VON", fromBox), c);
        c.weightx = 0;
        c.gridx = 1;
        c.insets = new Insets(14, 12, 0, 12);
        pickers.add(swap, c);
        c.weightx = 1;
        c.gridx = 2;
        c.insets = new Insets(0, 0, 0, 0);
        pickers.add(pickerCard("NACH", toBox), c);
        return fixedHeight(pickers, 86);
    }

    private static JComponent pickerCard(String title, CurrencyComboBox box) {
        Card card = new Card();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12, 18, 8, 10));
        card.add(caption(title), BorderLayout.NORTH);
        card.add(box, BorderLayout.CENTER);
        return card;
    }

    // ---- Kurse laden -------------------------------------------------------

    private void refresh() {
        refreshButton.setEnabled(false);
        setStatus("Lade Kurse …", Theme.MUTED);
        new SwingWorker<ExchangeRates, Void>() {
            @Override
            protected ExchangeRates doInBackground() throws Exception {
                return service.refresh();
            }

            @Override
            protected void done() {
                refreshButton.setEnabled(true);
                try {
                    applyRates(get(), service.history());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    showRefreshFailure();
                }
            }
        }.execute();
    }

    private void showRefreshFailure() {
        if (rates == null) {
            setStatus("Keine Verbindung und keine gespeicherten Kurse", Theme.WARNING);
        } else {
            setStatus("Offline · gespeicherte Kurse vom " + Formats.date(rates.lastUpdate()), Theme.WARNING);
        }
    }

    private void applyRates(ExchangeRates newRates, RateHistory newHistory) {
        String from = fromBox.selectedCurrency().map(CurrencyInfo::code).orElse(DEFAULT_FROM);
        String to = toBox.selectedCurrency().map(CurrencyInfo::code).orElse(DEFAULT_TO);

        rates = newRates;
        history = newHistory;

        adjusting = true;
        fromBox.setCurrencies(rates.currencyCodes(), from);
        toBox.setCurrencies(rates.currencyCodes(), to);
        adjusting = false;

        boolean live = rates.source() == ExchangeRates.Source.LIVE;
        String stand = "Stand " + Formats.date(rates.lastUpdate());
        setStatus(live ? "Live · " + stand : "Gespeichert · " + stand, live ? Theme.SUCCESS : Theme.WARNING);
        render();
    }

    private void setStatus(String text, Color dotColor) {
        statusLabel.setText("<html><span style='color:#" + Theme.hex(dotColor) + "'>●</span>&nbsp; " + text + "</html>");
    }

    // ---- Umrechnen -----------------------------------------------------------

    private void swapCurrencies() {
        CurrencyInfo from = (CurrencyInfo) fromBox.getSelectedItem();
        CurrencyInfo to = (CurrencyInfo) toBox.getSelectedItem();
        adjusting = true;
        fromBox.setSelectedItem(to);
        toBox.setSelectedItem(from);
        adjusting = false;
        render();
    }

    private void render() {
        if (adjusting || rates == null || fromBox.getSelectedItem() == null || toBox.getSelectedItem() == null) {
            return;
        }
        CurrencyInfo from = (CurrencyInfo) fromBox.getSelectedItem();
        CurrencyInfo to = (CurrencyInfo) toBox.getSelectedItem();

        OptionalDouble amount = AmountParser.parse(amountField.getText());
        String result = amount.isPresent()
                ? Formats.amount(rates.convert(amount.getAsDouble(), from.code(), to.code()))
                : "–";
        resultCard.show(result,
                to.code() + " · " + to.name(),
                "1 " + from.code() + " = " + Formats.amount(rates.rate(from.code(), to.code())) + " " + to.code(),
                "1 " + to.code() + " = " + Formats.amount(rates.rate(to.code(), from.code())) + " " + from.code());
        historyCard.show(from.code(), to.code(), history.series(from.code(), to.code()));
    }

    // ---- Layout-Helfer ---------------------------------------------------------

    private static JLabel caption(String text) {
        return mutedLabel(text, Font.BOLD);
    }

    private static JLabel mutedLabel(String text, int fontStyle) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.font(11, fontStyle));
        label.setForeground(Theme.MUTED);
        return label;
    }

    private static JComponent alignLeft(JComponent component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        return component;
    }

    private static JComponent fixedHeight(JComponent component, int height) {
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        return alignLeft(component);
    }
}
