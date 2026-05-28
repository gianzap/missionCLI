package orbitsim.patterns.chain;

/**
 * Handler 3/5 della pipeline — ISOLATION.
 * <p>
 * Responsabilità unica: isolare il sistema guasto
 * per evitare che il guasto si propaghi ad altri sistemi.
 * <p>
 * Solo severity >= 3 richiede isolamento fisico.
 * Severity 1-2 = monitoraggio, nessuna azione invasiva.
 */
public class IsolationHandler extends AnomalyHandler {

    @Override
    protected void process(AnomalyContext ctx) {
        System.out.println("  [CHAIN 3/5] ISOLATION");

        if (ctx.getSeverity() >= 3) {
            // Isola il sistema: disconnesso dal bus principale
            ctx.logAction("Isolating " + ctx.getSourceSystem() + " from main bus");
            ctx.logAction("Backup system engaged"); // attiva il sistema di riserva

            // Marca il contesto come "gestito" — il problema è stato contenuto
            // (ma non risolto — la notifica e l'escalation seguono)
            ctx.markHandled();
        } else {
            // Severity bassa: solo monitoraggio, nessun isolamento
            ctx.logAction("Severity " + ctx.getSeverity() + "/5 — isolation not required");
        }
    }

    @Override public String getHandlerName() { return "Isolation"; }
}
