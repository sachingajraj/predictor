package yieldpredictor.platform;

/**
 * Created by sachin.gajraj on 30/08/15.
 */
public class PltMain {
    public static void main(String[] args) throws Exception {
        String[] lines = {"2003,KARNATAKA,BANGALORE (URBAN),Rice,Summer,427,2125,49", "2003,KARNATAKA,BANGALORE (URBAN),Ragi,Kharif,41685,74079,17"
                , "2003,KARNATAKA,BANGALORE (URBAN),Maize,Kharif,601,1832,30", "2003,KARNATAKA,BANGALORE (URBAN),Castor seed,Kharif,399,358,8",
                "2003,KARNATAKA,BANGALORE (URBAN),Horse-gram,Kharif,781,697,8", "2003,KARNATAKA,BANGALORE (URBAN),Jowar,Kharif,253,268,10",
                "2003,KARNATAKA,BANGALORE (URBAN),Rice,Kharif,2213,4667,21", "1998,HIMACHAL PRADESH,KANGRA,Potato,Kharif,122,567,46", "1998,HIMACHAL PRADESH,KANGRA,Rice,Kharif,37728,52148,13"};
        String modelPath = "/Users/sachin.gajraj/workspace/YieldPredictor/src/main/resources/model";
        String featureIndexPath = "/Users/sachin.gajraj/workspace/YieldPredictor/src/main/resources/feature_indexes_map";
        OLRModel olrModel = new OLRModel(modelPath, featureIndexPath);
        for (String line : lines) {
            System.out.println(line + " :: " + olrModel.getYield(line));
        }
    }
}
