package orbitsim.patterns.chain;

/**
 * Handler 2/5 della pipeline — ASSESSMENT.
 *
 * Responsabilità unica: valutare l'impatto dell'anomalia
 * sui sistemi correlati e aggiornare la severity se necessario.
 *
 * REACTOR e LIFE_SUPPORT sono sistemi critici per la sopravvivenza —
 * un guasto su di essi è automaticamente più grave di quanto
 * la severity iniziale suggerisca.
 */
public class AssessmentHandler extends AnomalyHandler {

    @Override
    protected void process(AnomalyContext ctx) {
        System.out.println("  [CHAIN 2/5] ASSESSMENT");

        // Controlla se il sistema sorgente è critico per la missione
        if (ctx.getSourceSystem().contains("REACTOR") ||
            ctx.getSourceSystem().contains("LIFE_SUPPORT")) {

            // Math.min(5, ...) garantisce che severity non superi il massimo di 5.
            // Esempio: severity 4 + 1 = 5 (massimo), non 6 (fuori range).
            int nuovaSeverity = Math.min(5, ctx.getSeverity() + 1);
            ctx.setSeverity(nuovaSeverity); // modifica il contesto condiviso
            ctx.logAction("Critical system involved — severity elevated to " + nuovaSeverity);
        } else {
            // Anomalia contenuta al sistema sorgente — nessuna elevazione
            ctx.logAction("Impact assessment: contained to " + ctx.getSourceSystem());
        }
    }

    @Override public String getHandlerName() { return "Assessment"; }
}
