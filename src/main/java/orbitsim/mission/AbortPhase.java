package orbitsim.mission;

public class AbortPhase implements MissionPhase {
    @Override
    public String getName() {
        return "ABORT";
    }

    @Override
    public String getDescription() {
        return "Launch Abort System — crew safe";
    }

    @Override
    public String[] availableCommands() {
        return new String[]{"STATUS","REPORT", "HELP"};
    }

    @Override
    public boolean canTransitionTo(MissionPhase next) {
        return false;
    }

    @Override
    public String onEnter() {
        return """
                
                  !!! ABORT ABORT ABORT !!!
                  Launch Abort System activated.
                  Crew capsule separation confirmed.
                  Parachutes nominal. Crew safe.
                """;
    }

    @Override
    public String onExit() {
        return "";
    }
}
