import java.util.*;

public class Frequencies1 implements Frequencies {
    public List<Map.Entry<String, Integer>> top25(List<String> words) {
        Map<String, Integer> freqMap = new HashMap<>();
        for (String word : words) {
            freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
        }

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(freqMap.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return sorted.subList(0, Math.min(25, sorted.size()));
    }
}
