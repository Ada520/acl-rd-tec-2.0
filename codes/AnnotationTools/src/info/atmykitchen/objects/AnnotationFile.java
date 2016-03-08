/* 
 * Copyright (C) 2016 Behrang QasemiZadeh <zadeh at phil.hhu.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.atmykitchen.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.javatuples.Triplet;

/**
 *
 * @author bq
 */
public class AnnotationFile {

    private List<String> sentences;
    private List<Annotation> annotationList;
    private String aclid;
    private String title;

    public AnnotationFile(String aclid, String title, List<String> sentences, List<Annotation> annotationList) {
        this.sentences = sentences;
        this.annotationList = annotationList;
        this.aclid = aclid;
        this.title = title;
    }

    public String getAclid() {
        return aclid;
    }

    public List<Annotation> getAnnotationList() {
        return annotationList;
    }

    public List<String> getSentences() {
        return sentences;
    }

    public boolean areSentenceStringsIdentical(AnnotationFile af) {

        if (this.sentences.size() != af.sentences.size()) {
            System.out.println("Sentence length ");
            return false;
        } else {
            for (int i = 0; i < this.sentences.size(); i++) {
                if (!this.sentences.get(i).equals(af.sentences.get(i))) {
                    for (int j = 0; j < sentences.get(i).length() - 1; j++) {
                        String c = sentences.get(i).substring(j, j + 1);
                        String d = af.sentences.get(i).substring(j, j + 1);
                        if (!c.equals(d)) {
                            System.err.println( "Mismatch at " + j + " " + c + " -> " + d);
                            System.err.println( "Mismatch at " + j + " " + c + " -> " + d);
                            break;
                        }

                    }
                    System.err.println(i + " " + this.sentences.get(i));
                    System.err.println(i + " " + af.sentences.get(i));
                    return false;
                }

            }
        }
        return true;
    }

    public String getTitle() {
        return title;
    }

    public int getPublicationYear() {
        int year = Integer.parseInt(aclid.substring(1, 3));
        if (year > 10) {
            year += 1900;
        } else {
            year += 2000;
        }
        return year;
    }

    public Set<String> getTermStringsSet() {
        Set<String> termSet = new TreeSet();

        for (Annotation a : annotationList) {
            termSet.add(a.getContent());
        }
        return termSet;
    }

    public Set<String> getTermStringsAnnotationSet() {
        Set<String> termSet = new TreeSet();

        for (Annotation a : annotationList) {
            String term = a.getContent();
            String type = a.getType();
            termSet.add(term.split("\\s").length + "\t"
                    + "<term class=\"" + type + "\">" + term
                    + "</term>");
        }
        return termSet;
    }

    public Map<Integer, List<Annotation>> getAnnotationMapSentence() {
        Map<Integer, List<Annotation>> annotaitonSentMap = new TreeMap<>();
        for (Annotation a : this.annotationList) {
            int sentenceNumber = a.getSentenceNumber();
            if (annotaitonSentMap.containsKey(sentenceNumber)) {
                annotaitonSentMap.get(sentenceNumber).add(a);
            } else {
                List<Annotation> aList = new ArrayList<>();
                aList.add(a);
                annotaitonSentMap.put(sentenceNumber, aList);
            }

        }
        return annotaitonSentMap;
    }
    
   public Map<String, List<Annotation>> getAnnotationMapPerClassCategory() {
        Map<String, List<Annotation>> anntoationClassMap = new TreeMap<>();
        for (Annotation a : this.annotationList) {
            String type = a.getType();
            if (anntoationClassMap.containsKey(type)) {
                anntoationClassMap.get(type).add(a);
            } else {
                List<Annotation> aList = new ArrayList<>();
                aList.add(a);
                anntoationClassMap.put(type, aList);
            }

        }
        return anntoationClassMap;
    }
    public Map<Triplet, Annotation> getAnnotationSpanMap() throws Exception {
        Map<Triplet, Annotation> spanMap = new TreeMap<>();
        for (Annotation a : annotationList) {
            Triplet span = Triplet.with(a.getSentenceNumber(), a.getStartOffsetSentence(), a.getContent().length());
            if (spanMap.containsKey(span)) {
                throw new Exception("Currently a span can only be annotated once as only and only one type");
            } else {
                spanMap.put(span, a);
            }
        }
        return spanMap;
    }
    
      public Map<Triplet, Annotation> getAnnotationSpanMapForClass(String classType) throws Exception {
        Map<Triplet, Annotation> spanMap = new TreeMap<>();
        for (Annotation a : annotationList) {
            if (a.getType().endsWith(classType)) {
                Triplet span = Triplet.with(a.getSentenceNumber(), a.getStartOffsetSentence(), a.getContent().length());
                if (spanMap.containsKey(span)) {
                    throw new Exception("Currently a span can only be annotated once as only and only one type");
                } else {
                    spanMap.put(span, a);
                }
            }
        }
        return spanMap;
    }

