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

import info.atmykitchen.common.GetFiles;
import info.atmykitchen.common.XMLMethod;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import net.sf.saxon.Configuration;
import net.sf.saxon.event.StreamWriterToReceiver;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This is a dummy code to Select and copy those abstracts that appear in the
 * list provided by Anne Schumann. The program is supposed to get more
 * complicated as the selection criteria for sampling the corpus is going to be
 * updated. For this moment, for the selected file, first make sure that input
 * is a valid XML file, trim the content, etc.
 *
 * @author Behrang QasemiZadeh
 */
public class RegenerateThe30PiloitAbstracts {

    private static SentenceDetectorME sentenceeME;

    public static void main(String[] sugar) throws FileNotFoundException, IOException, SAXException, ParserConfigurationException, XMLStreamException, UnsupportedEncodingException, SaxonApiException {
        String rootOut=sugar[1];
        sentenceeME = new SentenceDetectorME(new SentenceModel(new File("../external_lr/en-sent.bin")));
        GetFiles gf = new GetFiles();
        gf.getCorpusFiles(sugar[0]); //e.g.,   "_pilot_task/_pilot_annotation_task_data_raw"
        List<String> fileListAbst = gf.getFiles();
        int count = 0;
        int breakT = 0;

        for (String file : fileListAbst) {

            String[] split = file.split("\\\\");
            // if (abstractFileNames.contains(split[split.length - 1].split("_abstr")[0])) {
            File abst = new File(file);
            System.out.println(file);
            Document makeDOM = XMLMethod.makeDOM(abst);
            int sectionLenth = makeDOM.getElementsByTagName("Section").getLength();
            String secTitle = makeDOM.getElementsByTagName("SectionTitle").item(0).getTextContent().trim();
            int paraLength = makeDOM.getElementsByTagName("Paragraph").getLength();
            // only those abstract files that contain one secion are allowed
        //    String uid = makeDOM.getElementsByTagName("Paper").item(0).getAttributes().getNamedItem("uid").getTextContent();
//            if (sectionLenth == 1 // if you do not want to annotate very long abstracts ;)
//                    && paraLength < 5 && paraLength > 0
//                    && //!secTitle.toLowerCase().contains("introduction") 
//                    secTitle.toLowerCase().contains("abstract")
//                    && (abst.length() / 1024) < 3.0 
//                    && (uid.startsWith("P") || uid.startsWith("C"))
//                    ) {
//             System.out.println(file);
            writeRawAbstract(rootOut, file, makeDOM);
            count++;
            //   }

            //}
        }

        System.out.println("Ci " + count);

    }

    private static void writeRawAbstract(String rootOut, String inputFile, Document xmlDom) throws FileNotFoundException, XMLStreamException, UnsupportedEncodingException, SaxonApiException {
        String strFile = new File(inputFile).getName();
                

        File f2 = new File(rootOut + strFile);

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
            //   out.writeStartElement("Paragraph");
            String textContent = paraElement.item(i).getTextContent();
            for (String sent : sentenceeME.sentDetect(textContent)) {
                out.writeStartElement("S");
                out.writeCharacters(sent);
                out.writeEndElement();
            }
            // out.writeEndElement();
        }
        // this is marked to remove the paragraph tags
        // out.writeEndElement();

        out.writeEndElement();
        out.writeEndDocument();

        out.close();

    }

}
