import graph.Graph;
import io.FileManager;
import matching.RISolver;

//TEMPORALRI MAIN CLASS
public class TemporalRI {
    public static void main(String[] args) throws Exception {
        // Input file for target network
        String targetFile = null;
        // Input file for query network
        String queryFile = null;
        // Output file for occurrences found
        String occFile = null;
        // Should the networks be undirected?
        boolean directed = true;
        // Print occurrences found to standard output?
        boolean dump = false;
        // Maximum allowed difference between timestamps for the retrieved occurrences
        int delta = Integer.MAX_VALUE;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-t" -> targetFile = args[++i];
                case "-q" -> queryFile = args[++i];
                case "-d" -> delta = Integer.parseInt(args[++i]);
                case "-u" -> directed = false;
                case "-o" -> {
                    dump = true;
                    occFile = args[++i];
                }
                default -> {
                    System.out.println("Error! Unrecognizable command '" + args[i] + "'");
                    printHelp();
                    System.exit(1);
                }
            }
        }

        // Error in case network file is missing
        if (targetFile == null) {
            System.out.println("Error! No target file has been specified!\n");
            printHelp();
            System.exit(1);
        }
        if (queryFile == null) {
            System.out.println("Error! No query file has been specified!\n");
            printHelp();
            System.exit(1);
        }

        FileManager fm = new FileManager();
        System.out.println("\n-----------------------------------------------");
        System.out.println("Reading query graph " + queryFile + "...");
        Graph query = fm.readGraph(queryFile, null, directed);

        double inizio = System.currentTimeMillis();
        System.out.println("Reading target graph " + targetFile + "...");
        Graph target = fm.readGraph(targetFile, query, directed);
        // System.out.println(target.getNumEdges()+"\n");
        double fine = System.currentTimeMillis();
        double totalTimeReading = (fine - inizio) / 1000;
        inizio = System.currentTimeMillis();
        System.out.println("Matching query to target...");
        RISolver ri = new RISolver(target, query, dump, occFile, fm);
        if (dump) {
            System.out.println("\nOCCURRENCES FOUND:\n");
            System.out.println("Nodes\tEdges");
        }
        long numOccs = ri.match(delta);
        fine = System.currentTimeMillis();
        double totalTimeMatching = (fine - inizio) / 1000;
        System.out.println("\nDone! Occurrences found: " + numOccs + "\n");
        System.out.println("Time for reading: " + totalTimeReading + " secs");
        System.out.println("Time for matching: " + totalTimeMatching + " secs");
        System.out.println("Total time: " + (totalTimeReading + totalTimeMatching) + " secs");
        System.out.println();

    }

    public static void printHelp() {
        String help = "Usage: java -jar TemporalRI.jar -t <targetFile> -q <queryFile> " +
                "[-d <deltaThresh> -u -o <dumpOccFile>] \n\n";
        help += "REQUIRED PARAMETERS:\n";
        help += "-t\tTarget network file\n";
        help += "-q\tQuery network file\n\n";
        help += "OPTIONAL PARAMETERS:\n";
        help += "-d\tDelta threshold for time window of events (by default delta is infinite)\n";
        help += "-u\tTreat target and query as undirected (by default networks are directed)\n";
        help += "-o\tSave query occurrences to specified output file (by default do not save, just count)\n\n";
        System.out.println(help);
    }
}
