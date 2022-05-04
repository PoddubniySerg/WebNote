import app.ConsoleApp;
import app.IApp;
import app.AppServer;
import storage.FileStorage;
import storage.IStorage;

public class Main {

    public static void main(String[] args) throws Exception {
        IStorage storage = new FileStorage("./assets/data.json");
        IApp[] apps = {new AppServer(), new ConsoleApp()};
        for (IApp app : apps) {
            app.start(storage);
        }
    }
}
