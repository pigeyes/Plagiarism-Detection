import java.io.IOException;
import java.text.DecimalFormat;

/**
 * This program calculates overall similarity of one
 * file to another according to a pre-defined synonym
 * list.
 *
 * @author  Ming-Ching Chu
 * @since   2016-11-04
 */

public class Main {
    private static final String USAGE = "USAGE: java PlagiarismDetector.java " +
            "[syns_file] [file_to_test] [reference_file] [tuple_size]";
    private static final String INVALID_TUPLE_SIZE = "Tuple size should be between 1~2147483647.";

    public static void main(String[] args) throws IOException {
        if (args.length < 3 || args.length > 4) {
            System.out.println(USAGE);
            return;
        }

        String synsFileName = args[0];
        String fileToTest = args[1];
        String referenceFile = args[2];
        int tuple_size = 0;
        if (args.length == 4) {
            try {
                tuple_size = Integer.parseInt(args[3]);
            } catch (Exception e) {
                System.out.print(USAGE);
                return;
            }
            if (tuple_size <= 0) {
                System.out.print(INVALID_TUPLE_SIZE);
                return;
            }
        }

        PlagiarismDetector detector;
        if (args.length == 4) {
            detector = new PlagiarismDetector(synsFileName);
        } else {
            detector = new PlagiarismDetector(synsFileName, tuple_size);
        }

        double res = detector.calculateMatch(fileToTest, referenceFile);
        System.out.println("Plagiarism score: " + new DecimalFormat("#.##").format(res) + " %");
    }
}
