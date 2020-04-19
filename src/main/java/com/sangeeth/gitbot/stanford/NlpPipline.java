package com.sangeeth.gitbot.stanford;


import com.sangeeth.gitbot.core.ReadPropertyFile;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

import static com.sangeeth.gitbot.util.Constants.STANFORD_NLP_ANNOTATE;

/**
 * @author dtsangeeth
 * @created 19 / 04 / 2020
 * @project GitComponentManager
 */
public class NlpPipline {

    private StanfordCoreNLP stanfordCoreNLP;
    private Properties properties;

    private NlpPipline(){

        String pro = ReadPropertyFile.getInstance().config().getString(STANFORD_NLP_ANNOTATE);

        if(stanfordCoreNLP == null){
            properties = new Properties();
            properties.setProperty("annotators" , ReadPropertyFile.getInstance().config().getString(STANFORD_NLP_ANNOTATE));
            stanfordCoreNLP = new StanfordCoreNLP(properties);
        }

    }


    public StanfordCoreNLP standford(){
        return stanfordCoreNLP;
    }

    public static NlpPipline getInstance(){
        return NlpPipline.Holder.INSTANCE;
    }

    private static class Holder{
        private static final NlpPipline INSTANCE = new NlpPipline();
    }
}
