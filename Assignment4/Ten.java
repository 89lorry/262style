import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class Ten {

    public static void main(String[] args) {
        try {
            TheOne<String> fileData = TheOne.of(Files.readString(Paths.get("../pride-and-prejudice.txt")));

            fileData
                .bind(Ten::filterChars)
                .bind(Ten::normalize)
                .bind(Ten::scan)
                .bind(Ten::removeStopWords)
                .bind(Ten::frequencies)
                .bind(Ten::sort)
                .unwrap(Ten::printText);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static TheOne<String> filterChars(String data) {
        String filtered = Pattern.compile("[\\W_]+").matcher(data).replaceAll(" ");
        return TheOne.of(filtered);
    }

    static TheOne<String> normalize(String data) {
        return TheOne.of(data.toLowerCase());
    }

    static TheOne<List<String>> scan(String data) {
        return TheOne.of(Arrays.asList(data.split(" ")));
    }

    static TheOne<List<String>> removeStopWords(List<String> words) {
        try {
            String stopWordsContent = Files.readString(Paths.get("../stop_words.txt"));
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
            return TheOne.of(filtered);
        } catch (IOException e) {
            e.printStackTrace();
            return TheOne.of(words);
        }
    }

    static TheOne<Map<String, Integer>> frequencies(List<String> words) {
        Map<String, Integer> wf = new HashMap<>();
        for (String w : words) {
            wf.put(w, wf.getOrDefault(w, 0) + 1);
        }
        return TheOne.of(wf);
    }

    static TheOne<List<Map.Entry<String, Integer>>> sort(Map<String, Integer> wf) {
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(wf.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return TheOne.of(sorted);
    }

    static void printText(List<Map.Entry<String, Integer>> wordFreqs) {
        for (int i = 0; i < Math.min(25, wordFreqs.size()); i++) {
            Map.Entry<String, Integer> entry = wordFreqs.get(i);
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }
}

// The One 
class TheOne<T> {
    private final T value;

    private TheOne(T value) {
        this.value = value;
    }

    public static <T> TheOne<T> of(T value) {
        return new TheOne<>(value);
    }

    public <R> TheOne<R> bind(Function<T, TheOne<R>> func) {
        return func.apply(value);
    }

    public void unwrap(Consumer<T> consumer) {
        consumer.accept(value);
    }

    interface Function<T, R> {
        R apply(T value);
    }

    interface Consumer<T> {
        void accept(T value);
    }
}
