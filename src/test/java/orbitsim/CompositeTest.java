package orbitsim;

import orbitsim.patterns.composite.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompositeTest {

    @Test
    void module_nominalWhenAllChildrenNominal() {
        SpacecraftModule mod = new SpacecraftModule("TEST", "Test Module");
        mod.add(new Subsystem("S1", "System 1", "%"));
        mod.add(new Subsystem("S2", "System 2", "%"));
        assertEquals(SystemStatus.NOMINAL, mod.getStatus());
    }

    @Test
    void module_propagatesCriticalFromChild() {
        SpacecraftModule mod = new SpacecraftModule("TEST", "Test Module");
        Subsystem s = new Subsystem("S1", "System 1", "%");
        s.setStatus(SystemStatus.CRITICAL);
        mod.add(s);
        // il modulo deve riportare lo stato peggiore del figlio
        assertEquals(SystemStatus.CRITICAL, mod.getStatus());
    }

    @Test
    void subsystem_isLeaf() {
        Subsystem s = new Subsystem("S1", "System 1", "%");
        assertTrue(s.isLeaf());
    }

    @Test
    void module_isNotLeaf() {
        SpacecraftModule mod = new SpacecraftModule("M", "Module");
        mod.add(new Subsystem("S1", "System 1", "%"));
        assertFalse(mod.isLeaf());
    }

    @Test
    void subsystem_setStatus_updatesCorrectly() {
        Subsystem s = new Subsystem("S1", "System 1", "%");
        s.setStatus(SystemStatus.DEGRADED);
        assertEquals(SystemStatus.DEGRADED, s.getStatus());
    }
}