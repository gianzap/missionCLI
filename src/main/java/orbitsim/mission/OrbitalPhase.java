package orbitsim.mission;

public class OrbitalPhase implements MissionPhase{
    @Override
    public String getName() {
        return "ORBITAL";
    }

    @Override
    public String getDescription() {
        return "LEO — Low Earth Orbit, 402 km";
    }

    @Override
    public String[] availableCommands() {
        return new String[]{"STATUS", "TELEMETRY", "SYSTEMS", "MANEUVER",
                "SNAPSHOT", "SCAN", "INJECT_ANOMALY", "REENTRY", "HELP"};
    }

    @Override
    public boolean canTransitionTo(MissionPhase next) {
        return next instanceof ReentryPhase || next instanceof AbortPhase;
    }

    @Override
    public String onEnter() {
        return "\n  *** ORBITAL INSERTION CONFIRMED ***\n" +
                "  Altitude: 402 km | Period: 92.5 min\n" +
                "  All systems nominal. Mission phase: ORBITAL OPS\n";
    }

    @Override
    public String onExit() {
        return "  Deorbit burn sequence initiated.";
    }
}
