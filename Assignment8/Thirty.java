import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class Thirty {

    static BlockingQueue<String> wordSpace = new LinkedBlockingQueue<>();
    static BlockingQueue<Map<String, Integer>> freqSpace = new LinkedBlockingQueue<>();
    static Set<String> stopWords = new HashSet<>();


    static void loadStopWords() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("../stop_words.txt"));
        for (String line : lines) {
            String[] words = line.split(",");
            stopWords.addAll(Arrays.asList(words));
        }
    }

    static class Worker implements Runnable {
        @Override
        public void run() {
            Map<String, Integer> wordFreqs = new HashMap<>();
            while (true) {
                String word;
                try {
                    word = wordSpace.poll(1, TimeUnit.SECONDS);
                    if (word == null) break; 
                } catch (InterruptedException e) {
                    break;
                }

                if (!stopWords.contains(word)) {
                    wordFreqs.put(word, wordFreqs.getOrDefault(word, 0) + 1);
                }
            }
            freqSpace.offer(wordFreqs);
        }
    }

    public static void main(String[] args) throws Exception {
        loadStopWords();

        String content = new String(Files.readAllBytes(Paths.get(args[0]))).toLowerCase();
        Matcher matcher = Pattern.compile("[a-z]{2,}").matcher(content);
        while (matcher.find()) {
            wordSpace.offer(matcher.group());
        }


        int numThreads = 5;
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            Thread t = new Thread(new Worker());
            workers.add(t);
            t.start();
        }

        for (Thread t : workers) {
            t.join();
        }


        Map<String, Integer> finalFreqs = new HashMap<>();
        while (!freqSpace.isEmpty()) {
            Map<String, Integer> partial = freqSpace.poll();
            if (partial != null) {
                for (Map.Entry<String, Integer> entry : partial.entrySet()) {
                    String word = entry.getKey();
                    int count = entry.getValue();
                    finalFreqs.put(word, finalFreqs.getOrDefault(word, 0) + count);
                }
            }
        }


        finalFreqs.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(25)
            .forEach(e -> System.out.println(e.getKey() + " - " + e.getValue()));
    }
}
