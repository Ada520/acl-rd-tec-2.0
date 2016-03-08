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
package info.atmykitchen.common;



import info.atmykitchen.objects.Annotation;
import info.atmykitchen.objects.AnnotationFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import static javax.xml.transform.TransformerFactory.newInstance;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import static javax.xml.transform.TransformerFactory.newInstance;

/**
 *
 * @author Behrang Q. Zadeh
 */
public class IOMethods {

    /**
     * read the user generated raw annotation file, remember the annotaiton file
     * are still not well-formed xml
     *
     * @param xmlPath input user generated annotaiton file path
     * @return the content of annotation file in a String
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String readFile(String xmlPath) throws FileNotFoundException, IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(xmlPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    /**
     * To generate final pretty print XML file
     * @param xmlAnnotation generated DOM from annotation file
     * @param outputPath path to write generated XML files
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws IOException
     */
    public static void writeXML(Document xmlAnnotation, String outputPath) throws TransformerConfigurationException, TransformerException, IOException {
        
        
        TransformerFactory tf = newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputPath),"UTF-8");
      //  FileWriter writer = new FileWriter(outputPath);
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
        transformer.transform(new DOMSource(xmlAnnotation), new StreamResult(writer));
        //String output = writer.getBuffer().toString();
        //System.out.println(output);
    }
    
    
       /**
     * To generate final pretty print XML file
     * @param xmlAnnotation generated DOM from annotation file
     * @param outputPath path to write generated XML files
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws IOException
     */
    public static void normaliseCheckWriteXML(Document xmlAnnotation, String outputPath) throws TransformerConfigurationException, TransformerException, IOException {
//        TransformerFactory tf = newInstance();
//        Transformer transformer = tf.newTransformer();
//        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
//         transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
//        FileWriter writer = new FileWriter(outputPath);
        
         TransformerFactory tf = newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputPath),"UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
        transformer.transform(new DOMSource(xmlAnnotation), new StreamResult(writer));
        //String output = writer.getBuffer().toString();
        //System.out.println(output);
    }

    /**
     * get list of files in a given folder
     *
     * @param parentDir path to folder contains the cml files
     * @return list of file names in the specified folder
     */
    public static List<String> getAnnotationFiles(String parentDir) {
        List<String> listOfFile = new ArrayList<>();
        //FilenameFilter filter1 = new FilenameFilter() {

//            public boolean accept(File dir, String name) {
//                return !name.startsWith(".");
//            }
//        };

        File dir = new File(parentDir);
        if (dir.isDirectory()) {
            File[] dirs = dir.listFiles();
            for (File dir1 : dirs) {
                if (dir1.isFile()) {
                    listOfFile.add(dir1.getName());
                } else if (dir1.isDirectory()) {
                    out.println("Nested folder is igonored: " + dir1);
                }
            }
        }

        return listOfFile;
    }
    //private static final Logger LOG = Logger.getLogger(IOMethods.class.getName());
    
    /**
     * Method for loading annotations 
     * @param file
     * @return
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws Exception 
     */
      public static AnnotationFile loadAnnotationFile(File file) throws SAXException, ParserConfigurationException, IOException, Exception{
        Document makeDOM = XMLMethod.makeDOM(file);
        String aclid = makeDOM.getElementsByTagName("Paper").item(0).getAttributes().getNamedItem("acl-id").getTextContent();
        String title = makeDOM.getElementsByTagName("Title").item(0).getTextContent().trim();
        NodeList elementsByTagName = makeDOM.getElementsByTagName("S");
        int sentLeng = elementsByTagName.getLength();
          
        List<Annotation> annotationList = new ArrayList();
        List<String> sentenceLst = new ArrayList<String>();
        for (int i = 0; i < sentLeng; i++) {
            //int startOffset = 0;
            String sentence = new String();

            NodeList childNodes = elementsByTagName.item(i).getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                short nodeType = childNodes.item(j).getNodeType();
                if (nodeType == Node.TEXT_NODE) {
                    sentence+=(childNodes.item(j).getTextContent());
                } else if ("term".equals(childNodes.item(j).getNodeName())) {
                    String term = childNodes.item(j).getTextContent();
                    // if this is the first token and the text begins with space eliminiate it
                    // I had to use this rule and not the .trim() to make sure that the offsets remains the same across annotated files by different annotators
                    if(j==0){
                        term = term.replaceAll("^\\s+","");
                    }else if(j==childNodes.getLength()){
                        term = term.replaceAll("\\s+$","");
                    }
                    
                    Annotation annotation = new Annotation(sentence.length(), i, childNodes.item(j).getAttributes().getNamedItem("class").getTextContent(), term);
                    sentence+=(term);
                    annotationList.add(annotation);
                }else{
                    throw new Exception("Unforeseen type of node in the annotation file " + childNodes.item(j).getNodeName() );
                }
                
            }
            sentenceLst.add(sentence);
        }
        AnnotationFile af = new AnnotationFile(aclid,title, sentenceLst, annotationList);
        return af;

    }
      
      /**
       * Get all files in nested folders
       * @param root
       * @return 
       */
      
      public static List<File> getAllFilesRecursive(File root) {
        List<File> flist = new ArrayList<>();
        if (root.isFile()) {
            flist.add(root);
        } else if (root.isDirectory()) {
            for (File file : root.listFiles()) {
                if (file.isFile()) {
                    flist.add(file);
                } else {
                    List<File> allFiles = getAllFilesRecursive(file);
                    flist.addAll(allFiles);
                }
            }
        }
        return flist;

    }


}
