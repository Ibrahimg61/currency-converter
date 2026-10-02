package de.waehrungsumrechner;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import de.waehrungsumrechner.data.HttpRateSource;
import de.waehrungsumrechner.data.RateStore;
import de.waehrungsumrechner.service.ExchangeRateService;
import de.waehrungsumrechner.ui.MainWindow;

public final class Main {

    /** Überschreibbar mit {@code -Dwaehrungsumrechner.data=/pfad}. */
    private static final String DATA_DIR_PROPERTY = "waehrungsumrechner.data";

    private Main() { }

    public static void main(String[] args) {
        ExchangeRateService service = new ExchangeRateService(new HttpRateSource(), new RateStore(dataDirectory()));
        SwingUtilities.invokeLater(() -> {
            try {
                // Plattformunabhängiges Look & Feel, damit das eigene Design überall gleich aussieht
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                // Standard-Look & Feel genügt
            }
            new MainWindow(service).setVisible(true);
        });
    }

    private static Path dataDirectory() {
        String configured = System.getProperty(DATA_DIR_PROPERTY);
        if (configured != null && !configured.isBlank()) {
            return Paths.get(configured);
        }
        return Paths.get(System.getProperty("user.home"), ".waehrungsumrechner");
    }
}
