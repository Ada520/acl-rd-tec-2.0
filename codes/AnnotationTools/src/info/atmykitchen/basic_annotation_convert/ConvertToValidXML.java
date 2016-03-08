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
import static info.atmykitchen.common.IOMethods.getAnnotationFiles;
import static info.atmykitchen.common.IOMethods.readFile;
import static info.atmykitchen.common.IOMethods.writeXML;
import java.io.BufferedReader;
import java.io.File;
import static java.io.File.separator;
import java.io.InputStreamReader;
import static java.lang.System.in;
import static java.lang.System.out;
import java.util.List;
import org.w3c.dom.Document;

/**
 *
 * @author Behrang Q. Zadeh
 */
public class ConvertToValidXML {
 
    /**
     * Main method for converting the annotation files
     * @param sugar input folder than contains your annotations + output folder that you would like to save generated xml files;
     * @throws Exception 
     */
    public static void main(String[] sugar) throws Exception {
        out.println("XML Conversion of annotation files");
        out.println("Remeber this program will terminate by the first annotation file that contains error");
        if(sugar.length!=2){
            out.println("Please provide path to the ");
            out.println("\ta) input folder than contains your annotations");
            out.println("\tb) output folder that you would like to save generated xml files");
            return;
        }
        if(sugar[0].trim().equalsIgnoreCase(sugar[1].trim())){
            out.println("Input and output folders cannot be the same... you do not want to overwrite your files, do you?!");
            return;
        }
        String inputpath = sugar[0];
        List<String> annotationFiles = getAnnotationFiles(inputpath);
        out.println("There are " + annotationFiles.size() + " files to check!");
        String outputPath = sugar[1];
        File output = new File(outputPath);
        if(output.exists()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            out.println("The specified output path alredy exists; you may overwrite some files. Are you sure you want to continue? Press y to contine or any other key to terminate the process.");
            String s = br.readLine();
            if(!s.equals("y")){
                out.println("Goodbye!");
                return;
            }
        }else{
            out.println("Creating the output path");
            output.mkdirs();
        }
        // for each file do the conversion
        for (String annotationFile : annotationFiles) {
            out.println("processing: " + annotationFile);
            String readFile = readFile(inputpath + separator + annotationFile);
            Document converAnnotation = converAnnotation(readFile);
            writeXML(converAnnotation, outputPath + separator + annotationFile);
        }
        out.println("All files are converted.");
        out.println("Have a nice day ... goodbye!");
         
    }

}
