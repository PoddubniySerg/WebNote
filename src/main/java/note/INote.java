package note;

import java.util.Date;

public interface INote {
    int getId();

    String getKey();

    String getName();

    Date getDateTime();

    String getText();

    void setName(String name);

    void setText(String text);
}
