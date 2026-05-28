package orbitsim;

import orbitsim.patterns.observer.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BlackBoxObserverTest {

    @Test
    void noEvents_reportContainsNoEventsMessage() {
        BlackBoxObserver bb = new BlackBoxObserver();
        assertTrue(bb.generateReport().contains("No events recorded"));
    }

    @Test
    void afterEvent_logContainsOneEntry() {
        BlackBoxObserver bb = new BlackBoxObserver();
        MissionEventBus bus = new MissionEventBus();
        bus.subscribe(bb);
        bus.publish(new MissionEvent(
                MissionEvent.EventType.ANOMALY, "TEST",
                "test event", MissionEvent.Severity.WARNING));
        assertEquals(1, bb.getEventLog().size());
    }

    @Test
    void multipleEvents_countIsCorrect() {
        BlackBoxObserver bb = new BlackBoxObserver();
        MissionEventBus bus = new MissionEventBus();
        bus.subscribe(bb);
        bus.publish(new MissionEvent(MissionEvent.EventType.PHASE_CHANGE,
                "MC", "→ ORBITAL", MissionEvent.Severity.INFO));
        bus.publish(new MissionEvent(MissionEvent.EventType.ANOMALY,
                "REACTOR", "spike", MissionEvent.Severity.EMERGENCY));
        assertEquals(2, bb.getEventLog().size());
    }

    @Test
    void report_containsAllSeverityLabels() {
        BlackBoxObserver bb = new BlackBoxObserver();
        MissionEventBus bus = new MissionEventBus();
        bus.subscribe(bb);
        bus.publish(new MissionEvent(MissionEvent.EventType.ANOMALY,
                "SYS", "fault", MissionEvent.Severity.CRITICAL));
        String report = bb.generateReport();
        assertTrue(report.contains("HORUS-21 MISSION BLACK BOX"));
        assertTrue(report.contains("CRITICAL"));
        assertTrue(report.contains("Total events recorded"));
    }
}