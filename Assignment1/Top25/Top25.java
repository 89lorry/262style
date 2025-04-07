import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Top25 {
    public static void main(String[] args) throws IOException {

        // read stop words
        Set<String> stopWords = new HashSet<>();
        try (Scanner stopWordsScanner = new Scanner(new File("../stop_words.txt"))) {
            stopWordsScanner.useDelimiter(",");
            while (stopWordsScanner.hasNext()) {
                stopWords.add(stopWordsScanner.next());
            }
        }

        // read the book and count
        Map<String, Integer> wordCounts = new HashMap<>();
        try (Scanner fileScanner = new Scanner(new File(args[0]))) {
            fileScanner.useDelimiter("[^a-zA-Z]+");
            while (fileScanner.hasNext()) {
                String word = fileScanner.next().toLowerCase();
                if (word.length() > 1 && !stopWords.contains(word)) {
                    wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
                }
            }
        }


        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(wordCounts.entrySet());
        sortedEntries.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        List<Map.Entry<String, Integer>> top25 = sortedEntries.subList(0, 25);
        for (Map.Entry<String, Integer> entry : top25) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }
}