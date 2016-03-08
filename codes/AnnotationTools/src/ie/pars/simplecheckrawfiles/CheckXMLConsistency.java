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
package ie.pars.simplecheckrawfiles;

import info.atmykitchen.common.IOMethods;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import static javax.xml.parsers.DocumentBuilderFactory.newInstance;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author bq
 */
public class CheckXMLConsistency {
     public static void main(String[] ss) throws IOException, ParserConfigurationException, SAXException {

     
        DocumentBuilderFactory factory = newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        

        String root =ss[0];

        List<File> bqFiles = IOMethods.getAllFilesRecursive(new File(root));
        for (File ak : bqFiles) {
            if(ak.getName().endsWith("xml")){
                System.out.println(ak.getAbsoluteFile());
            builder.parse(ak);
            }

        }
        
     }
}
