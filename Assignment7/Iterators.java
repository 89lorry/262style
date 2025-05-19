import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class Iterators {
    public static void main(String[] args) throws Exception {
        String filename = args[0];
        CountAndSort countAndSort = new CountAndSort(filename);
        while (countAndSort.hasNext()) {
            System.out.println("-----------------------------");
            List<Map.Entry<String, Integer>> top25 = countAndSort.next();
            for (int i = 0; i < Math.min(25, top25.size()); i++) {
                Map.Entry<String, Integer> entry = top25.get(i);
                System.out.println(entry.getKey() + " - " + entry.getValue());
            }
        }
    }
}

// Characters generator
class Characters implements Iterator<Character> {
    private BufferedReader reader;
    private int nextChar;

    public Characters(String filename) throws IOException {
        reader = new BufferedReader(new FileReader(filename));
        nextChar = reader.read();
    }

    @Override
    public boolean hasNext() {
        return nextChar != -1;
    }

    @Override
    public Character next() {
        try {
            int current = nextChar;
            nextChar = reader.read();
            return (char) current;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

// AllWords generator
class AllWords implements Iterator<String> {
    private Characters chars;
    private String nextWord = null;

    public AllWords(String filename) throws IOException {
        chars = new Characters(filename);
        advance();
    }

    private void advance() {
        StringBuilder word = new StringBuilder();
        boolean startChar = true;
        while (chars.hasNext()) {
            char c = chars.next();
            if (startChar) {
                if (Character.isLetterOrDigit(c)) {
                    word.append(Character.toLowerCase(c));
                    startChar = false;
                }
            } else {
                if (Character.isLetterOrDigit(c)) {
                    word.append(Character.toLowerCase(c));
                } else {
                    nextWord = word.toString();
                    return;
                }
            }
        }
        if (word.length() > 0) {
            nextWord = word.toString();
        } else {
            nextWord = null;
        }
    }

    @Override
    public boolean hasNext() {
        return nextWord != null;
    }

    @Override
    public String next() {
        String result = nextWord;
        advance();
        return result;
    }
}

// NonStopWords generator
class NonStopWords implements Iterator<String> {
    private AllWords allWords;
    private Set<String> stopWords;
    private String nextWord = null;

    public NonStopWords(String filename) throws IOException {
        allWords = new AllWords(filename);
        stopWords = new HashSet<>();
        String stopWordData = Files.readString(Path.of("../stop_words.txt")).strip();
        stopWords.addAll(Arrays.asList(stopWordData.split(",")));
        for (char ch = 'a'; ch <= 'z'; ch++) {
            stopWords.add(String.valueOf(ch));
        }
        advance();
    }

    private void advance() {
        while (allWords.hasNext()) {
            String w = allWords.next();
            if (!stopWords.contains(w)) {
                nextWord = w;
                return;
            }
        }
        nextWord = null;
    }

    @Override
    public boolean hasNext() {
        return nextWord != null;
    }

    @Override
    public String next() {
        String result = nextWord;
        advance();
        return result;
    }
}

// CountAndSort generator
class CountAndSort implements Iterator<List<Map.Entry<String, Integer>>> {
    private NonStopWords nonStopWords;
    private Map<String, Integer> freqs = new HashMap<>();
    private int count = 0;
    private boolean done = false;

    public CountAndSort(String filename) throws IOException {
        nonStopWords = new NonStopWords(filename);
    }

    @Override
    public boolean hasNext() {
        return !done;
    }

    @Override
    public List<Map.Entry<String, Integer>> next() {
        while (nonStopWords.hasNext()) {
            String word = nonStopWords.next();
            freqs.put(word, freqs.getOrDefault(word, 0) + 1);
            count++;
            if (count % 5000 == 0) {
                return sortedFreqs();
            }
        }
        done = true;
        return sortedFreqs();
    }

    private List<Map.Entry<String, Integer>> sortedFreqs() {
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(freqs.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return sorted;
    }
}
