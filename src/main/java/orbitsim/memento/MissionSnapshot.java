package orbitsim.memento;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ═══════════════════════════════════════════════════════════════
 * PATTERN: Memento — il "Memento" stesso (oggetto stato catturato)
 * ═══════════════════════════════════════════════════════════════
 *
 * Ruoli nel pattern:
 *   Originator  = Spacecraft (crea gli snapshot)
 *   Memento     = MissionSnapshot (questo record — conserva lo stato)
 *   Caretaker   = SnapshotCaretaker (gestisce la lista di snapshot)
 *
 * USO NEL PROGETTO:
 *   1. PRE-anomalia:  snapshot salvato automaticamente prima della risposta
 *   2. POST-anomalia: snapshot salvato dopo la gestione
 *   3. Confronto:     "lo stato è migliorato dopo la procedura di emergenza?"
 *   4. Debriefing:    il Caretaker li mostra tutti con SNAPSHOTS + RESTORE
 *
 * PERCHÉ record Java?
 * - Immutabile per design: uno snapshot non deve cambiare dopo la cattura
 * - Java genera automaticamente: costruttore, getter, equals, hashCode, toString
 * - Semantica di "oggetto valore": due snapshot con stessi dati sono equivalenti
 * - Non può essere esteso (final implicito) — nessun rischio di subclassing
 *
 * PERCHÉ non una classe normale con setter?
 * Se qualcuno potesse modificare lo snapshot dopo la cattura, perderebbe il suo
 * scopo: essere una fotografia fedele di un momento preciso.
 */
public record MissionSnapshot(

    String id,               // identificatore univoco: "SNP-001", "SNP-002"...
    String label,            // descrizione leggibile: "PRE-REACTOR", "ORBITAL-INSERTION"
    String phaseName,        // nome della fase al momento dello snapshot: "ORBITAL"
    double altitude,         // quota in km al momento della cattura
    double velocity,         // velocità m/s al momento della cattura
    double fuelPercent,      // carburante residuo in percentuale
    double reactorTemp,      // temperatura reattore in °C
    Map<String, String> systemStatuses, // mappa ID sistema → stato (NOMINAL/CRITICAL/ecc.)
    LocalDateTime capturedAt,           // timestamp preciso della cattura
    String note              // nota descrittiva opzionale

) {

    /**
     * Compact constructor (Java 16+) — eseguito dal costruttore canonico.
     * Qui potremmo aggiungere validazioni prima che i campi vengano assegnati.
     * Esempio: se volessimo validare che altitude >= 0.
     * Nel progetto non serve — la Spacecraft garantisce valori validi.
     */

    /**
     * Genera un riassunto formattato a più righe dello snapshot.
     * Usato dal comando RESTORE del CLI.
     * String.format() con %s (stringa), %.0f (float senza decimali), %.1f (1 decimale).
     */
    public String summary() {
        return String.format(
            "  Snapshot [%s] — %s\n"     +  // ID e label
            "  Phase: %s | Alt: %.0f km | Vel: %.0f m/s\n" +  // telemetria principale
            "  Fuel: %.1f%% | Reactor: %.0f°C\n"             +  // sistemi propulsivi
            "  Captured: %s\n"            +  // timestamp
            "  Note: %s",                    // nota descrittiva
            id, label, phaseName, altitude, velocity,
            fuelPercent, reactorTemp, capturedAt, note);
    }
}
