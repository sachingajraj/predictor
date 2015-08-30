package yieldpredictor.components;

import com.google.common.collect.Maps;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.vectorizer.encoders.ConstantValueEncoder;
import org.apache.mahout.vectorizer.encoders.FeatureVectorEncoder;
import org.apache.mahout.vectorizer.encoders.StaticWordValueEncoder;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by sachin.gajraj on 29/08/15.
 */
public class YieldData {
    public static final int FEATURES = 10000;
    private static final ConstantValueEncoder interceptEncoder = new ConstantValueEncoder("intercept");
    private static final FeatureVectorEncoder featureEncoder = new StaticWordValueEncoder("feature");

    private RandomAccessSparseVector vector;

    private Map<String, String> fields = Maps.newLinkedHashMap();

    public YieldData(Iterable<String> fieldNames, Iterable<String> values) {
        vector = new RandomAccessSparseVector(FEATURES);
        Iterator<String> value = values.iterator();
        interceptEncoder.addToVector("1", vector);
        //System.out.println("-----------START");
        for (String name : fieldNames) {
            String fieldValue = value.next();
            //System.out.println(fieldValue);
            fields.put(name, fieldValue);

            switch (name) {
                case "Rainfall":
                    double v = Double.parseDouble(fieldValue);
                    featureEncoder.addToVector(name, Math.log(v), vector);
                    break;

                case "State":
                case "District":
                case "Season":
                case "Crop":
                case "SoilType":
                    featureEncoder.addToVector(name + ":" + fieldValue, 1, vector);
                    break;

                case "Year":
                case "Area":
                case "Production":
                case "NPV":
                    // ignore these for vectorizing
                    // Yield = Area/Production
                    break;

                default:
                    System.out.println(name + " # " + fieldValue);
                    throw new IllegalArgumentException(String.format("Bad field name: %s", name));
            }
        }
        //System.out.println("-----------END");
    }

    public Vector asVector() {
        return vector;
    }

    public int getTarget() {
        Double area = Double.parseDouble(fields.get("Area"));
        Double production = Double.parseDouble(fields.get("Production"));
        int yield = (int) (production / area);
        return Integer.parseInt(fields.get("NPV"));
    }
}
