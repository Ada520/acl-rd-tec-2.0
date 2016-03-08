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
package ie.pars.aclrdtec.fileutils;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import info.atmykitchen.common.GetFiles;
import info.atmykitchen.common.XMLMethod;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author bq
 */
public class GetStatRawTextFile {
    public static void main(String[] ss) throws SAXException, ParserConfigurationException, IOException{
    
        
        String input = ss[0]; //path to the input folder

        GetFiles gf = new GetFiles();
        gf.getCorpusFiles(input);
        List<String> annotationFiles = gf.getFiles();
        System.out.println("There are " + annotationFiles.size() + " files to check!");
        TokenizerFactory<Word> newTokenizerFactory = PTBTokenizer.PTBTokenizerFactory.newTokenizerFactory();
        int sentenceNumber = 0;
        int wordSize = 0;
        for (String file : annotationFiles) {
            File f = new File(file);
            Document makeDOM = XMLMethod.makeDOM(file);
            NodeList elementsByTagName = makeDOM.getElementsByTagName("S");
            sentenceNumber+=elementsByTagName.getLength();
            for (int i = 0; i < elementsByTagName.getLength(); i++) {
               String sentence =  elementsByTagName.item(i).getTextContent();
                StringReader sr  = new StringReader(sentence);
                  Tokenizer<Word> tokenizer = newTokenizerFactory.getTokenizer(sr);
            List<Word> tokenize = tokenizer.tokenize();
                wordSize+=tokenize.size();
            }
            
        }
        System.out.println(sentenceNumber);
        System.out.println(wordSize);
    }
}
