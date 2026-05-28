package orbitsim.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogManager {

    private static final Logger LOG = Logger.getLogger("missionTextLog");
    private FileHandler handler;

    public LogManager(String filename) {
        try {
            // Crea la directory se non esiste
            Path logPath = Paths.get(filename);
            if (logPath.getParent() != null) {
                Files.createDirectories(logPath.getParent());
            }

            handler = new FileHandler(filename, true);
            handler.setFormatter(new SimpleFormatter());
            LOG.addHandler(handler);
            LOG.setUseParentHandlers(false); // evita uscita duplicato sulla console

        } catch (IOException e) {
            // Exception Shielding: non esponiamo lo stack trace all'utente
            // Il logger di sistema registra il problema internamente
            Logger.getLogger(LogManager.class.getName())
                    .warning("Log file unavailable, falling back to console: " + e.getMessage());
        }
    }

    public void appendLogInfo(String textContent) {
        LOG.log(Level.INFO, textContent);
    }

    public void appendLogWarn(String textContent) {
        LOG.log(Level.WARNING, textContent);
    }

    public void appendLogSevere(String textContent) {
        LOG.log(Level.SEVERE, textContent);
    }

    public void closeLog() {
        if (handler != null) handler.close();
    }
}