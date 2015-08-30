package yieldpredictor.main;

import com.google.common.io.Resources;

import java.io.*;

/**
 * Created by sachin.gajraj on 29/08/15.
 */
public class Test {
    public static void main(String[] args) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(Resources.getResource("apy.csv").openStream()));
        File file = new File("/Users/sachin.gajraj/workspace/YieldPredictor/src/main/resources/apy_clean.csv");
        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        String line = input.readLine();

        Double max = 100d;
        Double min = 0d;

        while ((line = input.readLine()) != null) {
            //System.out.println(line);
            String[] values = line.split(",");


            if (values.length < 7) {
                //System.out.println(line);
                continue;
            }

            if (values[4].equals("Whole year")) {
                System.out.println("Whole Year" + line);
                continue;
            }

            Double area = Double.parseDouble(values[values.length - 2]);
            Double production = Double.parseDouble((values[values.length - 1]));
            Double pred = production / area;

            if (pred > 100) {
                System.out.println(">100" + line);
                continue;
            }
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (i = 0; i < values.length - 1; i++) {
                String str = values[i];
                sb.append(str.trim() + ",");
            }
            sb.append(values[i] + ",");

            Double npred = pred / max * 1000d;
            int pv = npred.intValue();
            if (pv >= 1)
                System.out.println(pv);
            sb.append(pv + "\n");
            bw.write(sb.toString());
        }

        System.out.println(min + "  " + max);
        bw.close();
        input.close();
    }
}
