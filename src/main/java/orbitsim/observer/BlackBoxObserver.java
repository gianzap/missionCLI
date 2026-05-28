package orbitsim.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PATTERN: Observer — Subscriber "Scatola Nera".
 *
 * Registra TUTTI gli eventi di missione in ordine cronologico.
 * Analogo al Flight Data Recorder degli aerei reali.
 *
 * Responsabilità:
 * - Accumulare tutti gli eventi (nessun filtro)
 * - Fornire statistiche per il report finale (countBySeverity)
 * - Alimentare la funzione REPORT del CLI
 *
 * Non stampa nulla a video — è solo un archivio.
 * ConsoleAlertObserver si occupa dell'output visibile.
 * Separazione delle responsabilità.
 */
public class BlackBoxObserver implements MissionObserver {

    // Lista interna degli eventi — List perché l'ordine conta (cronologico)
    // e i duplicati sono validi (due eventi dello stesso tipo sono due eventi distinti)
    private final List<MissionEvent> log = new ArrayList<>();

    /**
     * Aggiunge l'evento al registro.
     * Chiamato per OGNI evento, indipendentemente dal tipo o severity.
     * Nessuna logica di filtro — il BlackBox registra tutto.
     */
    @Override
    public void onEvent(MissionEvent event) {
        log.add(event); // O(1) — ArrayList aggiunge in coda
    }

    /**
     * Restituisce una vista NON modificabile del log.
     * Collections.unmodifiableList() crea un wrapper che lancia
     * UnsupportedOperationException se si prova a modificare la lista.
     * Protegge l'incapsulamento senza copiare i dati (efficiente).
     */
    public List<MissionEvent> getLog() {
        return Collections.unmodifiableList(log);
    }

    /**
     * STREAM API: conta gli eventi per severity.
     *
     * Equivalente imperativo:
     *   long count = 0;
     *   for (MissionEvent e : log) {
     *       if (e.severity() == sev) count++;
     *   }
     *   return count;
     *
     * Versione Stream:
     * - .stream()         → crea la stream dalla lista
     * - .filter(...)      → mantiene solo eventi con quella severity
     * - .count()          → operazione terminale, scatena l'esecuzione
     */
    public long countBySeverity(MissionEvent.Severity sev) {
        return log.stream()
            .filter(e -> e.severity() == sev) // lambda: e è il parametro, e.severity() == sev è il predicato
            .count();                          // operazione terminale — restituisce il conteggio
    }

    @Override public String getName() { return "BlackBox"; }
}
