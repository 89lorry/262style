import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Frequencies {
    List<Map.Entry<String, Integer>> top25(List<String> words) throws IOException;
}
