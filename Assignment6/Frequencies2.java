import java.util.*;

public class Frequencies2 implements Frequencies {
    public List<Map.Entry<String, Integer>> top25(List<String> words) {
        Map<String, Integer> letterFreq = new HashMap<>();

        for (String word : words) {
            if (word.length() > 0) {
                String firstLetter = word.substring(0, 1);
                letterFreq.put(firstLetter, letterFreq.getOrDefault(firstLetter, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(letterFreq.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return sorted.subList(0, Math.min(25, sorted.size()));
    }
}
