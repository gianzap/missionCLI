package orbitsim.spacecraft;


import orbitsim.composite.SubSystem;
import orbitsim.observer.MissionEventBus;


/**
 * Navicella spaziale HORUS-21
 * gestisce lo stato fisico e i moduli compositi
 * Z G 2026 epicode
 */

public class Spacecraft {

    //dichiarazione variabili
    private final String name = "HORUS-21";
    private double altitude = 0;
    private double velocity = 0;
    private double fuelPercent = 100.0;
    private double reactorTemp = 280.0;
    private int snapshotCounter = 1;


    public Spacecraft(MissionEventBus eventBus) {
    }


    public void setLaunchState() {
    }
}
