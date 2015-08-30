package yieldpredictor.components;

import com.google.common.base.Splitter;
import com.google.common.io.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by sachin.gajraj on 29/08/15.
 */
public class YieldDataParser {
    private final Splitter onTab = Splitter.on("\t");
    private final Splitter onComma = Splitter.on(",");
    private BufferedReader input;
    private Iterable<String> fieldNames;
    private YieldDataN yieldDataN;

    public YieldDataParser(String resourceName) throws IOException {
        input = new BufferedReader(new InputStreamReader(Resources.getResource(resourceName).openStream()));
        fieldNames = onComma.split(input.readLine());
        yieldDataN = new YieldDataN();
    }

    public YieldData next() throws IOException {
        String line = input.readLine();
        if (line == null) return null;

        return new YieldData(fieldNames, onComma.split(line));
    }

    public YieldDataN.VectorData nextTrain() throws IOException {
        String line = input.readLine();
        if (line == null) return null;

        return yieldDataN.createVector(fieldNames, onComma.split(line), true);
    }

    public YieldDataN.VectorData nextPredict(String line) throws IOException {

        if (line == null) return null;

        return yieldDataN.createVector(fieldNames, onComma.split(line), false);
    }

    public void writeFeatureMap(String path) throws IOException {
        yieldDataN.writeFeatureMap(path);
    }
}
