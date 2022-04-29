import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import player.Player;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class JSONParser {
    private final Gson gson = new Gson();

    public Player[] read(String filePath) throws FileNotFoundException {
        FileReader fileReader = new FileReader(filePath);
        JsonReader jsonReader = new JsonReader(fileReader);
        return gson.fromJson(jsonReader, Player[].class);
    }
}
