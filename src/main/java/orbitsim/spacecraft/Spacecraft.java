package orbitsim.spacecraft;


import orbitsim.composite.SubSystem;
import orbitsim.events.MissionEventBus;
import orbitsim.patterns.composite.SpacecraftModule;

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

    //composite root
    private final SpacecraftModule root;

    //subsystems - accesso diretto per simulazione
    private final SubSystem propulsion, lifeSupport, navigation, comms, reactor, rcs;

    private final MissionEventBus eventBus;

    public Spacecraft(SubSystem propulsion, SubSystem lifeSupport, SubSystem navigation, SubSystem comms, SubSystem reactor, SubSystem rcs, MissionEventBus eventBus){
        this.propulsion = propulsion; //PROPULSORE
        this.lifeSupport = lifeSupport; //SUPPORTI VITALI
        this.navigation = navigation;  //SISTEMA DI NAVIGAZIONE
        this.comms = comms;  //SISTEMI DI COMUNICAZIONE
        this.reactor = reactor; //REATTORE
        this.rcs = rcs; // REACTION CONTROL SYSTEMS
        this.eventBus = eventBus; //BUS EVENTI

        //costruzione gerarchia Composite
        root = new SpacecraftModule("ROOT", "HORUS-21 systems");
    }


}
