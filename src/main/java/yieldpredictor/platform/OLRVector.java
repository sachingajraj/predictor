package yieldpredictor.platform;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.vectorizer.encoders.ConstantValueEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sachin.gajraj on 30/08/15.
 */
public class OLRVector {
    public static final int FEATURES = 5000;
    private HashMap<String, Integer> featureVectorIndexMap = new HashMap<String, Integer>();
    private int mapIndex = 0;
    private static final ConstantValueEncoder interceptEncoder = new ConstantValueEncoder("intercept");
    private Map<String, String> fields = Maps.newLinkedHashMap();
    private final Splitter onComma = Splitter.on(",");

    public static class VectorData {
        private Vector vector;
        private int click;

        public Vector asVector() {
            return vector;
        }

        public int getTarget() {
            return click;
        }
    }

    public OLRVector(String featureIndexPath) throws IOException {
        FileSystem fs = FileSystem.get(new Configuration());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fs.open(new Path(featureIndexPath))));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String token[] = line.split("\t");
            featureVectorIndexMap.put(token[0], Integer.parseInt(token[1]));
        }
    }

    public VectorData createVector(String fieldNames, String line) {
        Iterable<String> fields = onComma.split(fieldNames);
        Iterable<String> values = onComma.split(line);
        return createVector(fields, values, false);
    }

    private VectorData createVector(Iterable<String> fieldNames, Iterable<String> values, boolean isTrain) {
        RandomAccessSparseVector vector = new RandomAccessSparseVector(FEATURES);
        Iterator<String> value = values.iterator();
        interceptEncoder.addToVector("1", vector);
        String key;
        for (String name : fieldNames) {
            String fieldValue = value.next();
            fields.put(name, fieldValue);

            switch (name) {
                case "Rainfall":
                    double v = Double.parseDouble(fieldValue);
                    key = name + ":" + v;
                    if (featureVectorIndexMap.containsKey(key)) {
                        vector.set(featureVectorIndexMap.get(key), v);
                    } else if (isTrain) { // add to map only while creating training vector
                        vector.set(mapIndex, 1);
                        featureVectorIndexMap.put(key, mapIndex);
                        mapIndex++;
                    }
                    break;
                case "State":
                case "District":
                case "Season":
                case "Crop":
                case "SoilType":
                    key = name + ":" + fieldValue;
                    if (featureVectorIndexMap.containsKey(key)) {
                        vector.set(featureVectorIndexMap.get(key), 1);
                    } else if (isTrain) { // add to map only while creating training vector
                        vector.set(mapIndex, 1);
                        featureVectorIndexMap.put(key, mapIndex);
                        mapIndex++;
                    }
                    break;
                case "Year":
                case "Area":
                case "Production":
                case "NPV":
                    // ignore these for vectorizing
                    // Yield = Area/Production
                    break;

                default:
                    throw new IllegalArgumentException(String.format("Bad field name: %s", name));
            }
        }
        VectorData vectorData = new VectorData();
        vectorData.click = getTarget();
        vectorData.vector = vector;
        return vectorData;
    }

    private int getTarget() {
        Double area = Double.parseDouble(fields.get("Area"));
        Double production = Double.parseDouble(fields.get("Production"));
        int yield = (int) (production / area);
        return Integer.parseInt(fields.get("NPV"));
    }
}
