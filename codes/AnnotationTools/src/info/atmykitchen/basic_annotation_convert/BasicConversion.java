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
package info.atmykitchen.basic_annotation_convert;

import info.atmykitchen.common.XMLMethod;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import static java.lang.System.exit;
import static java.lang.System.out;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.compile;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Dummy scripts for preparing XML files
 *
 * @author Behrang QasemiZadeh
 */
public class BasicConversion {

    final private static String TAG_NAME_FOR_ANNOTATION = "term";
    final private static String ATTRIBUTE_CLASS_NAME = " class";
    final private static int MAX_TAG_NUM = 6;
    

    ////////////////////////
    final private static String END_TAG = "</" + TAG_NAME_FOR_ANNOTATION + ">";

    //final static private String endTagPattern = "(</[1-" + MAX_TAG_NUM + "]>)";
    final static private String INT_END_TAG_PATTERN = "</(\\d+)>";
    final static private String INT_START_TAG_PATTERN = "<(\\d+)>";
    final static private String XMLPatterns = "<(\\d+)>(.*?)</(\\d+)>";

    //final static Pattern END_TAG_REG = Pattern.compile(endTagPattern);
    private final static Pattern ANY_INT_START_TAG = compile(INT_START_TAG_PATTERN);
    private final static Pattern ANY_INT_END_TAG = compile(INT_END_TAG_PATTERN);
    private final static Pattern ANY_ANNOTATION = compile(XMLPatterns);

    /**
     * Check for possible errors in the annotated content Now, look for those
     * substring that look like the annotation marks to find possible errors in
     * the annotation (wrongly nested ones). this method can be extended to
     * cover more errors
     *
     * @param inputXMLString
     * @return
     * @throws Exception
     */
    private static StringBuffer doCustomisedChecking(String inputXMLString) throws Exception {
        Matcher anyOtherEndTag = ANY_ANNOTATION.matcher(inputXMLString);
        boolean isEverythingOK = true;
        StringBuffer sb = new StringBuffer();
        while (anyOtherEndTag.find()) {

            if (!anyOtherEndTag.group(1).endsWith(anyOtherEndTag.group(3))) {
                out.println("\t*Err at " + anyOtherEndTag.start() + " offset " + anyOtherEndTag.end() + ": " + anyOtherEndTag.group());
                // further check the input
                isEverythingOK = false;
            }
            // check for empty nodes
            if (anyOtherEndTag.group(2).trim().length() == 0) {
                out.println("\t*Err Empty node at " + anyOtherEndTag.start() + " offset " + anyOtherEndTag.end() + ": " + anyOtherEndTag.group());
                isEverythingOK = false;
            }
            // check for other content errors  // important this does not effect the subsequent processes // this will be catched by the XML DOM Parser
            if (!possibleErrorsInAnnottatedContent(anyOtherEndTag.group(2))) {// this is just to provide information to the annotator and does not terminate the process! 
                out.println("\t*Err possible confusion at " + anyOtherEndTag.start() + " offset " + anyOtherEndTag.end() + ": " + anyOtherEndTag.group());
            }
            int annotation = parseInt(anyOtherEndTag.group(1));
            if (annotation > MAX_TAG_NUM) {
                isEverythingOK = false;
                out.println("\t*Err undefined annotation mark at " + anyOtherEndTag.start() + " offset " + anyOtherEndTag.end() + ": " + anyOtherEndTag.group());
            } else if (isEverythingOK) {
                String replace = translateStartTag(annotation) + anyOtherEndTag.group(2) + END_TAG;
                anyOtherEndTag.appendReplacement(sb, replace);
            }

        }
        return anyOtherEndTag.appendTail(sb);
    }

    /**
     * check the content inside each node and try to pick abnormal stuff
     *
     * @param xmlAsStr
     * @return
     * @throws Exception
     */
    private static boolean possibleErrorsInAnnottatedContent(String xmlAsStr) throws Exception {
        Matcher anyStart = ANY_INT_START_TAG.matcher(xmlAsStr);
        boolean areInputAnnotationOK = true;

        while (anyStart.find()) {
            areInputAnnotationOK = false;
            break;
        }
        Matcher anyEnding = ANY_INT_END_TAG.matcher(xmlAsStr);

        while (anyEnding.find()) {
            areInputAnnotationOK = false;
            break;
        }
        return areInputAnnotationOK;

    }

    /**
     * Method for translating annotation values to proper tags, this method must
     * be changed according to the annotation guideline
     *
     * @param tagNumber
     * @return
     * @throws Exception
     */
    public static String translateStartTag(int tagNumber) throws Exception {
        if (tagNumber <= MAX_TAG_NUM) {
            switch (tagNumber) {
                case 0:
                    return "<" + TAG_NAME_FOR_ANNOTATION + ATTRIBUTE_CLASS_NAME + "=\"other\">";
                case 1:
                    return "<" + TAG_NAME_FOR_ANNOTATION + ATTRIBUTE_CLASS_NAME + "=\"tech\">";
                case 2:
                    return "<" + TAG_NAME_FOR_ANNOTATION + ATTRIBUTE_CLASS_NAME + "=\"tool\">";
                case 3:
                    return "<" + TAG_NAME_FOR_ANNOTATION + ATTRIBUTE_CLASS_NAME + "=\"lr\">";
                case 4:
                    return "<" + TAG_NAME_FOR_ANNOTATION + ATTRIBUTE_CLASS_NAME + "=\"lr-prod\">";
                case 5:
                    return "<" + TAG_NAME_FOR_ANNOTATION + ATTRIBUTE_CLASS_NAME + "=\"model\">";
                case 6:
                    return "<" + TAG_NAME_FOR_ANNOTATION + ATTRIBUTE_CLASS_NAME + "=\"measure(ment)\">";

                // extend the code from here for new classes
            }
        }
        throw new Exception("FATAL ANNOTATION ERROR -- Change in code is required ");

    }

   
    /**
     * Public method to call for checking the annotations/content/etc.
     *
     * @param xmlStr
     * @return
     * @throws Exception
     */
    public static Document converAnnotation(String xmlStr) throws Exception  {
        // check the customised 
        StringBuffer doCustomisedChecking = doCustomisedChecking(xmlStr);
        String xml = doCustomisedChecking.toString();
       
        Document makeDOM=null;
        
        try {
            makeDOM = XMLMethod.makeDOM(xml);
            
        } catch (SAXException ex) {
            out.println("** ERR: The input annotation tags are converted to XML tags but still there is a problem in your file that makes it an invalid XML file. Here is the generated XML:");
            out.println("\t"+xml);
            out.println("Pay attention to the exception message from the parser:");
            out.println(ex);
            out.println("QUIT ON FATAL EXCEPTION");
            exit(0);
            
        } catch (ParserConfigurationException | IOException ex) {
        }
        
        return makeDOM;
    }


}
