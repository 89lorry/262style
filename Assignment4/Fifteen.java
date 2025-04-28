import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class Fifteen {
    public static void main(String[] args) throws IOException {
        WordFrequencyFramework wfapp = new WordFrequencyFramework();
        StopWordFilter stopWordFilter = new StopWordFilter(wfapp);
        DataStorage dataStorage = new DataStorage(wfapp, stopWordFilter);
        WordFrequencyCounter wordFreqCounter = new WordFrequencyCounter(wfapp, dataStorage);
        wfapp.run("../pride-and-prejudice.txt");
    }
}

class WordFrequencyFramework {
    private final List<LoadEventHandler> loadHandlers = new ArrayList<>();
    private final List<DoWorkEventHandler> doWorkHandlers = new ArrayList<>();
    private final List<EndEventHandler> endHandlers = new ArrayList<>();

    public void registerForLoadEvent(LoadEventHandler handler) {
        loadHandlers.add(handler);
    }

    public void registerForDoWorkEvent(DoWorkEventHandler handler) {
        doWorkHandlers.add(handler);
    }

    public void registerForEndEvent(EndEventHandler handler) {
        endHandlers.add(handler);
    }

    public void run(String pathToFile) throws IOException {
        for (LoadEventHandler handler : loadHandlers) {
            handler.load(pathToFile);
        }
        for (DoWorkEventHandler handler : doWorkHandlers) {
            handler.doWork();
        }
        for (EndEventHandler handler : endHandlers) {
            handler.end();
        }
    }
}

interface LoadEventHandler {
    void load(String pathToFile) throws IOException;
}

interface DoWorkEventHandler {
    void doWork();
}

interface EndEventHandler {
    void end();
}

class DataStorage {
    private String data = "";
    private final StopWordFilter stopWordFilter;
    private final List<WordEventHandler> wordHandlers = new ArrayList<>();

    public DataStorage(WordFrequencyFramework wfapp, StopWordFilter stopWordFilter) {
        this.stopWordFilter = stopWordFilter;
        wfapp.registerForLoadEvent(this::load);
        wfapp.registerForDoWorkEvent(this::produceWords);
    }

    private void load(String pathToFile) throws IOException {
        data = new String(Files.readAllBytes(Paths.get(pathToFile)));
        Pattern pattern = Pattern.compile("[\\W_]+");
        data = pattern.matcher(data).replaceAll(" ").toLowerCase();
    }

    private void produceWords() {
        for (String word : data.split(" ")) {
            if (!stopWordFilter.isStopWord(word)) {
                for (WordEventHandler handler : wordHandlers) {
                    handler.handle(word);
                }
            }
        }
    }

    public void registerForWordEvent(WordEventHandler handler) {
        wordHandlers.add(handler);
    }
}

interface WordEventHandler {
    void handle(String word);
}

class StopWordFilter {
    private final Set<String> stopWords = new HashSet<>();

    public StopWordFilter(WordFrequencyFramework wfapp) {
        wfapp.registerForLoadEvent(this::load);
    }

    private void load(String ignore) throws IOException {
        String stopWordsContent = new String(Files.readAllBytes(Paths.get("../stop_words.txt")));
        stopWords.addAll(Arrays.asList(stopWordsContent.split(",")));
        for (char c = 'a'; c <= 'z'; c++) {
            stopWords.add(String.valueOf(c));
        }
    }

    public boolean isStopWord(String word) {
        return stopWords.contains(word);
    }
}

class WordFrequencyCounter {
    private final Map<String, Integer> wordFreqs = new HashMap<>();

    public WordFrequencyCounter(WordFrequencyFramework wfapp, DataStorage dataStorage) {
        dataStorage.registerForWordEvent(this::incrementCount);
        wfapp.registerForEndEvent(this::printFreqs);
    }

    private void incrementCount(String word) {
        wordFreqs.put(word, wordFreqs.getOrDefault(word, 0) + 1);
    }

    private void printFreqs() {
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(wordFreqs.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        for (int i = 0; i < Math.min(25, sorted.size()); i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }
}
