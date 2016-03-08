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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import net.sf.saxon.Configuration;
import net.sf.saxon.event.StreamWriterToReceiver;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import opennlp.tools.sentdetect.SentenceDetectorME;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is the code used to Select abstracts from the ACL ARC
 *
 * @author Behrang QasemiZadeh
 */
public class ManipulateAbstractFilesText {

    private static SentenceDetectorME sentenceeME;

//    public static void main(String[] sugar) throws FileNotFoundException, IOException, SAXException, ParserConfigurationException, XMLStreamException {
//        sentenceeME = new SentenceDetectorME(new SentenceModel(new File("../external_lr/en-sent.bin")));
//
//        GetFiles gf = new GetFiles();
//        gf.getCorpusFiles(rootPath + "material\\selecting-abstracts\\_all_abstr");
//        List<String> fileListAbst = gf.getFiles();
//        System.out.println("There are " + fileListAbst.size() + " raw abstracts");
//        int count = 0;
//        int breakT = 0;
//
//        for (String file : fileListAbst) {
//            String[] split = file.split("\\\\");
//            File abst = new File(file);
//            Document makeDOM = XMLMethod.makeDOM(abst);
//            int sectionLenth = makeDOM.getElementsByTagName("Section").getLength();
//            String secTitle = makeDOM.getElementsByTagName("SectionTitle").item(0).getTextContent().trim();
//            int paraLength = makeDOM.getElementsByTagName("Paragraph").getLength();
//            // only those abstract files that contain one secion are allowed
//            String uid = makeDOM.getElementsByTagName("Paper").item(0).getAttributes().getNamedItem("uid").getTextContent();
//            if (sectionLenth == 1
//                    && paraLength < 5 && paraLength > 0
//                    && //!secTitle.toLowerCase().contains("introduction") 
//                    secTitle.toLowerCase().contains("abstract")
//                    && (abst.length() / 1024) <= 2.0
//                    && (uid.startsWith("P") || uid.startsWith("C"))) {
//                writeRawAbstract(file, makeDOM);
//                count++;
//            }
//             
//        }
//
//        System.out.println("Ci " + count);
//
//    }
    private static void writeRawAbstract(String inputFile, Document xmlDom) throws FileNotFoundException, XMLStreamException, UnsupportedEncodingException, SaxonApiException {
        String strFile = inputFile.replace("_all_abstr", "sent_abs");

        File f2 = new File(strFile);

        String parent = f2.getParent();
        File pFile = new File(parent);
        if (!pFile.exists()) {
            pFile.mkdirs();

        }
//        OutputStream outputStream = new FileOutputStream(new File(strFile));
//
//        XMLOutputFactory newInstance = XMLOutputFactory.newInstance();
//        IndentingXMLStreamWriter out = new IndentingXMLStreamWriter(newInstance.createXMLStreamWriter(outputStream));
//        out.setIndentStep("  ");

        OutputStream outputStream = new FileOutputStream(new File(strFile));

        Configuration conf = new Configuration();
        conf.setValidation(false);
        conf.setXMLVersion(1);
        Processor p = new Processor(conf);
        Serializer serialize = p.newSerializer();
        serialize.setOutputProperty(Serializer.Property.METHOD, "xml");
        serialize.setOutputProperty(Serializer.Property.INDENT, "yes");
        serialize.setOutputStream(outputStream);
        StreamWriterToReceiver out = serialize.getXMLStreamWriter();
        
        Node paperTag = xmlDom.getElementsByTagName("Paper").item(0);
        String uid = paperTag.getAttributes().getNamedItem("uid").getTextContent();
        out.writeStartDocument();
        out.writeStartElement("Paper");
        out.writeAttribute("acl-id", uid);

        String title = xmlDom.getElementsByTagName("Title").item(0).getTextContent().trim();
        String secTitle = xmlDom.getElementsByTagName("SectionTitle").item(0).getTextContent().trim();
        out.writeStartElement("Title");
        out.writeCharacters(title);
        out.writeEndElement();
        out.writeStartElement("Section");
        out.writeStartElement("SectionTitle");
        out.writeCharacters(secTitle);
        out.writeEndElement();

        NodeList paraElement = xmlDom.getElementsByTagName("Paragraph");
        for (int i = 0; i < paraElement.getLength(); i++) {

            String textContent = paraElement.item(i).getTextContent();
            for (String sent : sentenceeME.sentDetect(textContent)) {
                out.writeStartElement("S");
                out.writeCharacters(sent);
                out.writeEndElement();
            }

        }
        // this is marked to remove the paragraph tags
        // out.writeEndElement();

        out.writeEndElement();
        out.writeEndDocument();

        out.close();

    }

    public static void writeRawAbstractFromAnnotated(String inputFile, String outputPath, Document xmlDom) throws FileNotFoundException, XMLStreamException, UnsupportedEncodingException, SaxonApiException {

        File f2 = new File(outputPath);

        String parent = f2.getParent();
        File pFile = new File(parent);
        if (!pFile.exists()) {
            pFile.mkdirs();

        }
//        OutputStream outputStream = new FileOutputStream(new File(outputPath));
//
//        XMLOutputFactory newInstance = XMLOutputFactory.newInstance();
//        IndentingXMLStreamWriter out = new IndentingXMLStreamWriter(newInstance.createXMLStreamWriter(outputStream));
//        out.setIndentStep("  ");

        OutputStream outputStream = new FileOutputStream(new File(outputPath));

        Configuration conf = new Configuration();
        conf.setValidation(false);
        conf.setXMLVersion(1);
        Processor p = new Processor(conf);
        Serializer serialize = p.newSerializer();
        serialize.setOutputProperty(Serializer.Property.METHOD, "xml");
        serialize.setOutputProperty(Serializer.Property.INDENT, "yes");
        serialize.setOutputStream(outputStream);
        StreamWriterToReceiver out = serialize.getXMLStreamWriter();


        Node paperTag = xmlDom.getElementsByTagName("Paper").item(0);
        String uid = paperTag.getAttributes().getNamedItem("acl-id").getTextContent();
        out.writeStartDocument();
        out.writeStartElement("Paper");
        out.writeAttribute("acl-id", uid);

        String title = xmlDom.getElementsByTagName("Title").item(0).getTextContent().trim();
        String secTitle = xmlDom.getElementsByTagName("SectionTitle").item(0).getTextContent().trim();
        out.writeStartElement("Title");
        out.writeCharacters(title.trim());
        out.writeEndElement();
        out.writeStartElement("Section");
        out.writeStartElement("SectionTitle");
        out.writeCharacters(secTitle.trim());
        out.writeEndElement();

        NodeList paraElement = xmlDom.getElementsByTagName("S");
        for (int i = 0; i < paraElement.getLength(); i++) {

            out.writeStartElement("S");
            out.writeCharacters(paraElement.item(i).getTextContent().trim());
            out.writeEndElement();

        }

        out.writeEndElement();
        out.writeEndDocument();

        out.close();

    }

}
