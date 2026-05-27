package orbitsim.mission;

public class SplashdownPhase implements MissionPhase{
    @Override
    public String getName() {
        return "SPLASHDOWN";
    }

    @Override
    public String getDescription() {
        return "Mission complete — recovery ops";
    }

    @Override
    public String[] availableCommands() {
        return new String[]{"STATUS", "REPORT", "HELP"};
    }

    @Override
    public boolean canTransitionTo(MissionPhase next) {
        return false;
    }

    @Override
    public String onEnter() {
        return "\n  *** SPLASHDOWN CONFIRMED ***\n" +
                "  Recovery team en route.\n" +
                "  ARES-VII mission: SUCCESS\n" +
                "  Mission elapsed time: see REPORT\n";
    }

    @Override
    public String onExit() {
        return "";
    }
}
