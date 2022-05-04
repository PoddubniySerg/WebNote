package app;

import note.INote;
import note.INoteImpl;
import note.comparators.NoteDateComparator;
import note.comparators.NoteNameComparator;
import storage.IStorage;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ConsoleApp implements IApp {
    private String printCommands;
    Scanner scanner;
    IStorage storage;

    List<INote> showList;

    public ConsoleApp() {
        this.scanner = new Scanner(System.in);
        this.setCommands();
    }

    private void setCommands() {
        this.printCommands = "Введите 'q' для выхода из программы или выберите действие:\n" +
                "a) - создать заметку\n" +
                "v) - показать список заметок\n" +
                "o) - открыть заметку\n" +
                "s) - сортировка\n" +
                "f) - найти\n" +
                "d) - удалить заметку\n";
    }

    private String userDialog(String print) {
        System.out.print(print + ": ");
        return scanner.nextLine();
    }

    private void newNote() {
        INote note = new INoteImpl();
        String input = this.userDialog("Введите название заметки (до 20 символов)");
        note.setName(input == null || input.isEmpty() || input.split(" ").length == 0 ? "Без названия" : input);
        input = this.userDialog("Введите текст заметки");
        note.setText(input == null || input.isEmpty() ? "Пустая заметка" : input);
        this.storage.inputNote(note);
        this.showList = this.storage.getNotes();
        System.out.println("Запись добавлена");
    }

    private void showNotes(List<INote> noteList) {
        System.out.println("Список заметок:");
        System.out.printf("%20s\t|", "Название заметки");
        System.out.printf("%12s\t|", "Код заметки");
        System.out.printf("%15s\t\t\t|", "Дата");
        System.out.printf("%25s", "Содержимое");
        System.out.println();
        if (!noteList.isEmpty()) {
            for (INote note : noteList) {
                System.out.printf("%20s", note.getName().length() > 20 ? note.getName().substring(0, 20) : note.getName());
                System.out.printf("%14s", note.getId());
                System.out.printf("%30s\t\t", new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(note.getDateTime()));
                System.out.printf("%1s",
                        note.getText().length() > 60 ?
                                "\"" + note.getText().substring(0, 60) + "...\"" : "\"" + note.getText() + "\"");
                System.out.println();
            }
        }
        System.out.println();
    }

    private void openNote() {
        String actions = "\nn) - изменить название\n" +
                "t) - изменить содержимое\n" +
                "чтобы вернуться в предыдущее меню, введите любое иное значение";
        String input = this.userDialog("Введите код заметки");
        INote note = this.storage.getNotesId().contains(input) ? this.storage.getNote(input) : null;
        if (note == null) {
            System.out.println("Заметка с выбранным кодом не найдена\n");
            return;
        }
        System.out.println(note);
        switch (this.userDialog(actions)) {
            case "n":
                input = this.userDialog("Введите новое название");
                note.setName(input == null || input.isEmpty() || input.split(" ").length == 0 ?
                        "Без названия" : input);
                break;
            case "t":
                input = this.userDialog("Введите новый текст");
                note.setText(input == null || input.isEmpty() ? "Пустая заметка" : input);
                break;
            default:
                return;
        }
        System.out.println("Запись изменена");
    }

    private void removeNote() {
        String input = this.userDialog("Введите код заметки для удаления");
        System.out.println(this.storage.removeNote(this.storage.getNote(input)) ? "Запись удалена" : "Запись не найдена");
        this.showList = this.storage.getNotes();
    }

    private void sortedNotes() {
        String sortType = "ni) - по возрастанию имён\n" +
                "nn) - по убыванию имён\n" +
                "di) - по возрастанию даты\n" +
                "dd) - по убыванию даты\n";
        switch (this.userDialog(sortType)) {
            case "ni":
                this.showList = this.showList.stream().sorted(new NoteNameComparator()).collect(Collectors.toList());
                break;
            case "nn":
                this.showList = this.showList.stream()
                        .sorted(new NoteNameComparator())
                        .sorted(Collections.reverseOrder())
                        .collect(Collectors.toList());
                break;
            case "di":
                this.showList = this.showList.stream().sorted(new NoteDateComparator()).collect(Collectors.toList());
                break;
            case "dd":
                this.showList = this.showList.stream()
                        .sorted(new NoteDateComparator())
                        .sorted(Collections.reverseOrder())
                        .collect(Collectors.toList());
                break;
            default:
                System.out.println("Команда не опознана\n");
                break;
        }
        this.showNotes(this.showList);
    }

    private void findByText() {
        String[] input = this.userDialog("Введите ключевые слова для поиска через пробел").split(" ");
        if (input.length == 0) {
            System.out.println("ничего не введено");
            return;
        }
        this.showList = this.showList.stream()
                .filter(note -> {
                    for (String substr : input) {
                        if (note.getName().contains(substr) || note.getText().contains(substr)) return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());
        this.showNotes(this.showList);
    }

    @Override
    public void start(IStorage storage) {
        System.out.println("Добро пожаловать!\n");
        this.storage = storage;
        this.showList = this.storage.getNotes();
        String input;
        while (true) {
            input = this.userDialog(this.showList.size() == this.storage.getNotes().size() ?
                    this.printCommands : this.printCommands + "c) - сбросить фильтр\n").toLowerCase();
            switch (input) {
                case "q":
                case "й":
                    System.out.println("Программа окончена!");
                    return;
                case "a":
                case "ф":
                    this.newNote();
                    break;
                case "v":
                case "м":
                    this.showNotes(this.showList);
                    break;
                case "o":
                case "щ":
                    this.openNote();
                    break;
                case "s":
                case "ы":
                    this.sortedNotes();
                    break;
                case "f":
                case "а":
                    this.findByText();
                    break;
                case "d":
                case "в":
                    this.removeNote();
                    break;
                case "c":
                case "с":
                    this.showList = this.storage.getNotes();
                    showNotes(this.showList);
                    break;
                default:
                    System.out.println("Команда не опознана. Попробуйте снова: ");
                    break;
            }
        }
    }
}
