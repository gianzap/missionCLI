package orbitsim.mission;

public class AscentPhase implements MissionPhase{
    @Override
    public String getName() {
        return "ASCENT";
    }

    @Override
    public String getDescription() {
        return "Max-Q — Ascending to orbit";
    }

    @Override
    public String[] availableCommands() {
        return new String[]{"STATUS", "TELEMETRY", "SYSTEMS", "ABORT", "HELP"};
    }

    @Override
    public boolean canTransitionTo(MissionPhase next) {
        return next instanceof OrbitalPhase || next instanceof AbortPhase ;
    }

    @Override
    public String onEnter() {
        return """
                
                  Staging complete. Second stage ignition confirmed.
                  Altitude: 120 km | Velocity: 7,800 m/s
                  Approaching orbital insertion burn...
                """;
    }

    @Override
    public String onExit() {
        return "  MECO — Main Engine Cut-Off. Coasting to apogee.";
    }
}
