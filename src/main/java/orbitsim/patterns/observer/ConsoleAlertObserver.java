package orbitsim.patterns.observer;

public class ConsoleAlertObserver implements MissionObserver {

    @Override
    public void onEvent(MissionEvent event) {
        String prefix = switch (event.severity()) {
            case EMERGENCY -> "  🚨 [EMERGENCY]";
            case CRITICAL  -> "  ⚠️  [CRITICAL]";
            case WARNING   -> "  ⚡ [WARNING]";
            default        -> "  ℹ️  [INFO]";
        };
        System.out.println(prefix + " " + event.source() + " — " + event.message());
    }

    @Override
    public String getName() { return "ConsoleAlertObserver"; }
}