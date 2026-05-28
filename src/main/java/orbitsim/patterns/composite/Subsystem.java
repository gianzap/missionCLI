package orbitsim.patterns.composite;

import java.util.Collections;
import java.util.List;

/**
 * PATTERN: Composite — Leaf (Foglia)
 *
 * Rappresenta un singolo sottosistema fisico della navicella.
 * Non ha figli — è il nodo terminale dell'albero Composite.
 *
 * Esempi: "O2 Recycler", "Main Engine", "Navigation Computer"
 *
 * Tiene traccia di:
 * - status: stato corrente (NOMINAL/DEGRADED/CRITICAL/OFFLINE)
 * - value: valore corrente del sensore principale (temperatura, pressione, ecc.)
 * - unit: unità di misura per il report
 */
public class Subsystem implements SpacecraftComponent {

    private final String id;    // es. "REACTOR", "O2_SYS"
    private final String name;  // es. "Reactor Core", "O2 Recycler"
    private SystemStatus status; // stato corrente — mutabile (il sistema può degradarsi)
    private double value;        // valore corrente del parametro principale
    private final String unit;  // unità: "°C", "%", "dBm" — final, non cambia

    /**
     * Costruttore: inizializza il subsystem in stato NOMINAL.
     * Value = 100.0 come default — override con setValue() se necessario.
     */
    public Subsystem(String id, String name, String unit) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.status = SystemStatus.NOMINAL; // ogni sistema parte nominale
        this.value = 100.0;                 // valore di default
    }

    /** Aggiorna il valore del sensore. Chiamato da Spacecraft.tick(). */
    public void setValue(double v) { this.value = v; }

    /** Aggiorna lo stato. Chiamato da Spacecraft.degradeSystem(). */
    public void setStatus(SystemStatus s) { this.status = s; }

    /** Valore corrente — usato da Spacecraft per la telemetria. */
    public double getValue() { return value; }

    /**
     * Implementazione diretta: una foglia risponde direttamente con il proprio stato.
     * Nessuna aggregazione — non ha figli da cui aggregare.
     */
    @Override public SystemStatus getStatus() { return status; }

    /**
     * Report formattato per il comando SYSTEMS del CLI.
     * String.format con %-22s: 22 caratteri, allineato a sinistra.
     * Crea colonne allineate nel CLI.
     */
    @Override public String getStatusReport() {
        return String.format("%-22s [%s] %.1f %s\n",
            name, status, value, unit);
    }

    /**
     * Shutdown della foglia: semplicemente imposta lo stato a OFFLINE.
     * Nessuna propagazione — non ci sono figli.
     */
    @Override public void shutdown() {
        this.status = SystemStatus.OFFLINE;
    }

    /**
     * Una foglia non ha figli.
     * Collections.emptyList() restituisce una lista immutabile singleton —
     * non alloca un nuovo ArrayList ogni volta (efficiente).
     * isLeaf() nella superclass chiama getChildren().isEmpty() → true.
     */
    @Override public List<SpacecraftComponent> getChildren() {
        return Collections.emptyList();
    }

    @Override public String getId()   { return id; }
    @Override public String getName() { return name; }
}
