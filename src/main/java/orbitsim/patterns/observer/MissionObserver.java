package orbitsim.patterns.observer;

/**
 * PATTERN: Observer — interfaccia Subscriber.
 *
 * Ogni classe che vuole ricevere eventi di missione implementa questa interfaccia.
 * Due metodi:
 * - onEvent(): reazione all'evento (obbligatorio)
 * - getName(): identificazione per logging e debug (obbligatorio)
 *
 * PERCHÉ è un'interfaccia e non una classe astratta?
 * Le implementazioni (ConsoleAlert, BlackBox) non condividono stato né comportamento.
 * L'interfaccia definisce solo il contratto — massima flessibilità.
 * Una classe potrebbe implementare MissionObserver E altre interfacce
 * (impossibile con classe astratta — Java single inheritance).
 *
 * NOTA: questa interfaccia ha DUE metodi astratti (onEvent + getName),
 * quindi NON è un'interfaccia funzionale → non può essere usata come lambda.
 */
public interface MissionObserver {

    /**
     * Chiamato dall'EventBus quando viene pubblicato un evento.
     * L'implementazione decide cosa fare con l'evento.
     * Non lancia eccezioni checked — se crasha, l'EventBus la cattura.
     */
    void onEvent(MissionEvent event);

    /**
     * Nome dell'observer per logging e debug.
     * Usato da EventBus nel messaggio di errore se l'observer crasha.
     */
    String getName();
}
