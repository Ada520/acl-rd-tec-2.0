/*
 * The MIT License
 *
 * Copyright 2015 Behrang QasemiZadeh <me at atmykitchen.info>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.atmykitchen.report.unithood;

import java.io.PrintWriter;

/**
 * Generate report of annotation we did so far
 * @author Behrang QasemiZadeh <me at atmykitchen.info>
 */
public class DonottouchMain {
    
    public static void main(String[] sugar) throws Exception {
        if(sugar.length!=3){
            System.out.println("Give me the right param");
        }
      //  int roundHist = 2;
        //String base ="annotation-history/3/";
        
        String fileRoot1 = //base+"ak\\";
        sugar[0] ;// "/acl_rd_tec/annotation-history/" + roundHist + "/ak/"; //H05-1012_abstr.xml";
        String fileRoot2 = 
                //base+"bq\\";//
        sugar[1];// + 3 + "\\ak\\"; //H05-1012_abstr.xml";

        AnnotatorProfile ak = new AnnotatorProfile(fileRoot1);
        AnnotatorProfile bq = new AnnotatorProfile(fileRoot2);
        
        
        
        String outputRoot = sugar[2];// "../" ;

        // bq.printAllTerms();
//        bq.writeFileAllAnnotation(outputRoot + "bq-sum-all-terms");
//        bq.writeFileAllAnnotation(outputRoot + "bq-sum-all-annotation");
//        ak.writeFileAllAnnotation(outputRoot + "ak-sum-all-terms");
//        ak.writeFileAllAnnotation(outputRoot + "ak-sum-all-annotation");
        
        PrintWriter writer = new PrintWriter( outputRoot +"_verbose-anno-perf-3-report");
//                "c:\\users\\bq\\" +//outputRoot+
//                "_verbose-anno-perf-report"
//        );
//        
        
        double computeFMScore = ak.computeFMScore( ak.getMpTermAll(), bq.getMpTermAll());
        writer.println("# Computed f-scores (overall): " + computeFMScore);
        writer.println("## F-scores in a galance (per abstract):");
        writer.println("| Paper-ID | f-score | " );
        writer.println("|:---:|:---:| " );
        for (String paperid : ak.getAnnotationStatPerFile().keySet()) {
            double fscore = ak.computeFMScore(ak.getAnnotationStatPerFile().get(paperid).getTermCountMap(),
                    bq.getAnnotationStatPerFile().get(paperid).getTermCountMap()
            );
            
            writer.println("| "+paperid + " | " + fscore+" |");
        }
        //PrintWriter writer = new PrintWriter(System.out);
        
        writer.println();
        writer.println("# Verbose Report" );
        ak.getDiffTermList(bq,writer);
        writer.println();
        writer.println("# Detailed Results Per File");
        ak.getDiffTermListPerFile(bq,writer);
        writer.close();

    }

    
    
    
    

 


}
