import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class ThirtyTwo {

    public static List<String> partition(String data, int nlines) {
        List<String> lines = Arrays.asList(data.split("\n"));
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < lines.size(); i += nlines) {
            int end = Math.min(i + nlines, lines.size());
            chunks.add(String.join("\n", lines.subList(i, end)));
        }
        return chunks;
    }

    // leave stop words, return [(word, 1), ...]
    public static List<Pair> splitWords(String data, Set<String> stopWords) {
        List<Pair> result = new ArrayList<>();
        String cleaned = data.replaceAll("[\\W_]+", " ").toLowerCase();
        for (String word : cleaned.split("\\s+")) {
            if (!stopWords.contains(word) && word.length() > 0) {
                result.add(new Pair(word, 1));
            }
        }
        return result;
    }

    // [(w1,1), (w2,1)] to Map<w1, List<Pair>>
    public static Map<String, List<Pair>> regroup(List<List<Pair>> allPairs) {
        Map<String, List<Pair>> mapping = new HashMap<>();
        for (List<Pair> pairs : allPairs) {
            for (Pair p : pairs) {
                mapping.computeIfAbsent(p.word, k -> new ArrayList<>()).add(p);
            }
        }
        return mapping;
    }

    //  Map<String, List<Pair>> to Map<String, Integer>
    public static List<Pair> countWords(Map<String, List<Pair>> grouped) {
        List<Pair> result = new ArrayList<>();
        for (Map.Entry<String, List<Pair>> entry : grouped.entrySet()) {
            int count = entry.getValue().stream().mapToInt(p -> p.count).sum();
            result.add(new Pair(entry.getKey(), count));
        }
        return result;
    }

    public static List<Pair> sort(List<Pair> wordFreqs) {
        return wordFreqs.stream()
                .sorted((a, b) -> Integer.compare(b.count, a.count))
                .collect(Collectors.toList());
    }


    public static Set<String> loadStopWords(String path) throws IOException {
        Set<String> stopWords = new HashSet<>();
        String content = new String(Files.readAllBytes(Paths.get(path)));
        String[] words = content.split(",");
        stopWords.addAll(Arrays.asList(words));
        for (char c = 'a'; c <= 'z'; c++) {
            stopWords.add(String.valueOf(c));
        }
        return stopWords;
    }

    static class Pair {
        String word;
        int count;

        Pair(String word, int count) {
            this.word = word;
            this.count = count;
        }
    }


    public static void main(String[] args) throws Exception {

        String inputText = new String(Files.readAllBytes(Paths.get(args[0])));
        Set<String> stopWords = loadStopWords("../stop_words.txt");

        List<List<Pair>> mappedPartitions = partition(inputText, 200).stream()
                .map(chunk -> splitWords(chunk, stopWords))
                .collect(Collectors.toList());

        Map<String, List<Pair>> grouped = regroup(mappedPartitions);
        List<Pair> wordFreqs = sort(countWords(grouped));

        for (int i = 0; i < Math.min(25, wordFreqs.size()); i++) {
            Pair p = wordFreqs.get(i);
            System.out.println(p.word + " - " + p.count);
        }
    }
}
