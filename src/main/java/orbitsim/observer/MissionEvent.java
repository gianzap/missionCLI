package orbitsim.observer;

import java.time.LocalDateTime;

public record MissionEvent(
    EventType type,
    String source,
    String message,
    Severity severity,
    LocalDateTime timestamp
) {
    public enum EventType {
        ANOMALY, SYSTEM_FAULT, PHASE_CHANGE,
        CREW_ALERT, TELEMETRY, MISSION_COMPLETE
    }
    public enum Severity { INFO, WARNING, CRITICAL, EMERGENCY }

    public MissionEvent(EventType t, String src, String msg, Severity sev) {
        this(t, src, msg, sev, LocalDateTime.now());
    }
}
