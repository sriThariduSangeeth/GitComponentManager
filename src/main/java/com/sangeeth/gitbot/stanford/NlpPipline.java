package com.sangeeth.gitbot.stanford;


import com.sangeeth.gitbot.core.ReadPropertyFile;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.*;
import java.util.Properties;

import static com.sangeeth.gitbot.util.Constants.*;

/**
 * @author dtsangeeth
 * @created 19 / 04 / 2020
 * @project GitComponentManager
 */
public class NlpPipline {

    private StanfordCoreNLP stanfordCoreNLP;
    private Properties properties;
    private List<String> keys;

    private NlpPipline(){

        keys = new ArrayList<>();
        keys.addAll(Arrays.asList(ReadPropertyFile.getInstance().config().getString(GIT_DEFECTS_KEYS).split(COMMA)));

        if(stanfordCoreNLP == null){

            properties = new Properties();
            properties.setProperty("annotators" , ReadPropertyFile.getInstance().config().getString(STANFORD_NLP_ANNOTATE));
            stanfordCoreNLP = new StanfordCoreNLP(properties);
        }

    }

    public boolean checkGitSimillorKeys (String phare){

        CoreDocument coreDocument = new CoreDocument(phare);
        stanfordCoreNLP.annotate(coreDocument);

        for (CoreLabel cl: coreDocument.tokens()) {
           if( keys.contains(cl.lemma()) ){
               return true;
           }
        }
        return false;
    }

    public StanfordCoreNLP standford(){
        return this.stanfordCoreNLP;
    }

    public static NlpPipline getInstance(){
        return NlpPipline.Holder.INSTANCE;
    }

    private static class Holder{
        private static final NlpPipline INSTANCE = new NlpPipline();
    }
}
