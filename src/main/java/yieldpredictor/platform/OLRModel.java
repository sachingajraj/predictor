package yieldpredictor.platform;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.classifier.sgd.ModelSerializer;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import yieldpredictor.components.YieldDataN;

import java.io.IOException;

/**
 * Created by sachin.gajraj on 30/08/15.
 */
public class OLRModel {
    private static final String fieldNames = "Year,State,District,Crop,Season,Area,Production,NPV";
    private OnlineLogisticRegression model;
    private OLRVector olrVector;

    public OLRModel(String modelPath, String featureIndexPath) throws IOException {
        // load model
        FileSystem fs = FileSystem.get(new Configuration());
        Path in = new Path(modelPath);
        model = ModelSerializer.readBinary(fs.open(in), OnlineLogisticRegression.class);

        // load featureMap
        olrVector = new OLRVector(featureIndexPath);
    }

    public double getYield(String line) {
        double max = 0;
        int max_i = -1;
        OLRVector.VectorData observation = olrVector.createVector(fieldNames, line);
        for (int i = 0; i < 1000; i++) {
            Double pred = model.classifyFull(observation.asVector()).get(i);
            if (pred > max) {
                max = pred;
                max_i = i;
            }
        }
        return max_i / 10d;
    }
}
