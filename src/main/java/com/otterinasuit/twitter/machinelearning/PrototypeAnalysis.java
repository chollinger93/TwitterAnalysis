package com.otterinasuit.twitter.machinelearning;

import com.otterinasuit.twitter.helper.TweetUtil;
import com.otterinasuit.twitter.interfaces.ITweetScoring;
import com.otterinasuit.twitter.objects.Tweet;
import com.otterinasuit.twitter.objects.TweetResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class PrototypeAnalysis {
    private final String configPath;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Tweet tweet;
    private List<String> words;
    private double scoring;
    private ITweetScoring tweetScoring;

    public PrototypeAnalysis(Tweet tweet, String configPath, ITweetScoring tweetScoring){
        this.tweet = tweet;
        this.configPath = configPath;
        this.tweetScoring = tweetScoring;
        this.scoring = tweetScoring.getTweetScore(tweet);
        this.words = TweetUtil.splitWords(tweet);
    }

    /**
     * Calculates your scoring and party
     * @return null if not american (=(), TweetResult if successful
     */
    public TweetResult election(){
        if(!tweetScoring.fitsCountryCriteria(tweet)) return null;
        double score1 = 0.0D;
        double score2 = 0.0D;
        Map<String, Double> scoreMap1 = PrototypeMachine.getInstance(configPath).getCountMapPrototype1();
        Map<String, Double> scoreMap2 = PrototypeMachine.getInstance(configPath).getCountMapPrototype2();

        for(String word : words){
            score1 = mapper(scoreMap1, word, score1);
            score2 = mapper(scoreMap2, word, score2);
        }

        logger.debug("Candidate {} has republican score: {} and democrat score: {}");
        return new TweetResult(score1, score2, scoring, tweet, configPath+"/config.properties");
    }

    /**
     * Crazy expensive, but for sake of demonstration
     * TODO: optimize
     * @param candidate
     * @param word
     * @param score
     * @return
     */
    private double mapper(Map<String, Double> candidate, String word, double score){
        for(String key : candidate.keySet()){
            double value = candidate.get(key);
            if(StringUtils.contains(key.toLowerCase(), word.toLowerCase())){
                score += value;
            }
        }
        return score;
    }
}
