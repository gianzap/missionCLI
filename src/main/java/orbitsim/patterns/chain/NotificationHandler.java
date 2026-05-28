package orbitsim.patterns.chain;

/**
 * Handler 4/5 della pipeline — NOTIFICATION.
 * <p>
 * Responsabilità unica: notificare le parti interessate.
 * Mission Control riceve sempre il report.
 * Il crew viene allertato solo se severity >= 4 (critica).
 * <p>
 * Nota: questo handler usa il sistema di notifica del CLI (System.out).
 * In produzione invierebbe via rete a Mission Control reale.
 */
public class NotificationHandler extends AnomalyHandler {

    @Override
    protected void process(AnomalyContext ctx) {
        System.out.println("  [CHAIN 4/5] NOTIFICATION");

        // Mission Control riceve sempre il report anomalia via telemetria
        ctx.logAction("Mission Control notified — anomaly report transmitted");

        // Severity >= 4: situazione critica, il crew deve essere informato
        if (ctx.getSeverity() >= 4) {
            ctx.logAction("CREW ALERT issued — all hands to emergency stations");
        }
    }

    @Override public String getHandlerName() { return "Notification"; }
}
