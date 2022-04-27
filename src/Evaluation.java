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
what are qrels for?
read in a trecrun and see if the doc at whatever rank/line is relevant?
what am i running MAP on? 
 */
public class Evaluation {
    Map<Integer, String> relevanceJudgements;
    List<String> retrieved;
    List<String> relevant;

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

    private void evaluateBM25(String filename) throws FileNotFoundException {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String s;
            List<Double> recall = new ArrayList<>();
            List<Double> precision = new ArrayList<>();
            List<Double> precisionsToAvg = new ArrayList<>();
            boolean changedRecall = false;
            int numRetrieved = 0;
            Double numRelevant = 0.0;
            while ((s = br.readLine()) != null) {
                numRetrieved++;
                String[] line = s.split("\\s+");
                String docID = line[2];
                Double recallVal = numRelevant/relevant.size();
                Double precisionVal = numRelevant/numRetrieved;
                if (relevant.contains(docID)) {
                    numRelevant++;
                    recall.add(numRelevant/relevant.size());
                    precision.add(numRelevant/numRetrieved);
                    precisionsToAvg.add(numRelevant/numRetrieved);
                }
                else {
                    recall.add(recallVal);
                    precision.add(precisionVal);
                }

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        Evaluation eval = new Evaluation();
        String inputFile = args.length >= 1 ? args[0] : "qrels";
        eval.load(inputFile);

    }
}
