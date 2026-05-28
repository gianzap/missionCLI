package orbitsim.memento;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * PATTERN: Memento — il Caretaker.
 *
 * Il Caretaker conserva gli snapshot senza MAI accederne al contenuto interno.
 * Principio fondamentale del Memento: il Caretaker è "cieco" rispetto
 * allo stato che conserva — sa solo che gli snapshot esistono.
 *
 * In questo progetto il Caretaker è leggero perché MissionSnapshot è un record
 * (public) — in un Memento "puro" lo snapshot sarebbe completamente opaco.
 * La scelta è un bilanciamento tra purezza del pattern e semplicità Java.
 *
 * OPERAZIONI:
 * - save(): aggiunge snapshot alla storia
 * - getLast(): recupera l'ultimo (per confronto rapido post-anomalia)
 * - getById(): recupera per ID specifico (comando RESTORE)
 * - getAll(): tutti gli snapshot (comando SNAPSHOTS)
 * - listAll(): formato testuale per il CLI
 */
public class SnapshotCaretaker {

    // ArrayList perché vogliamo ordine di inserimento (cronologico)
    // e accesso per indice (getLast usa index size-1)
    private final List<MissionSnapshot> history = new ArrayList<>();

    /**
     * Salva un nuovo snapshot nella storia.
     * Stampa conferma a video — feedback immediato per il controllore.
     * .toLocalTime() mostra solo l'ora (senza data) — più leggibile nel CLI.
     */
    public void save(MissionSnapshot snap) {
        history.add(snap);
        // toLocalTime() estrae ore:minuti:secondi da LocalDateTime
        System.out.println("  [SNAPSHOT] Saved: " + snap.label()
                           + " @ " + snap.capturedAt().toLocalTime());
    }

    /**
     * Restituisce l'ultimo snapshot salvato.
     * Optional<T> invece di null: forza il chiamante a gestire il caso "vuoto".
     * history.size() - 1 = indice dell'ultimo elemento.
     * Se history è vuota, ritorna Optional.empty() — nessuna NPE possibile.
     */
    public Optional<MissionSnapshot> getLast() {
        if (history.isEmpty()) return Optional.empty();
        return Optional.of(history.get(history.size() - 1));
    }

    /**
     * Cerca uno snapshot per ID.
     * STREAM API:
     * - .stream()           → pipeline sulla lista
     * - .filter(s -> ...)   → mantiene solo quello con l'ID corrispondente
     * - .findFirst()        → prende il primo (o Optional.empty() se nessuno)
     *
     * s.id().equals(id) — .equals() per confronto String (mai ==)
     */
    public Optional<MissionSnapshot> getById(String id) {
        return history.stream()
            .filter(s -> s.id().equals(id)) // lambda: s è il parametro, condizione sul campo id
            .findFirst();                    // operazione terminale — prende il primo match
    }

    /**
     * Restituisce tutti gli snapshot in sola lettura.
     * Collections.unmodifiableList() = view immutabile senza copiare la lista.
     */
    public List<MissionSnapshot> getAll() {
        return Collections.unmodifiableList(history);
    }

    /**
     * Genera il testo formattato per il comando SNAPSHOTS del CLI.
     * STREAM API + Collectors.joining():
     * - .stream()   → pipeline sulla lista
     * - .map(...)   → trasforma ogni snapshot in una stringa formattata
     * - .collect(Collectors.joining("\n")) → concatena con newline tra gli elementi
     *
     * Equivalente imperativo:
     *   StringBuilder sb = new StringBuilder();
     *   for (MissionSnapshot s : history) {
     *       sb.append("  [").append(s.id()).append("] ...\n");
     *   }
     */
    public String listAll() {
        if (history.isEmpty()) return "  No snapshots saved.";
        return history.stream()
            .map(s -> String.format("  [%s] %-20s — %s",   // formatta ogni snapshot
                    s.id(), s.label(), s.capturedAt().toLocalTime()))
            .collect(Collectors.joining("\n")); // unisce con newline
    }
}
