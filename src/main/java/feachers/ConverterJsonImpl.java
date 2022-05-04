package feachers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ConverterJsonImpl<E> implements IConverterJson<E> {
    @Override
    public String jsonFromList(List<E> list) {
        Type listType = new TypeToken<List<E>>() {
        }.getType();
        Gson gson = new GsonBuilder().create();
        return gson.toJson(list, listType);
    }
}
