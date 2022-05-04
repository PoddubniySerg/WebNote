package feachers;

import java.util.List;

public interface IConverterJson<T> {

    String jsonFromList(List<T> list);
}
