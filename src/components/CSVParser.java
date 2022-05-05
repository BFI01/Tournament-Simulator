package components;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Allows CSV file reading functionality.
 */
public class CSVParser {
    /**
     * Reads data from a given CSV file path.
     *
     * @param filePath {@link String} indicating the file path of the CSV file
     *
     * @return A 2-dimensional array of {@link String}s representing each comma separated
     * line of data.
     *
     * @throws IOException Thrown when an error with interacting with the file occurs.
     * @throws CsvException Thrown when an error with {@link CSVReader} occurs.
     */
    public List<String[]> readFromFile(String filePath) throws IOException, CsvException {
        CSVReader reader = new CSVReader(new FileReader(filePath));
        return reader.readAll();
    }
}
