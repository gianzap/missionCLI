package orbitsim;

import orbitsim.patterns.chain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AnomalyChainTest {

    private AnomalyHandler pipeline;

    @BeforeEach
    void setUp() {
        // ricostruisce la pipeline prima di ogni test
        pipeline = new DetectionHandler();
        pipeline.setNext(new AssessmentHandler())
                .setNext(new IsolationHandler())
                .setNext(new NotificationHandler())
                .setNext(new EscalationHandler());
    }

    @Test
    void severity5_abortsMission() {
        AnomalyContext ctx = new AnomalyContext("O2 failure", "LIFE_SUPPORT_A", 5);
        pipeline.handle(ctx);
        assertTrue(ctx.isMissionAborted());
    }

    @Test
    void severity1_doesNotAbortMission() {
        AnomalyContext ctx = new AnomalyContext("Minor fault", "COMMS_ARRAY", 1);
        pipeline.handle(ctx);
        assertFalse(ctx.isMissionAborted());
    }

    @Test
    void reactorAnomaly_elevatesSeverity() {
        // AssessmentHandler deve elevare severity se il sistema è REACTOR
        AnomalyContext ctx = new AnomalyContext("Spike", "REACTOR_CORE", 4);
        pipeline.handle(ctx);
        // severity 4 + 1 = 5, capped a 5
        assertEquals(5, ctx.getSeverity());
    }

    @Test
    void severity3_marksHandled() {
        AnomalyContext ctx = new AnomalyContext("Hull loss", "HULL_SECTION_B", 3);
        pipeline.handle(ctx);
        assertTrue(ctx.isHandled());
    }

    @Test
    void actionLog_notEmpty_afterPipeline() {
        AnomalyContext ctx = new AnomalyContext("Nav fault", "NAV_COMPUTER", 2);
        pipeline.handle(ctx);
        assertFalse(ctx.getActionLog().isEmpty());
    }
}