package storage;

import note.INote;

import java.util.List;
import java.util.Set;

public interface IStorage {
    INote getNote(String noteId);

    List<INote> getNotes();

    Set<String> getNotesId();

    void inputNote(INote note);

    void shiftNote(String id, String newName, String newContent);

    boolean removeNote(INote note);
}
