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

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.common.ParserGrammar;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import info.atmykitchen.common.IOMethods;

import info.atmykitchen.objects.Annotation;
import info.atmykitchen.objects.AnnotationFile;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author bq
 */
public class ConvertToVerticalSke {

    private static MaxentTagger tagger;
    private static Morphology m;

    public static void main(String[] s) throws IOException, Exception {

        init();
        String fileoutput = "all-inc-double-annotation-acl_rdtec2.vert";
        OutputStreamWriter pw = new OutputStreamWriter(new FileOutputStream(fileoutput), "UTF-8");
        PrintWriter printer = new PrintWriter(pw);
        //  AnnotationFile annotationFile = IOMethods.loadAnnotationFile();

        String annotator = "a1";

        List<File> allFiles = IOMethods.getAllFilesRecursive(new File(s[0])); // path to the directory contains annotations from the first annotator
        List<File> allFilesAK = IOMethods.getAllFilesRecursive(new File(s[1]));// path to the directory contains annotations from the second annotator
        for (File f : allFiles) {
            convertFile(f, annotator, printer);

        }
        for (File f : allFilesAK) {
            convertFile(f, "a2", printer);

        }
        System.out.println(allFiles.size());

        printer.close();
    }

    private static void convertFile(File file, String annotator, PrintWriter printer) throws ParserConfigurationException, IOException, Exception {
        System.out.println(file.getAbsolutePath());
        AnnotationFile annotationFile = IOMethods.loadAnnotationFile(file);
        Map<Integer, List<Annotation>> annotationLstMap = annotationFile.getAnnotationMapSentence();

        TokenizerFactory<Word> newTokenizerFactory = PTBTokenizer.PTBTokenizerFactory.newTokenizerFactory();
        String currentLabel = "";
        int previousEnd = 0;
        printer.println("<doc id=\"" + annotationFile.getAclid() + "\" title=\"" + annotationFile.getTitle() + "\" annotatorid=\"" + annotator + "\">");
        for (int i = 0; i < annotationFile.getSentences().size(); i++) {

            String sid = (i + 1) + "-" + annotationFile.getAclid();
            printer.println("<s id=\"" + sid + "\" annotatorid=\"" + annotator + "\">");
            String sentence = annotationFile.getSentences().get(i);
            System.out.println(sentence);
            StringReader sr = new StringReader(sentence);
            Tokenizer<Word> tokenizer = newTokenizerFactory.getTokenizer(sr);
            List<Word> tokenize = tokenizer.tokenize();
            List<TaggedWord> tagSentence = tagger.tagSentence(tokenize);
            List<Annotation> sentenceAnnotationList = new ArrayList<>();
            if (annotationLstMap.containsKey(i)) {
                sentenceAnnotationList = annotationLstMap.get(i);
            }
            System.out.println(sentenceAnnotationList.size());
            Collections.sort(sentenceAnnotationList, Annotation.sentnceOrderComp());
            List<Integer> toEnd = new ArrayList();
            for (int j = 0; j < tagSentence.size(); j++) {

                //to add <g/> gap tags
                if (j == 0) {
                    previousEnd = tagSentence.get(j).endPosition();
                } else {
                    if (previousEnd == tagSentence.get(j).beginPosition()) {
                        printer.println("<g/>");
                    }
                    previousEnd = tagSentence.get(j).endPosition();
                }
                int startoffset = tagSentence.get(j).beginPosition();

                if (!toEnd.isEmpty()) {
                    Collections.sort(toEnd);
                    while (!toEnd.isEmpty() && startoffset >= toEnd.get(0)) {
                        currentLabel = "";
                        //System.out.println("** "+toEnd.get(0));
                        printer.println("</term>");
                        toEnd.remove(0);
                    }
                }
                // this is based on the fact that currently we do not have nested annotations, 
                // while the inner annotations work assignin labels to them for ske engine is going to be a bit problamatic, the best solution is to use multivalue feature of ske but this is something to be dealt in the future
                if (!sentenceAnnotationList.isEmpty()) {

                    while (!sentenceAnnotationList.isEmpty() && sentenceAnnotationList.get(0).getStartOffsetSentence() <= startoffset) {
                        Annotation remove = sentenceAnnotationList.remove(0);
                        toEnd.add(remove.getStartOffsetSentence() + remove.getContent().length());
                        printer.println("<term class=\"" + remove.getType() + "\" id=\"" + j + "-" + sid + "\" annotatorid=\"" + annotator + "\">");
                        currentLabel = remove.getType();

                    }
                }

                printer.println(sentence.substring(tagSentence.get(j).beginPosition(), tagSentence.get(j).endPosition()) + "\t" + m.lemma(tagSentence.get(j).word(), tagSentence.get(j).tag())
                        + "\t"
                        + tagSentence.get(j).tag()
                );

            }
            printer.println("</s>");
        }
        printer.println("</doc>");
    }

    private static void init() {
        String taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
        tagger = new MaxentTagger(taggerPath);
        m = new Morphology();

    }

}
