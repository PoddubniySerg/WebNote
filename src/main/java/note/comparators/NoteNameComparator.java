package note.comparators;

import note.INote;

import java.util.Comparator;

public class NoteNameComparator implements Comparator<INote> {
    @Override
    public int compare(INote o1, INote o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
