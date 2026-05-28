package orbitsim.patterns.memento;

import orbitsim.patterns.composite.SystemStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * PATTERN: Memento — lo snapshot immutabile dello stato.
 * <p>
 * Contiene una fotografia dello stato della navicella
 * in un preciso momento. È immutabile: una volta creato
 * non può essere modificato — garantisce l'integrità storica.
 * <p>
 * Solo Spacecraft (Originator) può crearlo.
 * Solo MissionCaretaker può conservarlo e restituirlo.
 * MissionCLI non accede mai ai dati interni — vede solo il report.
 */
public class SpacecraftMemento {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    // tutti i campi final — immutabilità garantita
    private final double altitude;
    private final double velocity;
    private final double fuelPercent;
    private final double reactorTemp;
    private final SystemStatus overallStatus;
    private final String label;
    private final LocalDateTime timestamp;

    // costruttore package-private: solo Spacecraft può istanziarlo
    public SpacecraftMemento(double altitude, double velocity, double fuelPercent,
                      double reactorTemp, SystemStatus overallStatus, String label) {
        this.altitude      = altitude;
        this.velocity      = velocity;
        this.fuelPercent   = fuelPercent;
        this.reactorTemp   = reactorTemp;
        this.overallStatus = overallStatus;
        this.label         = label;
        this.timestamp     = LocalDateTime.now();
    }

    /** Report leggibile — usato dal CLI per SNAPSHOT command. */
    public String getReport() {
        return String.format(
                """
                  ── SNAPSHOT [%s] @ %s ──
                  Altitude:  %.1f km  |  Velocity: %.1f m/s
                  Fuel:      %.1f%%   |  Reactor:  %.1f°C
                  Status:    %s
                """,
                label, timestamp.format(FMT),
                altitude, velocity, fuelPercent, reactorTemp, overallStatus);
    }

    public String getLabel()     { return label; }
    public LocalDateTime getTimestamp() { return timestamp; }
}