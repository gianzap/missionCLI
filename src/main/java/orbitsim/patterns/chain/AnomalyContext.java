package orbitsim.patterns.chain;

import java.util.ArrayList;
import java.util.List;

/**
 * ═══════════════════════════════════════════════════════
 * Contesto dell'anomalia che percorre la pipeline Chain.
 * ═══════════════════════════════════════════════════════
 * Questo oggetto viene passato da handler a handler.
 * Ogni handler può:
 *  - leggere i dati dell'anomalia
 *  - arricchirlo con nuove informazioni (logAction)
 *  - modificare la severity se necessario
 *  - segnalare che la missione va abortita
 *
 * È un oggetto MUTABILE — si arricchisce attraverso la catena.
 * Opposto al Memento (immutabile).
 */
public class AnomalyContext {

    private final String anomalyType;    // descrizione del guasto (es. "Reactor spike")
    private final String sourceSystem;   // sistema sorgente (es. "REACTOR_CORE")
    private int severity;                // gravità 1-5, può essere modificata dagli handler

    // Flag impostato da IsolationHandler quando il sistema è stato isolato
    private boolean handled = false;

    // Flag impostato da EscalationHandler — severity 5 → abort
    private boolean missionAborted = false;

    // Registro di tutte le azioni compiute dagli handler.
    // Usato per il report finale e per la demo (mostra ogni step).
    private final List<String> actionLog = new ArrayList<>();

    /**
     * Costruttore: inizializza il contesto con i dati dell'anomalia.
     * I campi final (anomalyType, sourceSystem) non cambiano mai —
     * sono i "fatti" dell'anomalia. Severity invece può crescere.
     */
    public AnomalyContext(String anomalyType, String sourceSystem, int severity) {
        this.anomalyType = anomalyType;
        this.sourceSystem = sourceSystem;
        this.severity = severity;
    }

    /**
     * Aggiunge un'azione al log E la stampa a video.
     * Chiamato dagli handler per rendere visibile nella demo
     * ogni step della pipeline Chain of Responsibility.
     * System.out.println qui è intenzionale: è output narrativo del CLI.
     */
    public void logAction(String action) {
        actionLog.add(action);
        System.out.println("    > " + action); // indentato per visibilità nel CLI
    }

    // ── getter / setter ───────────────────────────────────────────────

    public String getAnomalyType()     { return anomalyType; }
    public String getSourceSystem()    { return sourceSystem; }
    public int getSeverity()           { return severity; }

    /** Setter per severity: usato da AssessmentHandler per elevare la gravità. */
    public void setSeverity(int s)     { this.severity = s; }

    public boolean isHandled()         { return handled; }

    /** Chiamato da IsolationHandler quando il sistema è stato contenuto. */
    public void markHandled()          { this.handled = true; }

    public boolean isMissionAborted()  { return missionAborted; }

    /** Chiamato da EscalationHandler — severity 5 → impossibile continuare. */
    public void abortMission()         { this.missionAborted = true; }

    public List<String> getActionLog() { return actionLog; }
}
