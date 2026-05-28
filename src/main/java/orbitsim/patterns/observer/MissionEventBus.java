package orbitsim.patterns.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * ═══════════════════════════════════════════════════════════════
 * PATTERN: Observer — EventBus (Publisher)
 * ═══════════════════════════════════════════════════════════════
 *
 * Il Publisher centrale: quando viene pubblicato un evento,
 * tutti i subscriber (Observer) vengono notificati automaticamente.
 *
 * SUBSCRIBER nel progetto:
 *   - ConsoleAlertObserver → stampa alert a video
 *   - BlackBoxObserver     → registra per il report finale
 *
 * PERCHÉ Observer invece di chiamate dirette?
 * Senza Observer, AnomalyHandler dovrebbe conoscere e chiamare
 * direttamente console.alert(), blackBox.record(), ecc.
 * Con Observer: pubblica un evento, chi vuole ascolta.
 * Aggiungere EmailNotifier non tocca NESSUN codice esistente.
 * Questo è l'Open/Closed Principle in pratica.
 */
public class MissionEventBus {

    // Logger per uso interno — non esposto all'utente (Exception Shielding)
    private static final Logger LOG = Logger.getLogger(MissionEventBus.class.getName());

    // Lista degli observer registrati.
    // ArrayList: ordinata, ammette duplicati, accesso O(1) per indice.
    // In un contesto multi-thread useremmo CopyOnWriteArrayList.
    private final List<MissionObserver> observers = new ArrayList<>();

    /**
     * Registra un nuovo observer.
     * Chiamato nel costruttore di MissionCLI per il wiring iniziale.
     * Potrebbe essere chiamato anche a runtime per aggiungere subscriber.
     */
    public void subscribe(MissionObserver o)   { observers.add(o); }

    /**
     * Rimuove un observer.
     * Utile se un subscriber vuole smettere di ascoltare (es. sistema offline).
     */
    public void unsubscribe(MissionObserver o) { observers.remove(o); }

    /**
     * Pubblica un evento a TUTTI gli observer.
     *
     * Il try-catch DENTRO il for è intenzionale e fondamentale:
     * se un observer crasha, gli altri devono comunque ricevere l'evento.
     * Esempio: ConsoleAlertObserver lancia NullPointerException →
     *          BlackBoxObserver riceve comunque l'evento e lo registra.
     * Questo è resilienza — un singolo punto di fallimento non blocca il sistema.
     *
     * L'eccezione viene loggata internamente (Exception Shielding):
     * l'utente non vede lo stack trace, solo il log interno.
     */
    public void publish(MissionEvent event) {
        // Log interno: traccia ogni evento pubblicato per debug
        LOG.info("[EVENTBUS] " + event.type() + " | " + event.message());

        // Itera su una copia della lista sarebbe più sicuro in multi-thread,
        // ma qui siamo single-thread, quindi va bene iterare direttamente.
        for (MissionObserver o : observers) {
            try {
                o.onEvent(event); // notifica il subscriber
            } catch (Exception e) {
                // Exception Shielding: l'errore del subscriber non si propaga
                // L'utente non lo vede, il log interno sì.
                LOG.severe("Observer [" + o.getName() + "] failed: " + e.getMessage());
            }
        }
    }
}
