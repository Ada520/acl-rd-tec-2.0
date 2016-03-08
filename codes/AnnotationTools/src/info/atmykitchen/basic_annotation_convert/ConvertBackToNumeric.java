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
import static info.atmykitchen.basic_annotation_convert.ConvertBackToNumerical.writeNumericalFormat;
import info.atmykitchen.common.GetFiles;
import info.atmykitchen.common.IOMethods;
import info.atmykitchen.common.XMLMethod;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import static java.lang.System.out;
import java.util.List;
import org.w3c.dom.Document;

/**
 *
 * @author bq
 */
public class ConvertBackToNumeric {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] sugar) throws Exception {

        System.out.println("Get the annotation in numeric format from the XML annotated files");
        if (sugar.length != 2) {
            out.println("Please provide path to the ");
            out.println("\ta) input folder than contains your "
                    + "xml annotations");
            out.println("\tb) output folder that you would like to save generated number-based annotation files");
            return;
        }
        // be very careful with the file names that must be replaced
        String inputpath = sugar[0];
        String outputPath = sugar[1];

        GetFiles gf = new GetFiles();
        gf.getCorpusFiles(inputpath);
        List<String> annotationFiles = gf.getFiles();
        System.out.println("There are " + annotationFiles.size() + " files to check!");

        File output = new File(outputPath);
        if (output.exists()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("The specified output path alredy exists; you may overwrite some files. Are you sure you want to continue? Press y to contine or any other key to terminate the process.");
            String s = br.readLine();
            if (!s.equals("y")) {
                System.out.println("Goodbye!");
                return;
            }
        } else {
            System.out.println("Creating the output path");
            output.mkdirs();
        }
        
        // for each file do the conversion
        for (String annotationFile : annotationFiles) {
            System.out.println("processing: " + annotationFile);
            String outputFile = annotationFile.replace(inputpath, outputPath);
            Document makeDyOM = XMLMethod.makeDOM(new File(annotationFile));
            writeNumericalFormat(outputFile, makeDyOM);
        }
        System.out.println("All files are converted.");
        System.out.println("Have a nice day ... goodbye!");

    }

}
