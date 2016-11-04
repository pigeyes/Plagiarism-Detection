import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Scanner;
import java.util.Map;


public class Main {

    private static final String USAGE = "usage: java PlagiarismDetector.java [syns_file] [file1] [file2] [tuple_size]";
    private static final int DEFAULT_TUPLE_SIZE = 3;

    public static void main(String[] args) throws IOException {

        if (args.length < 3) {
            System.out.println(USAGE);
            return;
        }

        int tuple_size = DEFAULT_TUPLE_SIZE;
        String synsFileName = args[0];
        String file1 = args[1];
        String file2 = args[2];
        if (args.length >= 4) {
            try {
                tuple_size = Integer.parseInt(args[3]);
            } catch (Exception e) {
                System.out.print(USAGE);
                return;
            }
        }

        Map<String, Integer> map = new HashMap<>();
        Set<String> mask = new HashSet<>();
        getSyns(map, synsFileName);
        getTupleMask(mask, map, file2, tuple_size);
        double res = calculateMatch(file1, mask, map, tuple_size);
        System.out.println(new DecimalFormat("#.##").format(res) + " %");

    }

    public static void getSyns(Map<String, Integer> map, String synsFileName) {
        try (Scanner scanner = new Scanner(new File(synsFileName))) {
            int group = 0;
            while (scanner.hasNext()) {
                String[] words = scanner.nextLine().split(" ");
                for (String word : words) {
                    map.put(word.toLowerCase(), group);
                }
                group++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getTupleMask(Set<String> tuples, Map<String, Integer> map,
                                    String file2, int tuple_size) throws IOException {

        String[] words = readFile(file2);
        for (int i = 0; i <= words.length - tuple_size; i++) {
            StringBuilder key = new StringBuilder();
            for (int k = 0; k < tuple_size; k++) {
                String tmp = words[i + k].toLowerCase();
                if (map.containsKey(tmp)) {
                    key.append("#").append(map.get(tmp)).append(" ");
                } else {
                    key.append(tmp).append(" ");
                }
            }
            tuples.add(key.toString());
        }
    }

    public static double calculateMatch(String file1, Set<String> mask,
                                        Map<String, Integer> map, int tuple_size) throws IOException {

        String[] words = readFile(file1);
        if (words.length < tuple_size) {
            return 0;
        }

        int totalTuple = 0;
        int matchedTuple = 0;
        for (int i = 0; i <= words.length - tuple_size; i++) {
            StringBuilder key = new StringBuilder();
            for (int k = 0; k < tuple_size; k++) {
                String tmp = words[i + k].toLowerCase();
                if (map.containsKey(tmp)) {
                    key.append("#").append(map.get(tmp)).append(" ");
                } else {
                    key.append(tmp).append(" ");
                }
            }
            if (mask.contains(key.toString())) {
                matchedTuple++;
            }
            totalTuple++;
        }
        return (double) (matchedTuple) / (double) (totalTuple) * 100.0;

    }

    public static String[] readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString().split("[^a-zA-Z]+");
        } finally {
            br.close();
        }
    }
}
