
package orbitsim.spacecraft;


import orbitsim.patterns.composite.SpacecraftComponent;
import orbitsim.patterns.composite.SpacecraftModule;
import orbitsim.patterns.composite.Subsystem;
import orbitsim.patterns.composite.SystemStatus;
import orbitsim.patterns.observer.MissionEvent;
import orbitsim.patterns.observer.MissionEventBus;

import java.util.*;

/**
 * Navicella spaziale HORUS-21
 * gestisce lo stato fisico e i moduli compositi
 * Z G 2026 Epicode
 */

public class Spacecraft {

    public final MissionEventBus eventBus;
    private double altitude = 0;
    private double velocity = 0;
    private double fuelPercent = 100.0;
    private double reactorTemp = 280.0;
    private int snapshotCounter = 1;


    // Composite root
    private final SpacecraftModule root;

    private final Subsystem lifeSupport;
    private final Subsystem reactor;


    public Spacecraft(MissionEventBus eventBus) {
        this.eventBus = eventBus;

        // Costruzione gerarchia Composite
        root = new SpacecraftModule("ROOT", "HORUS-21 Systems");

        SpacecraftModule propModule = new SpacecraftModule("PROP", "Propulsion");
        // Subsystems (foglie) — accesso diretto per simulazione
        Subsystem propulsion = new Subsystem("MAIN_ENG", "Main Engine", "%thrust");
        reactor    = new Subsystem("REACTOR",  "Reactor Core",   "°C");
        Subsystem rcs = new Subsystem("RCS", "RCS Thrusters", "%");
        reactor.setValue(280); rcs.setValue(100);
        propModule.add(propulsion); propModule.add(reactor); propModule.add(rcs);

        SpacecraftModule lifeModule = new SpacecraftModule("LIFE", "Life Support");
        lifeSupport = new Subsystem("O2_SYS",  "O2 Recycler",    "%");
        Subsystem co2 = new Subsystem("CO2",   "CO2 Scrubber",   "%");
        lifeSupport.setValue(100); co2.setValue(98);
        lifeModule.add(lifeSupport); lifeModule.add(co2);

        SpacecraftModule avionics = new SpacecraftModule("AVIONICS", "Avionics");
        Subsystem navigation = new Subsystem("NAV", "Navigation Comp", "accuracy%");
        Subsystem comms = new Subsystem("COMMS", "Comm Array", "dBm");
        navigation.setValue(99.8); comms.setValue(-85);
        avionics.add(navigation); avionics.add(comms);

        root.add(propModule);
        root.add(lifeModule);
        root.add(avionics);

    }
    /** Aggiorna telemetria simulata a ogni ciclo. */
    public void tick() {
        // Deriva naturale valori
        fuelPercent = Math.max(0, fuelPercent - 0.1);
        reactorTemp = 280 + (Math.random() * 10 - 5);
        reactor.setValue(reactorTemp);
    }

    /** TELEMETRY command output. */
    public String getTelemetry() {
        return String.format(
                """
                        
                          ╔══════════════════ TELEMETRY ════════════════════╗
                          ║  Altitude:     %6.1f km                        ║
                          ║  Velocity:     %6.1f m/s                       ║
                          ║  Fuel:         %6.1f%%                          ║
                          ║  Reactor:      %6.1f°C                         ║
                          ║  Life Support: %-32s║
                          ╚═════════════════════════════════════════════════╝
                        """,
                altitude, velocity, fuelPercent, reactorTemp, lifeSupport.getStatus());
    }
    /** SYSTEMS command output — Composite in azione. */
    public String getSystemsReport() {
        return "\n  ── SYSTEMS REPORT ──\n" + root.getStatusReport();
    }

    /** Iterator pattern: scansiona tutti i subsystem. */
    public Iterator<SpacecraftComponent> systemIterator() {
        List<SpacecraftComponent> all = new ArrayList<>();
        collectLeaves(root, all);
        return all.iterator();
    }

    private void collectLeaves(SpacecraftComponent c, List<SpacecraftComponent> acc) {
        if (c.isLeaf()) acc.add(c);
        else c.getChildren().forEach(child -> collectLeaves(child, acc));
    }


    private String getCurrentPhaseName() { return "ORBITAL"; }

    // Metodi per simulazione anomalie
    public void degradeSystem(String systemId) {
        findSubsystem(systemId).ifPresent(s -> {
            s.setStatus(SystemStatus.CRITICAL);
            eventBus.publish(new MissionEvent(
                    MissionEvent.EventType.SYSTEM_FAULT, systemId,
                    systemId + " status: CRITICAL", MissionEvent.Severity.CRITICAL));
        });
    }

    public void restoreSystem(String systemId) {
        findSubsystem(systemId).ifPresent(s -> s.setStatus(SystemStatus.NOMINAL));
    }

    private Optional<Subsystem> findSubsystem(String id) {
        Iterator<SpacecraftComponent> it = systemIterator();
        while (it.hasNext()) {
            SpacecraftComponent c = it.next();
            if (c.getId().equals(id) && c instanceof Subsystem s) return Optional.of(s);
        }
        return Optional.empty();
    }

    // Phase transition helpers
    public void setLaunchState()  { altitude = 0;   velocity = 0;     fuelPercent = 100; }
    public void setAscentState()  { altitude = 120;  velocity = 7800;  fuelPercent = 82;  }
    public void setOrbitalState() { altitude = 402;  velocity = 7660;  fuelPercent = 68;  }
    public void setReentryState() { altitude = 122;  velocity = 7900;  fuelPercent = 42;  }

    public SystemStatus getOverallStatus() { return root.getStatus(); }
    public double getFuelPercent()   { return fuelPercent; }
    public double getAltitude()      { return altitude; }
    public double getVelocity()      { return velocity; }
    public SpacecraftModule getRoot(){ return root; }
    public String getName()          { //dichiarazione variabili
        return "HORUS-21"; }


    public int getSnapshotCounter() {
        return snapshotCounter;
    }

    public void setSnapshotCounter(int snapshotCounter) {
        this.snapshotCounter = snapshotCounter;
    }
}
