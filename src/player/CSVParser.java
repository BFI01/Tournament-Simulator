package player;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CSVParser {
    public List<String[]> readFromFile(String filePath) throws IOException, CsvException {
        CSVReader reader = new CSVReader(new FileReader(filePath));
        return reader.readAll();
    }
}
