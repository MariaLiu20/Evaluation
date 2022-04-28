import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
am i reading in each trecrun and that's when i compute all those values?
- yes
what are qrels for?
- gives all relevant docs
first k RELEVANT, not first k RETRIEVED
 */
public class Evaluation {
    Map<Integer, String> relevanceJudgements;
    List<String> retrieved;
    List<String> relevant;
    int NUM_QUERIES = 400;

    public Evaluation() {
        relevanceJudgements = new HashMap<>();
        retrieved = new ArrayList<>();
        relevant = new ArrayList<>();
    }

    private void load(String filename) throws FileNotFoundException {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String s;
            while ((s = br.readLine()) != null) {
                String[] line = s.split("\\s+");
                String queryID = line[0];
                String docID = line[2];
                int relevance = Integer.parseInt(line[3]);
                if (relevance > 0)
                    relevant.add(docID);
                retrieved.add(docID);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void evaluate(String filename, int k) throws FileNotFoundException {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String s;
            List<String> queries = new ArrayList<>();
            List<Double> recall = new ArrayList<>();
            List<Double> precision = new ArrayList<>();
            List<Double> precisionsToAvg = new ArrayList<>();
            Map<String, Double> apMap = new HashMap<>();
            Double allQueryPrecisions = 0.0;
            int numRetrieved = 0;
            Double numRelevant = 0.0;
            while ((s = br.readLine()) != null) {
                String[] line = s.split("\\s+");
                String queryID = line[0];
                String docID = line[2];
                numRetrieved++;
                // If new query
                if (!queries.contains(queryID)) {
                    queries.add(queryID);
                    // If not the first query
                    if (numRetrieved > 0) {
                        Double ap = getAvgPrecision(precisionsToAvg);
                        apMap.put(queryID, ap);
                        allQueryPrecisions += ap;
                        precisionsToAvg.clear();
                        numRetrieved = 0;
                        numRelevant = 0.0;
                    }
                }
                if (relevant.contains(docID)) {
                    numRelevant++;
                    recall.add(numRelevant / relevant.size());
                    precision.add(numRelevant / numRetrieved);
                    precisionsToAvg.add(numRelevant / numRetrieved);
                } else {
                    recall.add(numRelevant / relevant.size());
                    precision.add(numRelevant / numRetrieved);
                }
            }
            for (int rank = 1; rank <= k; rank++) {

            }

            Double map = allQueryPrecisions / NUM_QUERIES;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Double getAvgPrecision(List<Double> precisionsToAvg) {
        Double sum = 0.0;
        for (Double p : precisionsToAvg)
            sum += p;
        return sum;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Evaluation eval = new Evaluation();
        String inputFile = args.length >= 1 ? args[0] : "qrels";
        eval.load(inputFile);

    }
}
