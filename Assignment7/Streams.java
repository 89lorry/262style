import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

class Streams {
  public static void main(String[] a) throws Exception {
    var txt = Files.readString(Path.of(a[0])).toLowerCase();
    var stop = Set.of(Files.readString(Path.of("../stop_words.txt")).split(","));
    var freq = Pattern.compile("[a-z]{2,}").matcher(txt).results()
      .map(m -> m.group()).filter(w -> !stop.contains(w))
      .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

    freq.entrySet().stream().sorted(Map.Entry.<String,Long>comparingByValue().reversed())
      .limit(25).forEach(e -> System.out.println(e.getKey() + " - " + e.getValue()));
  }
}
