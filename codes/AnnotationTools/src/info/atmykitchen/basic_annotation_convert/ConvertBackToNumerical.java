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

import static info.atmykitchen.basic_annotation_convert.BasicConversion.converAnnotation;
import info.atmykitchen.common.IOMethods;
import info.atmykitchen.common.XMLMethod;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import java.nio.charset.StandardCharsets;
import javax.xml.stream.XMLStreamException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author bq
 */
public class ConvertBackToNumerical {

//    public static void main(String[] xx) throws Exception {
    // to test, read an xml file annotated
    // convert it to numerical and then again convert it to XML
//        System.out.println("XML Conversion of annotation files");
//        System.out.println("Remeber this program will terminate by the first annotation file that contains error");
//
//        String inputpath = "C04-1035_abstr.xml";
//        Document makeDyOM = XMLMethod.makeDOM(new File(inputpath));
//        writeNumericalFormat("test.txt", makeDyOM);
//
//        String readFile = IOMethods.readFile("c:/prj/test.txt");
//        Document converAnnotation = converAnnotation(readFile);
//        IOMethods.writeXML(converAnnotation, "test-txt-xml");
//
//    }

    public static void writeNumericalFormat(String outputPath, Document xmlDom) throws FileNotFoundException, XMLStreamException, UnsupportedEncodingException, Exception {

        File f2 = new File(outputPath);

        String parent = f2.getParent();
        System.out.println(parent);
        File pFile = new File(parent);
        if (!pFile.exists()) {
            pFile.mkdirs();
        }

        FileOutputStream fs = new FileOutputStream(new File(outputPath));
        OutputStreamWriter outStream = new OutputStreamWriter(fs, StandardCharsets.UTF_8);

        PrintWriter out = new PrintWriter(outStream, true);

        Node paperTag = xmlDom.getElementsByTagName("Paper").item(0);
        String uid = paperTag.getAttributes().getNamedItem("acl-id").getTextContent();
        String title = xmlDom.getElementsByTagName("Title").item(0).getTextContent().trim();
        out.println("<Paper acl-id=\"" + uid + "\">");
        out.println("\t<Title>" + title + "</Title>");
        out.println("\t\t<Section>");
        String secTitle = xmlDom.getElementsByTagName("SectionTitle").item(0).getTextContent().trim();
        out.println("\t\t\t<SectionTitle>" + secTitle + "</SectionTitle>");

        NodeList sentenceTags = xmlDom.getElementsByTagName("S");
        for (int i = 0; i < sentenceTags.getLength(); i++) {
            out.print("\t\t\t<S>");

            NodeList childNodesSentence = sentenceTags.item(i).getChildNodes();
            for (int j = 0; j < childNodesSentence.getLength(); j++) {
                if ("#text".equalsIgnoreCase(childNodesSentence.item(j).getNodeName())) {
                    out.print(childNodesSentence.item(j).getTextContent());
                } else if ("term".equalsIgnoreCase(childNodesSentence.item(j).getNodeName())) {
                    int type = translateTagToNumber(childNodesSentence.item(j).getAttributes().getNamedItem("class").getTextContent());
                    String term = "<" + type + ">" + childNodesSentence.item(j).getTextContent() + "</" + type + ">";
                    out.print(term);
                } else {
                    throw new Exception("Unseen node type");
                }
            }
            out.println("</S>");

        }
        out.println("\t\t</Section>");
        out.println("</Paper>");
        out.close();
    }

    /**
     * Method to convert ling-labels such as tech, model, other, etc. to numeric
     * @param tag
     * @return
     * @throws Exception 
     */
    public static int translateTagToNumber(String tag) throws Exception {

        switch (tag) {
            case "other":
                return 0;
            case "tech":
                return 1;
            case "tool":
                return 2;
            case "lr":
                return 3;
            case "lr-prod":
                return 4;
            case "model":
                return 5;
            case "measure(ment)":
                return 6;
        }
        throw new Exception("FATAL ANNOTATION ERROR -- Change in code is required ");

    }

}