    public double computeFMScoreTermBoundary(AnnotationFile referencAnnotationFile) throws Exception {

        //assume that *a* is the gold referencet, 
        int truePositives = 0; /// this means that a term is in both *a* and *b*
        // int trueNegative = 0; --> does not exist in our evaluation since only terms are annotated

        int falseNegatives = 0; /// count of terms apper in *a* but not in *b* is a false negative
        int falsePositives = 0; /// count of terms appear in *b* but not in *a* is a false positive
        Map<Triplet, Annotation> annotationSpanMapReference = referencAnnotationFile.getAnnotationSpanMap();
        // first true stuff
        for (Triplet span : annotationSpanMapReference.keySet()) {
            if (this.getAnnotationSpanMap().containsKey(span)) {
                truePositives++;
            } else {
                falseNegatives++;
            }
        }

        for (Triplet span : this.getAnnotationSpanMap().keySet()) {
            if (!annotationSpanMapReference.containsKey(span)) {
                falsePositives++;
            }
        }

        double precision = (truePositives * 1.0) / (truePositives + falsePositives);
        double recall = (truePositives * 1.0) / (truePositives + falseNegatives);
        double fscore = 0;
        if (recall + precision > 0) {
            fscore = (2 * (precision * recall)) / (precision + recall);
        }
        return fscore;

    }

    // do this per class
    public double computeFMScoreAllClassAssignment(AnnotationFile af) throws Exception {
        int truePositives = 0; /// this means that a term is in both *a* and *b*
        // int trueNegative = 0; --> does not exist in our evaluation since only terms are annotated
        int falsePositives = 0; /// count of terms appear in *b* but not in *a* is a false positive
        int falseNegatives = 0; /// count of terms apper in *a* but not in *b* is a false negative
        Map<Triplet, Annotation> annotationSpanMapReference = af.getAnnotationSpanMap();
        // first true stuff
        for (Triplet span : annotationSpanMapReference.keySet()) {
            String refType = annotationSpanMapReference.get(span).getType();
            if (this.getAnnotationSpanMap().containsKey(span)) {
                String testType = this.getAnnotationSpanMap().get(span).getType();
                if (refType.equals(testType)) {
                    // tougher mesaure that the class label must be the same
                    truePositives++;
                } else {
                    falseNegatives++;
                }
            } else {
                falseNegatives++;
            }
        }

        for (Triplet span : this.getAnnotationSpanMap().keySet()) {
            if (!annotationSpanMapReference.containsKey(span)) {
                falsePositives++;
            }
        }

        double precision = (truePositives * 1.0) / (truePositives + falsePositives);
        double recall = (truePositives * 1.0) / (truePositives + falseNegatives);
        double fscore = 0;
        if (recall + precision > 0) {
            fscore = (2 * (precision * recall)) / (precision + recall);
        }

        return fscore;

    }

  
    // do this per class
    public double computeFMScorePerClassAssignment(AnnotationFile af, String classLabel) throws Exception {
        int truePositives = 0; /// this means that a term is in both *a* and *b*
        // int trueNegative = 0; --> does not exist in our evaluation since only terms are annotated
        int falsePositives = 0; /// count of terms appear in *b* but not in *a* is a false positive
        int falseNegatives = 0; /// count of terms apper in *a* but not in *b* is a false negative

        Map<Triplet, Annotation> referenceAnnotaionMap = af.getAnnotationSpanMapForClass(classLabel);
        // first true stuff
        
       
         for (Triplet span : referenceAnnotaionMap.keySet()) {
            if (this.getAnnotationSpanMapForClass(classLabel).containsKey(span)) {
                String testType = this.getAnnotationSpanMap().get(span).getType();
                if (classLabel.equals(testType)) {
                    // tougher mesaure that the class label must be the same
                    truePositives++;
                } else {
                    //System.out.println("Ney !!! ");
                    falseNegatives++;
                }
            } else {
                falseNegatives++;
            }
        }

        for (Triplet span : this.getAnnotationSpanMapForClass(classLabel).keySet()) {
            if (!referenceAnnotaionMap.containsKey(span)) {
                falsePositives++;
            }
        }


        double precision = (truePositives * 1.0) / (truePositives + falsePositives);
        double recall = (truePositives * 1.0) / (truePositives + falseNegatives);
        double fscore = 0;

        if (recall + precision > 0) {
            fscore = (2 * (precision * recall)) / (precision + recall);
        }

        return fscore;
    }
    
    public double computeFMScoreBoundaryPerClass(AnnotationFile af, String classLabel) throws Exception {
        int truePositives = 0; /// this means that a term is in both *a* and *b*
        // int trueNegative = 0; --> does not exist in our evaluation since only terms are annotated
        int falsePositives = 0; /// count of terms appear in *b* but not in *a* is a false positive
        int falseNegatives = 0; /// count of terms apper in *a* but not in *b* is a false negative
        Map<Triplet, Annotation> referenceAnnotaionMap = af.getAnnotationSpanMapForClass(classLabel);
        // first true stuff
        
       
         for (Triplet span : referenceAnnotaionMap.keySet()) {
            if (this.getAnnotationSpanMap().containsKey(span)) {
                truePositives++;
            } else {
                falseNegatives++;
            }
        }

        for (Triplet span : this.getAnnotationSpanMapForClass(classLabel).keySet()) {
            if (!af.getAnnotationSpanMap().containsKey(span)) {
                falsePositives++;
            }
        }

        double precision = (truePositives * 1.0) / (truePositives + falsePositives);
        double recall = (truePositives * 1.0) / (truePositives + falseNegatives);
        double fscore = 0;

        if (recall + precision > 0) {
            fscore = (2 * (precision * recall)) / (precision + recall);
        }

        return fscore;

    }
    
    
   
}
