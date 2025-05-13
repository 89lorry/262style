import java.io.IOException;
import java.util.List;

public interface Words {
    List<String> extractWords(String filePath) throws IOException;
}
