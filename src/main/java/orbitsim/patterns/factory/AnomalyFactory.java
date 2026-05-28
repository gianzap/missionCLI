package orbitsim.patterns.factory;

import orbitsim.exception.OrbitSimException;
import orbitsim.patterns.chain.AnomalyContext;

/**
 * ═══════════════════════════════════════════════════════════════
 * PATTERN: Factory Method
 * ═══════════════════════════════════════════════════════════════
 *
 * Crea oggetti AnomalyContext con i parametri corretti
 * per ogni tipo di anomalia, nascondendo questa logica al CLI.
 *
 * SENZA Factory, il CLI dovrebbe:
 *   if (type.equals("REACTOR")) ctx = new AnomalyContext("Reactor spike","REACTOR_CORE",4);
 *   else if (type.equals("PRESSURE")) ctx = new AnomalyContext("Pressure","HULL_B",3);
 *   ...  ← logica di business nel CLI (sbagliato)
 *
 * CON Factory: il CLI chiama solo AnomalyFactory.create("REACTOR").
 * La logica di business (che severity ha REACTOR? che sistema?) sta qui.
 * Open/Closed: aggiungere "SOLAR_STORM" → aggiungo un case qui, nient'altro cambia.
 *
 * CLASSE UTILITY:
 * - Costruttore private → non istanziabile (come java.util.Collections, java.util.Arrays)
 * - Solo metodi static → si usa come AnomalyFactory.create(...), senza new
 * - final → non estendibile (non ha senso estendere una utility class)
 */
public final class AnomalyFactory {

    /**
     * Costruttore private: previene new AnomalyFactory().
     * Non ha senso istanziare questa classe — contiene solo metodi static.
     */
    private AnomalyFactory() {}

    /**
     * Crea un AnomalyContext per il tipo di anomalia richiesto.
     *
     * switch expression (Java 14+):
     * - La freccia '->' evita il fall-through del vecchio switch
     * - Può restituire un valore direttamente
     * - Il compilatore verifica che tutti i casi siano coperti (+ default)
     *
     * .toUpperCase(): normalizzazione dell'input — "reactor" funziona come "REACTOR"
     * Questo è input sanitization.
     *
     * @param type  stringa del tipo (es. "REACTOR", "PRESSURE")
     * @return      AnomalyContext pronto per la pipeline Chain
     * @throws OrbitSimException se il tipo non è riconosciuto — Exception Shielding:
     *         il CLI mostra solo il messaggio operatore, non i dettagli interni
     */
    public static AnomalyContext create(String type) throws OrbitSimException {
        return switch (type.toUpperCase()) {  // normalizza l'input prima del confronto
            // Ogni case: descrizione leggibile, ID sistema, severity 1-5
            case "REACTOR"      -> new AnomalyContext(
                "Reactor temperature spike",    // descrizione per il report
                "REACTOR_CORE",                 // ID del sistema sorgente
                4);                             // severity iniziale (può essere elevata da AssessmentHandler)

            case "PRESSURE"     -> new AnomalyContext(
                "Hull pressure loss detected",
                "HULL_SECTION_B",
                3);

            case "NAVIGATION"   -> new AnomalyContext(
                "Navigation computer fault",
                "NAV_COMPUTER",
                2);                             // bassa severity: backup nav disponibile

            case "LIFE_SUPPORT" -> new AnomalyContext(
                "O2 recycler malfunction",
                "LIFE_SUPPORT_A",
                5);                             // severity massima — vita umana in pericolo → abort

            case "COMMS"        -> new AnomalyContext(
                "Comm array failure",
                "COMMS_ARRAY",
                2);                             // bassa: blackout temporaneo, non pericoloso

            case "THRUSTER"     -> new AnomalyContext(
                "Thruster RCS-4 offline",
                "RCS_THRUSTER_4",
                3);

            // default: tipo non riconosciuto → eccezione con messaggio operatore chiaro
            // La causa originale (nessuna) non è rilevante — è un input errato dell'utente
            default -> throw new OrbitSimException(
                "Unknown anomaly type: '" + type +
                "'. Available: REACTOR, PRESSURE, NAVIGATION, LIFE_SUPPORT, COMMS, THRUSTER");
        };
    }

    /**
     * Restituisce la lista dei tipi disponibili come stringa formattata.
     * Usato dal CLI per mostrare l'help del comando INJECT_ANOMALY.
     * Metodo static: nessuna istanza necessaria.
     */
    public static String listTypes() {
        return "  REACTOR | PRESSURE | NAVIGATION | LIFE_SUPPORT | COMMS | THRUSTER";
    }
}
