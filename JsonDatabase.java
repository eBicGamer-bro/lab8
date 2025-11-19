import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class JsonDatabase<T> {

    private final String filename;
    private ArrayList<T> records;
    private final Class<T> type;

    public JsonDatabase(String filename, Class<T> type) {
        this.filename = filename;
        this.type = type;
        this.records = new ArrayList<>();
        createFileIfMissing();
        load();
    }

    private void createFileIfMissing() {
        try {
            File f = new File(filename);
            if (!f.exists()) {
                f.createNewFile();
                save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Field> getAllFields(Class<?> c) {
        ArrayList<Field> fields = new ArrayList<>();
        while (c != null) {
            for (Field f : c.getDeclaredFields()) fields.add(f);
            c = c.getSuperclass();
        }
        return fields;
    }

    public void add(T obj) {
        records.add(obj);
        save();
    }

    public ArrayList<T> getAll() {
        return records;
    }

    public void save() {
        JSONArray arr = new JSONArray();
        try {
            for (T obj : records) {
                JSONObject json = new JSONObject();
                for (Field field : getAllFields(type)) {
                    field.setAccessible(true);
                    json.put(field.getName(), field.get(obj));
                }
                arr.put(json);
            }
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(arr.toString(4));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            File file = new File(filename);
            if (file.length() == 0) return;

            StringBuilder text = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = br.readLine()) != null) text.append(line);
            }

            JSONArray arr = new JSONArray(text.toString());
            records.clear();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject json = arr.getJSONObject(i);
                T obj = type.getDeclaredConstructor().newInstance();
                for (Field field : getAllFields(type)) {
                    field.setAccessible(true);
                    if (json.has(field.getName())) {
                        field.set(obj, json.get(field.getName()));
                    }
                }
                records.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
