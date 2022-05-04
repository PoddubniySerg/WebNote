package note;

import java.text.SimpleDateFormat;
import java.util.Date;

public class INoteImpl implements INote {
    private static int count = 2;

    private final int id;
    private String name;
    private String text;
    private Date dateTime;

    public INoteImpl() {
        this.id = count++;
        this.dateTime = new Date();
    }

    public INoteImpl(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getKey() {
        return String.format("%04d", this.id);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Date getDateTime() {
        return this.dateTime;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        this.dateTime = new Date();
    }

    @Override
    public void setText(String text) {
        this.text = text;
        this.dateTime = new Date();
    }

    @Override
    public String toString() {
        return "Код заметки: " + String.format("%04d", this.id) +
                "\nНазвание: " + this.name +
                "\nДата: " + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(this.dateTime) +
                "\nСодержимое:\n" + this.text;
    }
}
