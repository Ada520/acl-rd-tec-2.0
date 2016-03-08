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
import info.atmykitchen.objects.AnnotationFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author bq
 */
public class CheckSemanticClassType {
        public static void main(String[] ss) throws IOException, ParserConfigurationException, Exception {

        int count = 0;

        Set<String> treeSetTag = new TreeSet();
        String root = ss[0];
        List<File> bqFiles = IOMethods.getAllFilesRecursive(new File(root));
        for(File f: bqFiles){
            if(f.getName().endsWith(".xml")){
                AnnotationFile loadAnnotationFile = IOMethods.loadAnnotationFile(f);
                treeSetTag.addAll(loadAnnotationFile.getAnnotationMapPerClassCategory().keySet());
            }
        }
        
        for(String s: treeSetTag){
            System.out.println( "\""+ s + "\"" );
        }
        
      

    }
}
