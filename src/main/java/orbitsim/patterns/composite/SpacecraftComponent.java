package orbitsim.patterns.composite;

import orbitsim.exception.SystemFaultException;
import java.util.List;

/**
 * ═══════════════════════════════════════════════════════════════
 * PATTERN: Composite — Interfaccia Component
 * ═══════════════════════════════════════════════════════════════
 * <p>
 * Il Composite permette di trattare uniformemente:
 * - oggetti SINGOLI (Subsystem — foglie, es. "O2 Recycler")
 * - COMPOSIZIONI di oggetti (SpacecraftModule — nodi, es. "Life Support")
 * <p>
 * STRUTTURA nel progetto:
 *   SpacecraftModule "HORUS-21 Systems"  ← Composite root
 *     SpacecraftModule "Propulsion"       ← nodo intermedio
 *       Subsystem "Main Engine"           ← foglia
 *       Subsystem "Reactor Core"          ← foglia
 *       Subsystem "RCS Thrusters"         ← foglia
 *     SpacecraftModule "Life Support"     ← nodo intermedio
 *       Subsystem "O2 Recycler"           ← foglia
 *       Subsystem "CO2 Scrubber"          ← foglia
 *     SpacecraftModule "Avionics"
 *       Subsystem "Navigation Comp"
 *       Subsystem "Comm Array"
 * <p>
 * PERCHÉ è utile?
 * Root.getStatus() → propaga ai moduli → propaga ai subsystem
 * Il chiamante non sa quanti livelli ci sono — tratta tutto uniformemente.
 * Aggiungere un nuovo modulo (es. "Scientific Instruments") non richiede
 * modificare nessun codice esistente.
 */
public interface SpacecraftComponent {

    /** Identificatore univoco del componente (es. "REACTOR", "LIFE"). */
    String getId();

    /** Nome leggibile per il report (es. "Reactor Core", "Life Support"). */
    String getName();

    /**
     * Stato aggregato del componente.
     * Per le foglie (Subsystem): stato diretto.
     * Per i nodi (SpacecraftModule): stato peggiore dei figli.
     * Esempio: se un Subsystem è CRITICAL, il suo modulo padre è almeno DEGRADED.
     */
    SystemStatus getStatus();

    /**
     * Report formattato a testo del componente e dei suoi figli.
     * Polimorfismo: il CLI chiama getStatusReport() senza sapere
     * se sta parlando con un nodo o una foglia.
     */
    String getStatusReport();

    /**
     * Spegni il componente. Propaga ai figli (nodi) o applica direttamente (foglie).
     * @throws SystemFaultException se lo shutdown fallisce — Exception Shielding
     */
    void shutdown() throws SystemFaultException;

    /**
     * Restituisce i figli del componente.
     * Foglie (Subsystem): restituiscono lista vuota.
     * Nodi (SpacecraftModule): restituiscono la lista dei figli.
     */
    List<SpacecraftComponent> getChildren();

    /**
     * Metodo default (Java 8+): implementazione standard nell'interfaccia.
     * Una foglia non ha figli → la lista è vuota → isEmpty() = true.
     * Evita di duplicare questo controllo in Subsystem.
     * Le sottoclassi possono sovrascrivere se necessario.
     */
    default boolean isLeaf() {
        return getChildren().isEmpty(); // true se nessun figlio
    }
}
