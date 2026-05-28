package orbitsim.patterns.chain;

/**
 * ═══════════════════════════════════════════════════════════════
 * PATTERN: Chain of Responsibility — Handler astratto
 * ═══════════════════════════════════════════════════════════════
 * Definisce la struttura della catena. Ogni handler concreto
 * si occupa di UNA sola responsabilità (Single Responsibility Principle).
 * <p>
 * Pipeline usata nel progetto:
 *   DetectionHandler
 *     → AssessmentHandler
 *       → IsolationHandler
 *         → NotificationHandler
 *           → EscalationHandler
 * <p>
 * Vantaggi vs if/else o switch:
 * - Aggiungere un handler non modifica gli esistenti (Open/Closed)
 * - Ogni handler è testabile indipendentemente
 * - L'ordine è configurabile a runtime
 */
public abstract class AnomalyHandler {

    // Riferimento al prossimo handler nella catena.
    // Null se questo è l'ultimo della pipeline.
    protected AnomalyHandler next;

    /**
     * Imposta il successore nella catena e lo restituisce.
     * Restituire 'next' anziché 'this' abilita la fluent interface:
     *   a.setNext(b).setNext(c).setNext(d)
     * Senza questo, servirebbe:
     *   a.setNext(b); b.setNext(c); c.setNext(d);  // verboso
     */
    public AnomalyHandler setNext(AnomalyHandler h) {
        this.next = h;
        return h; // ritorna h (non this) per permettere il chaining fluente
    }

    /**
     * TEMPLATE METHOD PATTERN — questo metodo è final.
     * <p>
     * 'Final' significa che NESSUNA sottoclasse può sovrascriverlo.
     * Questo è essenziale: la struttura della catena (chiama process,
     * poi passa al successivo) NON deve cambiare tra gli handler.
     * Solo il CONTENUTO (process) varia — è il "hook" del Template Method.
     * <p>
     * Flusso garantito per ogni handler:
     *   1. Esegui la logica di questo handler (process)
     *   2. Se non abortita la missione, passa al successivo
     */
    public final void handle(AnomalyContext ctx) {
        process(ctx);                                        // hook: implementato dalle sottoclassi
        if (next != null && !ctx.isMissionAborted()) {       // condizione di stop: missione abortita
            next.handle(ctx);                                // ricorsione: passa il contesto al prossimo
        }
    }

    /**
     * Il "hook" del Template Method.
     * Abstract = nessuna implementazione qui, OBBLIGATORIA nelle sottoclassi.
     * Ogni sottoclasse implementa solo la propria responsabilità.
     */
    protected abstract void process(AnomalyContext ctx);

    /** Nome dell' handler per logging e debug. */
    public abstract String getHandlerName();
}
