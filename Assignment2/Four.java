import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Four {

    // format is [word, frequency] 
    private static final List<String[]> word_freqs = new ArrayList<>();
    // list for stopwords
    private static final List<String> stop_words = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        initStopWords("stop_words.txt");
        List<String> lines = Files.readAllLines(Paths.get("pride-and-prejudice.txt"));

        for (String line : lines) {
            processLine(line);
        }

        // print top 25 frequency
        for (int k = 0; k < 25 && k < word_freqs.size(); k++) {
            String[] tf = word_freqs.get(k);
            System.out.println(tf[0] + " - " + tf[1]);
        }
    }


    private static void initStopWords(String stopWordsFile) throws IOException {

        String stopWordsData = new String(Files.readAllBytes(Paths.get(stopWordsFile)));
        stop_words.addAll(Arrays.asList(stopWordsData.split(",")));
        // add single letter
        for(char c = 'a'; c <= 'z'; c++){
            stop_words.add(String.valueOf(c));
        }
    }

    private static void processLine(String line) {
        Integer start_char = null; 
        char[] chars = line.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (start_char == null) {
                if (Character.isLetterOrDigit(c)) {
                    start_char = i;
                }
            } else {
                if (!Character.isLetterOrDigit(c)) {
                    String word = line.substring(start_char, i).toLowerCase();
                    processWord(word);
                    start_char = null;
                }
            }
        }

        if (start_char != null) {
            String word = line.substring(start_char).toLowerCase();
            processWord(word);
        }
    }

    private static void processWord(String word) {
        if (stop_words.contains(word)) {
            return;
        }
        // find the word in word_freqs list
        boolean found = false;
        int pair_index = 0;
        for (String[] pair : word_freqs) {
            if (word.equals(pair[0])) {
                pair[1] = String.valueOf(Integer.parseInt(pair[1]) + 1);
                found = true;
                break;
            }
            pair_index++;
        }

        if (!found) {
            word_freqs.add(new String[]{word, "1"});
        } 
        // if found, bubble sort
        else if (word_freqs.size() > 1) {
            for (int n = pair_index - 1; n >= 0; n--) {
                int currentFreq = Integer.parseInt(word_freqs.get(pair_index)[1]);
                int compareFreq = Integer.parseInt(word_freqs.get(n)[1]);
                if (currentFreq > compareFreq) {
                    // swap
                    String[] temp = word_freqs.get(n);
                    word_freqs.set(n, word_freqs.get(pair_index));
                    word_freqs.set(pair_index, temp);
                    pair_index = n;
                } else {
                    break;
                }
            }
        }
    }
}
