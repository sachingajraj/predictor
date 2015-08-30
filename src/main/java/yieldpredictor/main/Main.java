package yieldpredictor.main;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.classifier.sgd.L1;
import org.apache.mahout.classifier.sgd.ModelSerializer;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import yieldpredictor.components.YieldData;
import yieldpredictor.components.YieldDataN;
import yieldpredictor.components.YieldDataParser;

/**
 * Created by sachin.gajraj on 29/08/15.
 */
public class Main {
    public static final int NUM_CATEGORIES = 1000;

    public static void main(String[] args) throws Exception {
        YieldDataParser yParser = new YieldDataParser("apy_clean.csv");

        System.out.println("Training LR Models");
        OnlineLogisticRegression lr = new OnlineLogisticRegression(NUM_CATEGORIES, YieldDataN.FEATURES, new L1())
                .learningRate(50)
                .alpha(1)
                .lambda(0.0001)
                .stepOffset(20000)
                .decayExponent(0.2);

        YieldDataN.VectorData observation;
        int lineCount = 0;
        while ((observation = yParser.nextTrain()) != null) {
            lr.train(observation.getTarget(), observation.asVector());
            lineCount++;
            if (lineCount % 1000 == 0) {
                System.out.println("Lines done so far : " + lineCount);
            }
        }
        lr.close();

        String path = "/Users/sachin.gajraj/workspace/YieldPredictor/src/main/resources/";
        ModelSerializer.writeBinary(path + "model", lr);
        yParser.writeFeatureMap(path);
        System.out.println("Model written to file.....");

//        String path = "/Users/sachin.gajraj/workspace/YieldPredictor/src/main/resources/model";
//        YieldData observation;
//        YieldDataParser yParser;
//
//        FileSystem fs = FileSystem.get(new Configuration());
//        Path in = new Path(path);
//        OnlineLogisticRegression model = ModelSerializer.readBinary(fs.open(in), OnlineLogisticRegression.class);
//
//        double max = 0;
//        int max_i = -1;
//        //yParser = new YieldDataParser("apy_test.csv");
//        String[] lines = {"2003,KARNATAKA,BANGALORE (URBAN),Rice,Summer,427,2125,49", "2003,KARNATAKA,BANGALORE (URBAN),Ragi,Kharif,41685,74079,17"
//                , "2003,KARNATAKA,BANGALORE (URBAN),Maize,Kharif,601,1832,30", "2003,KARNATAKA,BANGALORE (URBAN),Castor seed,Kharif,399,358,8",
//                "2003,KARNATAKA,BANGALORE (URBAN),Horse-gram,Kharif,781,697,8", "2003,KARNATAKA,BANGALORE (URBAN),Jowar,Kharif,253,268,10", "2003,KARNATAKA,BANGALORE (URBAN),Rice,Kharif,2213,4667,21"};
//        for (String line : lines) {
//            observation = yParser.nextPredict(line);
//            max = 0;
//            max_i = -1;
//            for (int i = 0; i < 1000; i++) {
//                Double pred = model.classifyFull(observation.asVector()).get(i);
//                if (pred > max) {
//                    max = pred;
//                    max_i = i;
//                }
//            }
//            System.out.println(max_i + " " + max);
//            //System.out.println(model.classifyFull(observation.asVector()));
//        }
    }
}
