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
import java.util.TreeMap;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author bq
 */
public class CheckSentences {



    public static void main(String[] ss) throws IOException, ParserConfigurationException, Exception {

        int count = 0;

        
        List<File> bqFiles = IOMethods.getAllFilesRecursive(new File(ss[0]));
        List<File> akFiles = IOMethods.getAllFilesRecursive(new File(ss[1]));
        Map<String, AnnotationFile> akMap = new TreeMap();
        Map<String, AnnotationFile> bqMap = new TreeMap();
        
        for (File ak : akFiles) {
            AnnotationFile antFile = IOMethods.loadAnnotationFile(ak);
            akMap.put(ak.getName(), antFile);
        }

        for (File bq : bqFiles) {
            AnnotationFile antFile = IOMethods.loadAnnotationFile(bq);
            bqMap.put(bq.getName(), antFile);
        }
        

      
      
        for (String f : bqMap.keySet()) {
            System.err.println(f);
            if (akMap.containsKey(f)) {
                //System.out.println("Both");
                AnnotationFile getAK = akMap.get(f);
                AnnotationFile getBQ = bqMap.get(f);
                if (!getAK.areSentenceStringsIdentical(getBQ)) {
                    System.err.println("Error in sentence matching " + getAK.getAclid());
                }else{
                  //  System.out.println("ok");
                }
            }else{
                System.out.println("Not a common file: " + f);
            }

            
            //uncomment this
          // System.out.println(distinicFile.get(f).replace("-", "&#8209;") + rest);
        }

      

    }

}
