
package gerenciadortarefas;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskStorage {
    private static final String FILE_NAME = "tasks.json";
    private static final DateTimeFormatter DF = DateTimeFormatter.ISO_LOCAL_DATE;

    private Gson gson;

    public TaskStorage() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        // LocalDate adapter (we store as string)
        builder.registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
            public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.format(DF));
            }
        });
        builder.registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
            public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return LocalDate.parse(json.getAsString(), DF);
            }
        });
        gson = builder.create();
    }

    public List<Task> load() {
        File f = new File(FILE_NAME);
        if (!f.exists()) return new ArrayList<>();
        try (Reader r = new FileReader(f)) {
            Type listType = new TypeToken<List<Task>>(){}.getType();
            List<Task> list = gson.fromJson(r, listType);
            return list == null ? new ArrayList<>() : list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void save(List<Task> tasks) {
        try (Writer w = new FileWriter(FILE_NAME)) {
            gson.toJson(tasks, w);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
