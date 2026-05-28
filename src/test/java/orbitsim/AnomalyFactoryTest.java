package orbitsim;

import orbitsim.exception.OrbitSimException;
import orbitsim.patterns.chain.AnomalyContext;
import orbitsim.patterns.factory.AnomalyFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AnomalyFactoryTest {

    @Test
    void createReactor_returnsCorrectContext() throws OrbitSimException {
        AnomalyContext ctx = AnomalyFactory.create("REACTOR");
        assertEquals("Reactor temperature spike", ctx.getAnomalyType());
        assertEquals("REACTOR_CORE", ctx.getSourceSystem());
        assertEquals(4, ctx.getSeverity());
    }

    @Test
    void createLifeSupport_returnsMaxSeverity() throws OrbitSimException {
        AnomalyContext ctx = AnomalyFactory.create("LIFE_SUPPORT");
        assertEquals(5, ctx.getSeverity());
    }

    @Test
    void createLowercase_normalizedCorrectly() throws OrbitSimException {
        // input sanitization — "reactor" deve funzionare come "REACTOR"
        AnomalyContext ctx = AnomalyFactory.create("reactor");
        assertNotNull(ctx);
    }

    @Test
    void createUnknownType_throwsOrbitSimException() {
        // Exception Shielding — tipo sconosciuto non deve accadere crash dell' app
        assertThrows(OrbitSimException.class, () -> AnomalyFactory.create("UNKNOWN"));
    }

    @Test
    void createBlankType_throwsOrbitSimException() {
        assertThrows(OrbitSimException.class, () -> AnomalyFactory.create(""));
    }
}