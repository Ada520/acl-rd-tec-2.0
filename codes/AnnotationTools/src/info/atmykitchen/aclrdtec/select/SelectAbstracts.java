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
package info.atmykitchen.aclrdtec.select;

import info.atmykitchen.common.GetFiles;
import info.atmykitchen.common.XMLMethod;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import net.sf.saxon.Configuration;
import net.sf.saxon.event.StreamWriterToReceiver;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Behrang QasemiZadeh <me at atmykitchen.info>
 */
public class SelectAbstracts {

    private static SentenceDetectorME sentenceeME;

    public static void main(String[] ss) throws IOException, Exception {
        sentenceeME = new SentenceDetectorME(new SentenceModel(new File("../external_lr/en-sent.bin")));

        GetFiles gf = new GetFiles();
        int minPerYear = 5;
        int total = 500;
        gf.getCorpusFiles("_all_abstr");
        List<String> fileListAbst = gf.getFiles();
        System.out.println("There are " + fileListAbst.size() + " raw abstracts");
        int countAllAbst = fileListAbst.size();
        TreeMap<String, List<String>> yearMap = getPerYearStat(fileListAbst);

        for (String year : yearMap.keySet()) {
            Collections.shuffle(yearMap.get(year));
            int countWriten = 0;
            while (countWriten < Math.min(minPerYear, yearMap.get(year).size()) && !yearMap.get(year).isEmpty()) {
                boolean checkWriteFile = checkWriteFile(yearMap.get(year).get(countWriten));
                yearMap.get(year).remove(countWriten);
                if (checkWriteFile) {
                    countWriten++;
                }
            }

            int numPaperAtYear = (int) Math.ceil((total) * (yearMap.get(year).size() * 1.0 / countAllAbst));
            if (!yearMap.get(year).isEmpty() && numPaperAtYear > minPerYear) {
                numPaperAtYear -= minPerYear;
                while (numPaperAtYear != 0 && !yearMap.get(year).isEmpty()) {
                    boolean checkWriteFile = checkWriteFile(yearMap.get(year).get(countWriten));
                    yearMap.get(year).remove(countWriten);
                    if (checkWriteFile) {
                        numPaperAtYear--;
                    }
                }

            }
        }

    }

    private static boolean checkWriteFile(String file) throws Exception {
        File abst = new File(file);
        Document makeDOM = XMLMethod.makeDOM(abst);
        int sectionLenth = makeDOM.getElementsByTagName("Section").getLength();
        String secTitle = makeDOM.getElementsByTagName("SectionTitle").item(0).getTextContent().trim();
        int paraLength = makeDOM.getElementsByTagName("Paragraph").getLength();
        // only those abstract files that contain one secion are allowed
        String uid = makeDOM.getElementsByTagName("Paper").item(0).getAttributes().getNamedItem("uid").getTextContent();

        if (sectionLenth == 1
                && paraLength < 5 && paraLength > 0
                && //!secTitle.toLowerCase().contains("introduction") 
                //secTitle.toLowerCase().contains("abstract")
                //&& 
                abst.length() > 500
                && (abst.length()) <= 2500
                && (!uid.startsWith("W")) // || uid.startsWith("C"))
                ) {
            writeRawAbstract(file, makeDOM);
            return true;
        } else {
            return false;

        }

    }

    private static TreeMap<String, List<String>> getPerYearStat(List<String> fileListAbst) {
        TreeMap<String, List<String>> byYearMap = new TreeMap();

        for (String file : fileListAbst) {
            String year = file.split("\\\\")[file.split("\\\\").length - 1].substring(1, 3);
            //System.out.println(year);
            if (byYearMap.containsKey(year)) {
                byYearMap.get(year).add(file);
            } else {
                List<String> yearFile = new ArrayList<>();
                yearFile.add(file);
                byYearMap.put(year, yearFile);
            }

        }
        return byYearMap;
    }

    private static void writeRawAbstract(String inputFile, Document xmlDom) throws FileNotFoundException, XMLStreamException, UnsupportedEncodingException, SaxonApiException {
        String strFile = inputFile.replace("_all_abstr", "sent_abs_s");
        System.out.println(strFile);
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
