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

import info.atmykitchen.common.IOMethods;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.List;

/**
 *
 * @author bq
 */
public class GetCopyCommonAnnotationFilesText {
    
    public static void main(String[] ss) throws IOException, Exception {

        if(ss.length!=3){
            throw new Exception("Provide input paths etc.");
        }
        int count = 0;
        String root =         ss[0]; //output
        List<File> bqFiles = IOMethods.getAllFilesRecursive(new File(ss[1])); //input for annotation files from the first annotator
        List<File> akFiles = IOMethods.getAllFilesRecursive(new File(ss[2]));//input for the second annotator
      
        for (File ak : akFiles) {
            ak.getName();
            for (File bq : bqFiles) {
                if (ak.getName().equals(bq.getName())) {
                    count++;
                    String pathOut = root +  bq.getName();
                    File fileB = new File(pathOut);
                    fileB.mkdirs();
                    Files.copy(bq.toPath(), fileB.toPath(), REPLACE_EXISTING);

                }
            }
        }
        System.out.println(count);

    }
    
}
