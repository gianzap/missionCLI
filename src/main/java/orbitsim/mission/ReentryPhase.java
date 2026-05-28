package orbitsim.mission;

public class ReentryPhase implements MissionPhase{

    @Override
    public String getName() {
        return "REENTRY";
    }

    @Override
    public String getDescription() {
        return "Reentry interface — 122 km";
    }

    @Override
    public String[] availableCommands() {
        return new String[]{"STATUS", "TELEMETRY", "ABORT", "HELP"};
    }

    @Override
    public boolean canTransitionTo(MissionPhase next) {
        return next instanceof  SplashdownPhase || next instanceof AbortPhase;
    }

    @Override
    public String onEnter() {
        return """
                
                  *** REENTRY INTERFACE ***
                  Blackout comm window: 4 minutes
                  Heat shield temp: 1,650°C — nominal
                  ................ [SIGNAL LOST] ................
                """;
    }

    @Override
    public String onExit() {
        return "  Comm restored. Chutes deployed. Splashdown in 8 minutes.";
    }
}
