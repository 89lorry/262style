import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class Words1 implements Words {
    public List<String> extractWords(String filePath) throws IOException {
        String text = Files.readString(Paths.get(filePath)).toLowerCase();
        String[] allWords = Pattern.compile("[\\W_]+").split(text);

        String stopWordsText =Files.readString(Paths.get("../stop_words.txt"));
        Set<String> stopWords = new HashSet<>(Arrays.asList(stopWordsText.split(",")));
        for (char c = 'a'; c <= 'z'; c++) {
            stopWords.add(String.valueOf(c));
        }

        List<String> result = new ArrayList<>();
        for (String word : allWords) {
            if (!stopWords.contains(word)) {
                result.add(word);
            }
        }
        return result;
    }
}
