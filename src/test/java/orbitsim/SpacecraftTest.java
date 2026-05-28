package orbitsim;

import orbitsim.patterns.composite.SystemStatus;
import orbitsim.patterns.observer.MissionEventBus;
import orbitsim.spacecraft.Spacecraft;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SpacecraftTest {

    private Spacecraft spacecraft;

    @BeforeEach
    void setUp() {
        spacecraft = new Spacecraft(new MissionEventBus());
    }

    @Test
    void initialStatus_isNominal() {
        assertEquals(SystemStatus.NOMINAL, spacecraft.getOverallStatus());
    }

    @Test
    void setOrbitalState_updatesAltitude() {
        spacecraft.setOrbitalState();
        assertEquals(402, spacecraft.getAltitude());
    }

    @Test
    void systemIterator_returnsAllLeaves() {
        var it = spacecraft.systemIterator();
        int count = 0;
        while (it.hasNext()) { it.next(); count++; }
        // 7 subsystem foglie: Main Engine, Reactor, RCS, O2, CO2, Nav, Comms
        assertEquals(7, count);
    }

    @Test
    void degradeSystem_setsCritical() {
        spacecraft.degradeSystem("REACTOR");
        assertEquals(SystemStatus.CRITICAL, spacecraft.getOverallStatus());
    }

    @Test
    void restoreSystem_resetsToNominal() {
        spacecraft.degradeSystem("REACTOR");
        spacecraft.restoreSystem("REACTOR");
        assertEquals(SystemStatus.NOMINAL, spacecraft.getOverallStatus());
    }
}