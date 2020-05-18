package com.sangeeth.gitbot.util.client.classifier;

import com.sangeeth.gitbot.core.ConfigXmlProperty;
import de.daslaboratorium.machinelearning.classifier.Classifier;
import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author dtsangeeth
 * @created 12 / 05 / 2020
 * @project GitComponentManager
 */
public class NaiveBayesClassifier {

    private Classifier<String, String> bayes;
    private static Logger logger = LogManager.getLogger(NaiveBayesClassifier.class);
    private List<String> positiveTokens ;

    private NaiveBayesClassifier(){
        super();
        bayes = new BayesClassifier<String, String>();
        positiveTokens = new ArrayList<>();

        File file = new File(Paths.get("").toAbsolutePath().toString()+"/src/main/resources/Keywords/Exeptions.txt");

        if(file.exists()){
            try {
                Scanner myReader = new Scanner(file);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    positiveTokens.addAll(Arrays.asList(data.split(",")));
                }
                myReader.close();
            }catch (FileNotFoundException e){
                logger.error("File erro",e);
            }
        }
        bayes.learn("positive", positiveTokens);
    }

    public static NaiveBayesClassifier getInstance() {
        return NaiveBayesClassifier.Holder.INSTANCE;
    }

    private static class Holder {
        private static final NaiveBayesClassifier INSTANCE = new NaiveBayesClassifier();
    }

    public boolean checkPositiveOrNegative(String text){

        // Here is a unknown sentences to classify.
        String[] unknownText1 = text.split("\\s");


        if(bayes.classify(Arrays.asList(unknownText1)).getCategory().equalsIgnoreCase("positive")){
            return true;
        }
        // Get more detailed classification result.
        ((BayesClassifier<String, String>) bayes).classifyDetailed(
                Arrays.asList(unknownText1));
        return false;
    }

}
