import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class JSONParser {
    /**
     * Inner class JsonMap
     */
    class JsonMap {
        public int id;
        public String first_name;
        public String last_name;
    }

    private Gson gson = new Gson();

    public void read(String filePath) throws FileNotFoundException {
        FileReader fileReader = new FileReader(filePath);
        JsonReader jsonReader = new JsonReader(fileReader);

        JsonMap[] data = gson.fromJson(jsonReader, JsonMap[].class);
    }
}
