package orbitsim.patterns.chain;

/**
 * Handler 1/5 della pipeline — DETECTION.
 * <p>
 * Responsabilità unica (Single Responsibility):
 * rilevare e classificare l'anomalia.
 * <p>
 * Non isola, non notifica, non abort —
 * fa solo il rilevamento. Tutto il resto è degli handler successivi.
 */
public class DetectionHandler extends AnomalyHandler {

    /**
     * Implementa il hook astratto di AnomalyHandler.
     * @ Override segnala al compilatore che stiamo sovrascrivendo
     * un metodo della superclass — errore di compilazione se il nome è sbagliato.
     */
    @Override
    protected void process(AnomalyContext ctx) {
        System.out.println("\n  [CHAIN 1/5] DETECTION"); // output narrativo per la demo

        // logAction: aggiunge al registro E stampa a video — doppio effetto
        ctx.logAction("Anomaly detected: " + ctx.getAnomalyType()
                      + " on " + ctx.getSourceSystem());

        // Registra la severity iniziale (potrebbe essere elevata dagli handler successivi)
        ctx.logAction("Initial severity assessment: " + ctx.getSeverity() + "/5");
    }

    /** Nome leggibile per logging e debug. */
    @Override public String getHandlerName() { return "Detection"; }
}
