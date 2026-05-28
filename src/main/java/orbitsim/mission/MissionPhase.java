package orbitsim.mission;

/**
 *
 * PATTERN: Strategy — Interfaccia della strategia
 * <p>
 * Ogni fase della missione ha comandi diversi, messaggi narrativi diversi,
 * e transizioni consentite diverse. Invece di un gigantesco switch nel CLI:
 * Con Strategy, il CLI delega TUTTO a currentPhase:
 *   currentPhase.availableCommands() → comandi validi ora
 *   currentPhase.onEnter() → messaggio narrativo di ingresso
 * <p>
 * FASI IMPLEMENTATE:
 *   LaunchPhase → AscentPhase → OrbitalPhase → ReentryPhase → SplashdownPhase
 *                                            → AbortPhase (in caso di emergenza)
 */
public interface MissionPhase {

    /** Nome della fase per display nel CLI (es. "ORBITAL"). */
    String getName();

    /** Descrizione contestuale (es. "LEO — Low Earth Orbit, 402 km"). */
    String getDescription();

    /**
     * Comandi disponibili in questa fase.
     * Il CLI usa questo per:
     * 1. Visualizzare l'help contestuale
     * 2. Validare i comandi (requirePhase verifica la fase, non i comandi)
     * <p>
     * Ogni fase ha un sottoinsieme diverso di comandi validi:
     *
     */
    String[] availableCommands();

    /**
     * Verifica se la transizione a 'next' è consentita.
     * Esempio: da OrbitalPhase si può andare a ReentryPhase o AbortPhase,
     * ma NON a LaunchPhase (la missione va avanti, non torna indietro).
     * <p>
     * Default false: le fasi terminali (Splashdown, Abort) non possono
     * transitare a nessun'altra fase — non è necessario override.
     */
    boolean canTransitionTo(MissionPhase next);

    /**
     * Messaggio narrativo mostrato quando si ENTRA in questa fase.
     */
    String onEnter();

    /**
     * Messaggio narrativo mostrato quando si ESCE da questa fase.
     */
    String onExit();
}
