package note.card;

import note.INote;

import java.text.SimpleDateFormat;

public class Card {

    private final String id;
    private final String name;
    private final String date;
    private String content;

    public Card(INote note) {
        this.id = String.format("%04d", note.getId());
        this.name = note.getName().length() > 25 ? note.getName().substring(0, 25) + "..." : note.getName();
        this.date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(note.getDateTime());
        if (note.getText() != null && !note.getText().isEmpty()) {
            this.content = note.getText().length() > 35 ? note.getText().substring(0, 32) + "..." : note.getText();
            if (this.content.contains("div")) {
                this.content = this.content.split("<div>")[0];
            }
        } else {
            this.content = "Пустой";
        }
    }
}