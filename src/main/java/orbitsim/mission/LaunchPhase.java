package orbitsim.mission;

public class LaunchPhase implements MissionPhase{
    @Override
    public String getName() {
        return "LAUNCH";
    }

    @Override
    public String getDescription() {
        return "T-0 — Liftoff sequence active";
    }

    @Override
    public String[] availableCommands() {
        return new String[]{"STATUS", "ABORT", "TELEMETRY", "HELP"};
    }

    @Override
    public boolean canTransitionTo(MissionPhase next) {
        return next instanceof AscentPhase;
    }

    @Override
    public String onEnter() {
        return "\n  *** IGNITION SEQUENCE START ***\n" +
                "  Main engines throttle up... 104%\n";
    }

    @Override
    public String onExit() {
        return "  Launch phase complete. Nominal trajectory confirmed.";
    }

}
