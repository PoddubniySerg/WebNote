package app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import feachers.ConverterJsonImpl;
import note.INote;
import note.INoteImpl;
import note.card.Card;
import storage.IStorage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AppServer implements IApp {
    private static final int PORT = 8888;
    private static final String HOST = "localhost";

    private IStorage storage;

    private final HttpServer server;

    public AppServer() throws Exception {

        server = HttpServer.create(new InetSocketAddress(HOST, PORT), 0);
        server.createContext("/", this::serveHtml);
        server.createContext("/newNote", this::newNote);
        server.createContext("/getCards", this::getCards);
        server.createContext("/getName", this::getName);
        server.createContext("/getContent", this::getContent);
        server.createContext("/editNote", this::editNote);
        server.createContext("/removeNote", this::removeNote);
    }

    protected void serveHtml(HttpExchange h) throws IOException {
        Path htmlPath = Path.of("assets/index.html");
        String htmlContent = Files.readString(htmlPath);
        sendAndClose(h, htmlContent);
    }

    protected void newNote(HttpExchange h) throws IOException {
        String name = null;
        String content = null;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(h.getRequestBody()))) {
            StringBuilder stringBuilder = new StringBuilder();
            name = bufferedReader.readLine();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            content = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        INote note = createNote(name, content);
        this.storage.inputNote(note);
        String htmlContent = getCardJson(note);
        sendAndClose(h, htmlContent);
    }

    protected void editNote(HttpExchange h) throws IOException {
        String id = "";
        String name = "";
        String content = "";
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(h.getRequestBody()))) {
            StringBuilder stringBuilder = new StringBuilder();
            id = bufferedReader.readLine();
            name = bufferedReader.readLine();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            content = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.storage.shiftNote(id, name, content);
        String htmlContent = this.storage.getNotesId().contains(id) ? getCardJson(this.storage.getNote(id)) : "";
        sendAndClose(h, htmlContent);
    }

    protected void getCards(HttpExchange h) throws IOException {
        List<Card> cards = new ArrayList<>();
        if (!this.storage.getNotes().isEmpty()) {
            for (INote note : this.storage.getNotes()) {
                cards.add(new Card(note));
            }
        } else {
            int countOfCards = 0;
            INote emptyNote = new INoteImpl(countOfCards);
            emptyNote.setName("");
            emptyNote.setText("");
            cards.add(new Card(emptyNote));
            System.out.println(cards);
        }
        String htmlContent = new ConverterJsonImpl<Card>().jsonFromList(cards);
        sendAndClose(h, htmlContent);
    }

    protected void getName(HttpExchange h) throws IOException {
        String id = getIdFromRequest(h);
        String htmlContent = id == null ? "" : this.storage.getNote(id).getName();
        sendAndClose(h, htmlContent);
    }

    protected void getContent(HttpExchange h) throws IOException {
        String id = getIdFromRequest(h);
        String htmlContent = id == null ? "" : this.storage.getNote(id).getText();
        sendAndClose(h, htmlContent);
    }

    protected void removeNote(HttpExchange h) throws IOException {
        String id = "";
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(h.getRequestBody()))) {
            id = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.storage.removeNote(this.storage.getNote(id));
        String htmlContent = "";
        sendAndClose(h, htmlContent);
    }

    protected INote createNote(String name, String content) {
        INote note = new INoteImpl();
        note.setName(name == null || name.isEmpty() || name.split(" ").length == 0 ? "Без названия" : name);
        note.setText(content == null || content.isEmpty() ? "" : content);
        return note;
    }

    protected String getIdFromRequest(HttpExchange h) {
        String id = null;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(h.getRequestBody()))) {
            id = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return id;
    }

    protected String getCardJson(INote note) {
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(note));
        return new ConverterJsonImpl<Card>().jsonFromList(cards);
    }

    protected void sendAndClose(HttpExchange h, String htmlContent) throws IOException {
        byte[] htmlBytes = htmlContent.getBytes();
        h.sendResponseHeaders(200, htmlBytes.length);
        h.getResponseBody().write(htmlBytes);
        h.close();
    }

    @Override
    public void start(IStorage storage) {
        this.storage = storage;
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://" + HOST + ":" + PORT + "/");
        server.start();
    }
}