package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import note.INote;
import note.INoteImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileStorage extends DefaultStorage {

    private final String PATH;

    public FileStorage(String PATH) {
        super();
        this.PATH = PATH;
        try {
            this.readFile();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void readFile() throws IOException {
        String content = null;
        File file = new File(this.PATH);
        if (!Files.exists(Path.of(this.PATH)) || !file.canRead() || Files.size(Path.of(this.PATH)) == 0) return;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(this.PATH))) {
            StringBuilder stringBuilder = new StringBuilder();
            String row;
            while ((row = bufferedReader.readLine()) != null) {
                stringBuilder.append(row);
            }
            content = stringBuilder.toString();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
        this.parseJsonToList(content);
    }

    private void parseJsonToList(String json) {
        List<INote> noteList = new ArrayList<>();
        try {
            JSONArray jsonArray = (JSONArray) new JSONParser().parse(json);
            Gson gson = new GsonBuilder().create();
            for (Object object : jsonArray) {
                JSONObject jsonObject = (JSONObject) object;
                INoteImpl note = gson.fromJson(jsonObject.toJSONString(), INoteImpl.class);
                noteList.add(note);
            }
        } catch (org.json.simple.parser.ParseException e) {
            System.out.println(e.getMessage());
        }
        this.fillStorage(noteList);
    }

    private void fillStorage(List<INote> noteList) {
        if (!noteList.isEmpty()) {
            for (INote note : noteList) {
                this.notes.put(note.getKey(), note);
            }
        }
    }

    private String listToJson(List<INote> noteList) {
        Type listType = new TypeToken<List<INote>>() {
        }.getType();
        Gson gson = new GsonBuilder().create();
        return gson.toJson(noteList, listType);
    }

    private void writeFile(String jsonString) {
        File file = new File(this.PATH);
        if (!file.exists()) {
            if (!this.newFile(file)) {
                System.out.println("Файл хранения заметок не найден и не может быть создан\n");
            }
        } else {
            try (FileWriter jsonWriter = new FileWriter(this.PATH)) {
                jsonWriter.write(jsonString);
                jsonWriter.flush();
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private boolean newFile(File file) {
        try {
            return file.createNewFile();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public void inputNote(INote note) {
        super.inputNote(note);
        String jsonStr = this.listToJson(this.getNotes());
        this.writeFile(jsonStr);
    }

    @Override
    public void shiftNote(String id, String newName, String newContent) {
        super.shiftNote(id, newName, newContent);
        String jsonStr = this.listToJson(this.getNotes());
        this.writeFile(jsonStr);
    }

    @Override
    public boolean removeNote(INote note) {
        boolean noteRemoved = super.removeNote(note);
        if (noteRemoved) {
            String jsonStr = this.listToJson(this.getNotes());
            this.writeFile(jsonStr);
        }
        return noteRemoved;
    }
}
