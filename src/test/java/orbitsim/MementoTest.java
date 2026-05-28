package orbitsim;

import orbitsim.patterns.memento.MissionCaretaker;
import orbitsim.patterns.memento.SpacecraftMemento;
import orbitsim.patterns.observer.MissionEventBus;
import orbitsim.spacecraft.Spacecraft;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MementoTest {

    @Test
    void saveMemento_capturesCurrentState() {
        Spacecraft sc = new Spacecraft(new MissionEventBus());
        sc.setOrbitalState();
        SpacecraftMemento m = sc.saveMemento("TEST");
        assertNotNull(m);
        assertEquals("TEST", m.getLabel());
        assertTrue(m.getReport().contains("402")); // altitudine orbitale
    }

    @Test
    void caretaker_savesAndRetrievesLast() {
        Spacecraft sc = new Spacecraft(new MissionEventBus());
        MissionCaretaker ct = new MissionCaretaker();
        sc.setOrbitalState();
        ct.save(sc.saveMemento("SNAP-1"));
        sc.setReentryState();
        ct.save(sc.saveMemento("SNAP-2"));
        assertEquals("SNAP-2", ct.getLast().getLabel());
    }

    @Test
    void caretaker_getAll_chronologicalOrder() {
        Spacecraft sc = new Spacecraft(new MissionEventBus());
        MissionCaretaker ct = new MissionCaretaker();
        ct.save(sc.saveMemento("FIRST"));
        ct.save(sc.saveMemento("SECOND"));
        var all = ct.getAll();
        assertEquals("FIRST",  all.get(0).getLabel());
        assertEquals("SECOND", all.get(1).getLabel());
    }

    @Test
    void caretaker_emptyByDefault() {
        MissionCaretaker ct = new MissionCaretaker();
        assertTrue(ct.isEmpty());
        assertNull(ct.getLast());
    }
}