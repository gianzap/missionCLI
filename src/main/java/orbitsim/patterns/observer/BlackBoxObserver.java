package orbitsim.patterns.observer;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PATTERN: Observer — Subscriber BlackBox
 * <p>
 * Registra tutti gli eventi di missione in memoria,
 * come la scatola nera di un aereo.
 * Produce un report finale strutturato al termine della missione.
 * <p>
 * RESPONSABILITÀ UNICA: accumulare eventi e stamparli su richiesta.
 * Non filtra, non agisce — solo registra.
 */
public class BlackBoxObserver implements MissionObserver {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    // Lista immutabile dall'esterno — solo BlackBox può aggiungere eventi
    private final List<MissionEvent> eventLog = new ArrayList<>();

    @Override
    public void onEvent(MissionEvent event) {
        eventLog.add(event); // registra tutto, senza filtri
    }

    @Override
    public String getName() { return "BlackBoxObserver"; }

    /**
     * Genera il report finale della missione.
     * Chiamato dal CLI al comando REENTRY o EXIT.
     * Raggruppa gli eventi per severity per una lettura rapida.
     */
    public String generateReport() {
        if (eventLog.isEmpty()) {
            return "\n  [BLACK BOX] No events recorded.\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n  ╔══════════════════════════════════════════════════╗\n");
        sb.append("  ║            HORUS-21 MISSION BLACK BOX            ║\n");
        sb.append("  ╠══════════════════════════════════════════════════╣\n");
        sb.append(String.format("  ║  Total events recorded: %-24d║\n", eventLog.size()));
        sb.append("  ╠══════════════════════════════════════════════════╣\n");

        // Conta per severity
        long emergencies = count(MissionEvent.Severity.EMERGENCY);
        long criticals   = count(MissionEvent.Severity.CRITICAL);
        long warnings    = count(MissionEvent.Severity.WARNING);
        long infos       = count(MissionEvent.Severity.INFO);

        sb.append(String.format("  ║  🚨 EMERGENCY : %-32d║\n", emergencies));
        sb.append(String.format("  ║  ⚠️  CRITICAL  : %-32d║\n", criticals));
        sb.append(String.format("  ║  ⚡ WARNING   : %-32d║\n", warnings));
        sb.append(String.format("  ║  ℹ️  INFO      : %-32d║\n", infos));
        sb.append("  ╠══════════════════════════════════════════════════╣\n");
        sb.append("  ║  EVENT LOG                                       ║\n");
        sb.append("  ╠══════════════════════════════════════════════════╣\n");

        // Stampa tutti gli eventi in ordine cronologico
        for (MissionEvent e : eventLog) {
            String time = e.timestamp().format(FMT);
            String line = String.format("[%s] %-10s | %s",
                    time, e.severity(), e.message());
            // tronca se troppo lungo per il box
            if (line.length() > 48) line = line.substring(0, 45) + "...";
            sb.append(String.format("  ║  %-48s║\n", line));
        }

        sb.append("  ╚══════════════════════════════════════════════════╝\n");
        return sb.toString();
    }

    /** Restituisce una vista non modificabile del log — usata dai test. */
    public List<MissionEvent> getEventLog() {
        return Collections.unmodifiableList(eventLog);
    }

    private long count(MissionEvent.Severity severity) {
        return eventLog.stream()
                .filter(e -> e.severity() == severity)
                .count();
    }
}