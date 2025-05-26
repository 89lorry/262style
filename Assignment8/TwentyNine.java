import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

abstract class ActiveWFObject extends Thread {
    protected BlockingQueue<List<Object>> queue = new LinkedBlockingQueue<>();
    protected volatile boolean stopMe = false;

    public ActiveWFObject() {
        this.start();
    }

    public void send(List<Object> message) {
        queue.offer(message);
    }

    @Override
    public void run() {
        while (!stopMe) {
            try {
                List<Object> message = queue.take();
                if ("die".equals(message.get(0))) {
                    stopMe = true;
                    continue;
                }
                dispatch(message);
            } catch (InterruptedException e) {
                stopMe = true;
            }
        }
    }

    protected abstract void dispatch(List<Object> message);
}

class DataStorageManager extends ActiveWFObject {
    private String data = "";
    private StopWordManager stopWordManager;

    @Override
    protected void dispatch(List<Object> message) {
        switch (message.get(0).toString()) {
            case "init":
                init(message);
                break;
            case "send_word_freqs":
                processWords((ActiveWFObject) message.get(1));
                break;
            default:
                stopWordManager.send(message);
        }
    }

    private void init(List<Object> message) {
        String pathToFile = (String) message.get(1);
        stopWordManager = (StopWordManager) message.get(2);

        try {
            String content = new String(Files.readAllBytes(Paths.get(pathToFile)));
            data = content.replaceAll("[\\W_]+", " ").toLowerCase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processWords(ActiveWFObject recipient) {
        for (String word : data.split("\\s+")) {
            stopWordManager.send(Arrays.asList("filter", word));
        }
        stopWordManager.send(Arrays.asList("top25", recipient));
    }
}

class StopWordManager extends ActiveWFObject {
    private Set<String> stopWords = new HashSet<>();
    private WordFrequencyManager wordFreqsManager;

    @Override
    protected void dispatch(List<Object> message) {
        switch (message.get(0).toString()) {
            case "init":
                init(message);
                break;
            case "filter":
                filter(message.get(1).toString());
                break;
            default:
                wordFreqsManager.send(message);
        }
    }

    private void init(List<Object> message) {
        wordFreqsManager = (WordFrequencyManager) message.get(1);
        try {
            List<String> lines = Files.readAllLines(Paths.get("../stop_words.txt"));
            for (String line : lines) {
                stopWords.addAll(Arrays.asList(line.split(",")));
            }
            for (char c = 'a'; c <= 'z'; c++) {
                stopWords.add(String.valueOf(c));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void filter(String word) {
        if (!stopWords.contains(word)) {
            wordFreqsManager.send(Arrays.asList("word", word));
        }
    }
}

class WordFrequencyManager extends ActiveWFObject {
    private Map<String, Integer> wordFreqs = new HashMap<>();

    @Override
    protected void dispatch(List<Object> message) {
        switch (message.get(0).toString()) {
            case "word":
                incrementCount(message.get(1).toString());
                break;
            case "top25":
                top25((ActiveWFObject) message.get(1));
                break;
        }
    }

    private void incrementCount(String word) {
        wordFreqs.put(word, wordFreqs.getOrDefault(word, 0) + 1);
    }

    private void top25(ActiveWFObject recipient) {
        List<Map.Entry<String, Integer>> sorted = wordFreqs.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(25)
                .collect(Collectors.toList());
        recipient.send(Arrays.asList("top25", sorted));
    }
}

class WordFrequencyController extends ActiveWFObject {
    private DataStorageManager storageManager;
    private StopWordManager stopWordManager;
    private WordFrequencyManager wordFreqManager;

    @Override
    protected void dispatch(List<Object> message) {
        switch (message.get(0).toString()) {
            case "run":
                runApp((DataStorageManager) message.get(1),
                        (StopWordManager) message.get(2),
                        (WordFrequencyManager) message.get(3));
                break;
            case "top25":
                display((List<Map.Entry<String, Integer>>) message.get(1));
                break;
            case "die":
                stopMe = true;
                break;
            default:
                break;
        }
    }

    private void runApp(DataStorageManager storageManager, StopWordManager stopWordManager, WordFrequencyManager wordFreqManager) {
        this.storageManager = storageManager;
        this.stopWordManager = stopWordManager;
        this.wordFreqManager = wordFreqManager;
        storageManager.send(Arrays.asList("send_word_freqs", this));
    }

    private void display(List<Map.Entry<String, Integer>> wordFreqs) {
        for (Map.Entry<String, Integer> entry : wordFreqs) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }

        storageManager.send(Arrays.asList("die"));
        stopWordManager.send(Arrays.asList("die"));
        wordFreqManager.send(Arrays.asList("die"));
        this.send(Arrays.asList("die"));
    }
}

public class TwentyNine {
    public static void main(String[] args) throws InterruptedException {

        WordFrequencyManager wordFreqManager = new WordFrequencyManager();
        StopWordManager stopWordManager = new StopWordManager();
        stopWordManager.send(Arrays.asList("init", wordFreqManager));

        DataStorageManager storageManager = new DataStorageManager();
        storageManager.send(Arrays.asList("init", args[0], stopWordManager));

        WordFrequencyController controller = new WordFrequencyController();
        controller.send(Arrays.asList("run", storageManager, stopWordManager, wordFreqManager));

        wordFreqManager.join();
        stopWordManager.join();
        storageManager.join();
        controller.join();
    }
}
