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

        int WIDTH = 72; // larghezza interna del box
        String border    = "  ╔" + "═".repeat(WIDTH) + "╗\n";
        String separator = "  ╠" + "═".repeat(WIDTH) + "╣\n";
        String footer    = "  ╚" + "═".repeat(WIDTH) + "╝\n";

        StringBuilder sb = new StringBuilder();
        sb.append(border);
        sb.append(center("HORUS-21 MISSION BLACK BOX", WIDTH)).append("\n");
        sb.append(separator);
        sb.append(row("Total events recorded: " + eventLog.size(), WIDTH));
        sb.append(separator);

        long emergencies = count(MissionEvent.Severity.EMERGENCY);
        long criticals   = count(MissionEvent.Severity.CRITICAL);
        long warnings    = count(MissionEvent.Severity.WARNING);
        long infos       = count(MissionEvent.Severity.INFO);

        sb.append(row("🚨 EMERGENCY : " + emergencies, WIDTH));
        sb.append(row("⚠️  CRITICAL  : " + criticals,  WIDTH));
        sb.append(row("⚡ WARNING   : " + warnings,    WIDTH));
        sb.append(row("ℹ️  INFO      : " + infos,       WIDTH));
        sb.append(separator);
        sb.append(row("EVENT LOG", WIDTH));
        sb.append(separator);

        for (MissionEvent e : eventLog) {
            String time = e.timestamp().format(FMT);
            String line = String.format("[%s] %-10s | %s", time, e.severity(), e.message());
            // se supera la larghezza va a capo invece di troncare
            while (line.length() > WIDTH - 2) {
                sb.append(row(line.substring(0, WIDTH - 2), WIDTH));
                line = "  " + line.substring(WIDTH - 2); // indenta il proseguimento
            }
            sb.append(row(line, WIDTH));
        }

        sb.append(footer);
        return sb.toString();
    }

    // riga con padding a destra fino a WIDTH
    private String row(String content, int width) {
        return String.format("  ║  %-" + (width - 2) + "s║\n", content);
    }

    // testo centrato nella riga
    private String center(String text, int width) {
        int pad = (width - text.length()) / 2;
        return "  ║" + " ".repeat(pad) + text + " ".repeat(width - pad - text.length()) + "║";
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