package orbitsim.patterns.observer;

/**
 * Subscriber: stampa alert a video con formattazione narrativa.
 * I messaggi EMERGENCY hanno bordi !!!
 */
public class ConsoleAlertObserver implements MissionObserver {
    @Override
    public void onEvent(MissionEvent event) {
        if (event.severity() == MissionEvent.Severity.INFO) return;
        String prefix = switch (event.severity()) {
            case WARNING   -> "  [!] ";
            case CRITICAL  -> "  [!!] CRITICAL — ";
            case EMERGENCY -> "\n  !!! EMERGENCY !!! ";
            default        -> "  ";
        };
        System.out.println(prefix + event.message());
    }
    @Override public String getName() { return "ConsoleAlert"; }
}
