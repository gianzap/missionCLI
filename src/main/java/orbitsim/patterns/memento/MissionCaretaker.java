package orbitsim.patterns.memento;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * PATTERN: Memento — il Caretaker.
 * <p>
 * Conserva la lista degli snapshot senza conoscerne
 * il contenuto interno (rispetta l'incapsulamento).
 * Fornisce accesso all'ultimo snapshot (undo-style)
 * e alla lista completa per il report finale.
 */
public class MissionCaretaker {

    // Deque: struttura LIFO — l'ultimo salvato è il primo recuperato
    // Scelta rispetto a Stack: ArrayDeque è più moderna e non sincronizzata
    private final Deque<SpacecraftMemento> history = new ArrayDeque<>();

    /** Salva uno snapshot. */
    public void save(SpacecraftMemento memento) {
        history.push(memento);
    }

    /**
     * Recupera l'ultimo snapshot salvato.
     * Restituisce null se non ci sono snapshot — il CLI gestisce questo caso.
     */
    public SpacecraftMemento getLast() {
        return history.isEmpty() ? null : history.peek();
    }

    /** Lista completa in ordine cronologico — per il report finale. */
    public List<SpacecraftMemento> getAll() {
        List<SpacecraftMemento> list = new ArrayList<>(history);
        Collections.reverse(list); // più recente alla fine
        return Collections.unmodifiableList(list);
    }

    public boolean isEmpty() { return history.isEmpty(); }

    public int size() { return history.size(); }
}