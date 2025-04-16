import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Five {

    static ArrayList<Character> data = new ArrayList<>();
    static ArrayList<String> words = new ArrayList<>();
    static ArrayList<ArrayList<Object>> word_freqs = new ArrayList<>();

    static void read_file(String path_to_file) {
        try {
            String content = Files.readString(Paths.get(path_to_file));
            for (char c : content.toCharArray()) {
                data.add(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void filter_chars_and_normalize() {
        for (int i = 0; i < data.size(); i++) {
            char c = data.get(i);
            if (!Character.isLetterOrDigit(c)) {
                data.set(i, ' ');
            } else {
                data.set(i, Character.toLowerCase(c));
            }
        }
    }

    static void scan() {
        StringBuilder sb = new StringBuilder();
        for (char c : data) {
            sb.append(c);
        }
        String[] splitted = sb.toString().split("\\s+");
        words.addAll(Arrays.asList(splitted));
    }

    static void remove_stop_words() {
        try {
            String stopWordsStr = Files.readString(Paths.get("stop_words.txt"));
            String[] stopWordsArray = stopWordsStr.split(",");
            Set<String> stopWords = new HashSet<>(Arrays.asList(stopWordsArray));

            for (char c = 'a'; c <= 'z'; c++) {
                stopWords.add(String.valueOf(c));
            }

            ArrayList<Integer> indexes = new ArrayList<>();
            for (int i = 0; i < words.size(); i++) {
                if (stopWords.contains(words.get(i))) {
                    indexes.add(i);
                }
            }

            for (int i = indexes.size() - 1; i >= 0; i--) {
                words.remove((int)indexes.get(i));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void frequencies() {
        for (String w : words) {
            boolean found = false;
            for (ArrayList<Object> pair : word_freqs) {
                if (pair.get(0).equals(w)) {
                    pair.set(1, (Integer) pair.get(1) + 1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                ArrayList<Object> newPair = new ArrayList<>();
                newPair.add(w);
                newPair.add(1);
                word_freqs.add(newPair);
            }
        }
    }


    static void sort_word_freqs() {
        word_freqs.sort((a, b) ->
            (Integer)b.get(1) - (Integer)a.get(1)  // compera in descending order 
        );
    }


    public static void main(String[] args) {
        read_file(args[0]);
        filter_chars_and_normalize();
        scan();
        remove_stop_words();
        frequencies();
        sort_word_freqs();

        for (int i = 0; i < 25 && i < word_freqs.size(); i++) {
            ArrayList<Object> tf = word_freqs.get(i);
            System.out.println(tf.get(0) + " - " + tf.get(1));
        }
    }
}

