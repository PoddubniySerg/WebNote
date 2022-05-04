package note.comparators;

import note.INote;

import java.util.Comparator;

public class NoteDateComparator implements Comparator<INote> {
    @Override
    public int compare(INote o1, INote o2) {
        return o1.getDateTime().compareTo(o2.getDateTime());
    }
}
