import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class Nine {

    public static void main(String[] args) throws IOException {
        readFile("../pride-and-prejudice.txt");
    }

    static void readFile(String pathToFile) throws IOException {
        String data = new String(Files.readAllBytes(Paths.get(pathToFile)));
        filterChars(data);
    }

    static void filterChars(String data) {
        String filtered = Pattern.compile("[\\W_]+").matcher(data).replaceAll(" ");
        normalize(filtered);
    }

    static void normalize(String data) {
        scan(data.toLowerCase());
    }

    static void scan(String data) {
        List<String> words = Arrays.asList(data.split(" "));
        removeStopWords(words);
    }

    static void removeStopWords(List<String> words) {
        try {
            String stopWordsContent = new String(Files.readAllBytes(Paths.get("../stop_words.txt")));
            Set<String> stopWords = new HashSet<>(Arrays.asList(stopWordsContent.split(",")));
            for (char c = 'a'; c <= 'z'; c++) {
                stopWords.add(String.valueOf(c));
            }
            List<String> filtered = new ArrayList<>();
            for (String w : words) {
                if (!stopWords.contains(w)) {
                    filtered.add(w);
                }
            }
            frequencies(filtered);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void frequencies(List<String> words) {
        Map<String, Integer> wf = new HashMap<>();
        for (String w : words) {
            wf.put(w, wf.getOrDefault(w, 0) + 1);
        }
        sort(wf);
    }

    static void sort(Map<String, Integer> wf) {
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(wf.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        printText(sorted);
    }

    static void printText(List<Map.Entry<String, Integer>> wordFreqs) {
        for (int i = 0; i < Math.min(25, wordFreqs.size()); i++) {
            Map.Entry<String, Integer> entry = wordFreqs.get(i);
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }
}
