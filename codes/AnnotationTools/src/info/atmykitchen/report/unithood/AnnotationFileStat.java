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

import info.atmykitchen.common.XMLMethod;
import info.atmykitchen.objects.SimpleTermAnnotation;
import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Get stat for a single XML file
 * Load annotations to proper data structures that can be used for comparison
 * Any other stat for an annotation file must go here
 * @author Behrang QasemiZadeh <me at atmykitchen.info>
 */
public class AnnotationFileStat {
    private  TreeMap<String, Integer> classCountMap;
    private  TreeMap<String, TreeSet<String>> termAnnotationMap; 
    private  TreeMap<String, Integer> termCountMap;
    private  TreeMap<SimpleTermAnnotation, Integer> simpleAnnotationMap;
    
    private  ArrayList<SimpleTermAnnotation> listSimpleAnnotation;
   // private  List<String> termList;
    private String id;
    
    public void loadAnnotations(String file) throws Exception{
        Document makeDOM = XMLMethod.makeDOM(new File(file));
        id = makeDOM.getElementsByTagName("Paper").item(0).getAttributes().getNamedItem("uid").getTextContent();
        NodeList elementsByTagName = makeDOM.getElementsByTagName("term");
        classCountMap = new TreeMap();
        termCountMap = new TreeMap();
        simpleAnnotationMap = new TreeMap<>();
        termAnnotationMap= new TreeMap<>();
        listSimpleAnnotation = new ArrayList();
       // termList = new ArrayList();
        
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            Node item = elementsByTagName.item(i);
            String classType = item.getAttributes().getNamedItem("class").getTextContent();
            String term = item.getTextContent();
            SimpleTermAnnotation simpleTermAnnotation = new SimpleTermAnnotation(classType, item.getTextContent());
            //termList.add(term);
            listSimpleAnnotation.add(simpleTermAnnotation);
            updateMap(classCountMap, classType);
            updateMap(termCountMap, term);
            updateMap(simpleAnnotationMap,simpleTermAnnotation);
            if(termAnnotationMap.containsKey(term)){
                termAnnotationMap.get(term).add(classType);
            }else{
                TreeSet<String> anno = new TreeSet<>();
                anno.add(classType);
                termAnnotationMap.put(term, anno);
            }
            
        }
    }

    /**
     * update map that contain frequencies
     * @param countMap
     * @param indexTerm 
     */
    private  <T> void updateMap(TreeMap<T, Integer> countMap, T indexTerm){
         Integer get = countMap.get(indexTerm);
         if(get==null){
             countMap.put(indexTerm,1);
         }else{
             Integer currentCount = countMap.get(indexTerm);
             countMap.put(indexTerm, ++currentCount);
         }
        
    }

    public TreeMap<String, TreeSet<String>> getTermAnnotationMap() {
        return termAnnotationMap;
    }

    
    public String getId() {
        return id;
    }

    /**
     * Get the list of annotations in order that they appear in text
     * @return 
     */
    public ArrayList<SimpleTermAnnotation> getListSimpleAnnotation() {
        return listSimpleAnnotation;
    }



    
    
    

    /**
     * Get annotated terms by class
     * @return 
     */
    public  TreeMap<String, Integer> getClassCountMap() {
        return classCountMap;
    }

    /**
     * get annotated terms by their string
     * @return 
     */
    public  TreeMap<String, Integer> getTermCountMap() {
        return termCountMap;
    }

    /**
     * get the map that contains annotations i.e., terms + their sem category
     * @return 
     */
    public TreeMap<SimpleTermAnnotation, Integer> getSimpleAnnotationMap() {
        return simpleAnnotationMap;
    }
    
    
    
    
}
