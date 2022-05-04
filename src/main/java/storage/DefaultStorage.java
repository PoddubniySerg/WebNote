package storage;

import note.INote;

import java.util.*;

public class DefaultStorage implements IStorage {

    protected Map<String, INote> notes;

    public DefaultStorage() {
        this.notes = new HashMap<>();
    }

    @Override
    public INote getNote(String noteId) {
        return this.notes.isEmpty() ? null : this.notes.get(noteId);
    }

    @Override
    public List<INote> getNotes() {
        return this.notes.isEmpty() ? new ArrayList<>() : new ArrayList<>(this.notes.values());
    }

    @Override
    public Set<String> getNotesId() {
        return this.notes.keySet();
    }

    @Override
    public void inputNote(INote note) {
        this.notes.put(note.getKey(), note);
    }

    @Override
    public void shiftNote(String id, String newName, String newContent) {
        if (this.notes.containsKey(id)) {
            this.notes.get(id).setName(newName);
            this.notes.get(id).setText(newContent);
        }
    }

    @Override
    public boolean removeNote(INote note) {
        if (!this.notes.isEmpty() && this.getNotesId().contains(note.getKey())) {
            this.notes.remove(note.getKey());
            return true;
        }
        return false;
    }
}
