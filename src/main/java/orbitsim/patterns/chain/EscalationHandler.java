package orbitsim.patterns.chain;

/**
 * Handler 5/5 della pipeline — ESCALATION.
 * <p>
 * Responsabilità unica: decidere se la situazione
 * richiede l' abort della missione.
 * <p>
 * Severity 5 = impossibile continuare in sicurezza.
 * È l'unico handler che può chiamare ctx.abortMission().
 * Una volta abortita, AnomalyHandler.handle() non passa
 * ad altri handler (controllo in AnomalyHandler.handle()).
 */
public class EscalationHandler extends AnomalyHandler {

    @Override
    protected void process(AnomalyContext ctx) {
        System.out.println("  [CHAIN 5/5] ESCALATION");

        if (ctx.getSeverity() == 5) {
            // Severity massima — non c'è possibilità di continuare
            ctx.logAction("SEVERITY 5 — Mission abort protocol activated");
            ctx.abortMission(); // setta il flag: il CLI gestirà la transizione ad AbortPhase
        } else {
            // Missione può continuare, sotto monitoraggio aumentato
            ctx.logAction("Severity " + ctx.getSeverity() +
                          "/5 — Mission continues under enhanced monitoring");
        }
    }

    @Override public String getHandlerName() { return "Escalation"; }
}
