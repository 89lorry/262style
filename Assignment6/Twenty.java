import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Twenty {
    public static void main(String[] args) throws Exception {
        String inputFile = "../pride-and-prejudice.txt";

        Properties props = new Properties();
        props.load(new FileInputStream("config.properties"));

        String wordsClass = props.getProperty("words");
        String freqsClass = props.getProperty("frequencies");

        Words wordsPlugin = (Words) Class.forName(wordsClass).getDeclaredConstructor().newInstance();
        Frequencies freqPlugin = (Frequencies) Class.forName(freqsClass).getDeclaredConstructor().newInstance();

        List<String> words = wordsPlugin.extractWords(inputFile);
        List<Map.Entry<String, Integer>> top = freqPlugin.top25(words);

        for (Map.Entry<String, Integer> entry : top) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }
}
