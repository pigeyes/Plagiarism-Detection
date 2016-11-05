import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * This is a simple plagiarism detector that calculate
 * overall similarity of one file to another according to a
 * given synonym list.
 *
 * @author  Ming-Ching Chu
 * @since   2016-11-04
 */

public class PlagiarismDetector {
    private static final int DEFAULT_TUPLE_SIZE = 3;
    private int tupleSize;
    private Map<String, String> synsMap;

    PlagiarismDetector(String synsFileName) throws IOException {
        tupleSize = DEFAULT_TUPLE_SIZE;
        synsMap = new HashMap<>();
        groupSyns(synsFileName);
    }

    PlagiarismDetector(String synsFileName, int size) throws IOException {
        tupleSize = size;
        synsMap = new HashMap<>();
        groupSyns(synsFileName);
    }

    /* Map synonyms to the first word in each synonym group */
    public void groupSyns(String synsFileName) throws IOException {
        try (Scanner scanner = new Scanner(new File(synsFileName))) {
            while (scanner.hasNext()) {
                String[] words = scanner.nextLine().toLowerCase().split(" ");
                if (words.length == 0) {
                    continue;
                }
                for (String word : words) {
                    synsMap.put(word, words[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Generate tuple set from reference document */
    public Set<String> getTuples(String referenceFile) throws IOException {
        Set<String> tuples = new HashSet<>();
        String[] words = readFile(referenceFile);

        for (int i = 0; i <= words.length - tupleSize; i++) {
            String mappedString = getMappedString(words, i);
            tuples.add(mappedString);
        }
        return tuples;
    }

    /* Match tuples of file1 against file2 */
    public double calculateMatch(String fileToTest, String referenceFile) throws IOException {
        String[] words = readFile(fileToTest);
        Set<String> tuples = getTuples(referenceFile);
        int totalTuple = 0;
        int matchedTuple = 0;

        for (int i = 0; i <= words.length - tupleSize; i++) {
            String mappedString = getMappedString(words, i);
            if (tuples.contains(mappedString)) {
                matchedTuple++;
            }
            totalTuple++;
        }
        return (double) (matchedTuple) / (double) (totalTuple) * 100.0;
    }

    /* Map synonyms in a tuple to group representative */
    public String getMappedString(String[] words, int start) {
        StringBuilder key = new StringBuilder();

        for (int k = 0; k < tupleSize; k++) {
            String tmp = words[start + k];
            if (synsMap.containsKey(tmp)) {
                key.append(synsMap.get(tmp));
            } else {
                key.append(tmp);
            }
            key.append(" ");
        }
        return key.toString();
    }

    /* Parse words in a file into array of string */
    public String[] readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            // non-english-alphabets are treated as word separator
            return sb.toString().toLowerCase().split("[^a-zA-Z]+");
        } finally {
            br.close();
        }
    }

    public int getTupleSize() {
        return tupleSize;
    }

    public void setTupleSize(int tupleSize) {
        this.tupleSize = tupleSize;
    }

    public void setSyns(String synsFileName) throws IOException {
        synsMap = new HashMap<>();
        groupSyns(synsFileName);
    }
}
