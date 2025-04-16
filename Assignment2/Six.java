import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;


public class Six {

    private static String read_file(String path_to_file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path_to_file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static String filter_chars_and_normalize(String str_data) {
        Pattern pattern = Pattern.compile("[\\W_]+");
        Matcher matcher = pattern.matcher(str_data);
        return matcher.replaceAll(" ").toLowerCase();
    }

    //scan string to list
    private static List<String> scan(String str_data) {
        // split by blank space
        return Arrays.asList(str_data.split("\\s+"));
    }


    private static List<String> remove_stop_words(List<String> word_list) {
        Set<String> stop_words=new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader("stop_words.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line;
        //split by ,
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String[] stops = sb.toString().split(",");
            stop_words.addAll(Arrays.asList(stops));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // add all letter
        for (char c = 'a'; c <= 'z'; c++) {
            stop_words.add(String.valueOf(c));
        }

        List<String> result = new ArrayList<>();
        for (String w : word_list) {
            if (!stop_words.contains(w)) {
                result.add(w);
            }
        }
        return result;
    }

    private static Map<String, Integer> frequencies(List<String> word_list) {
        Map<String, Integer> word_freqs = new HashMap<>();
        for (String w : word_list) {
            word_freqs.put(w, word_freqs.getOrDefault(w, 0) + 1);
        }
        return word_freqs;
    }

    // turn map into a list in decsending order
    private static List<Map.Entry<String, Integer>> sort(Map<String, Integer> word_freq) {
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(word_freq.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return sortedList;
    }


    private static void print_all(List<Map.Entry<String, Integer>> word_freqs) {
        if (word_freqs.size() > 0) {
            Map.Entry<String, Integer> first = word_freqs.get(0);
            System.out.println(first.getKey() + " - " + first.getValue());
            // delete the first one when in recursion
            print_all(word_freqs.subList(1, word_freqs.size()));
        }
    }

    public static void main(String[] args) {

        List<Map.Entry<String, Integer>> sortedList = sort(
                frequencies(
                        remove_stop_words(
                                scan(
                                        filter_chars_and_normalize(
                                                read_file(args[0])
                                        )
                                )
                        )
                )
        );

        // print top 25
        int end = Math.min(25, sortedList.size());
        print_all(sortedList.subList(0, end));
    }
}
