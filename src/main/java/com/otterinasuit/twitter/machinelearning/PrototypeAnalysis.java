package com.otterinasuit.twitter.machinelearning;

import com.otterinasuit.twitter.helper.TweetUtil;
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
    private TweetScoring tweetScoring;

    public PrototypeAnalysis(Tweet tweet, String configPath){
        this.tweet = tweet;
        this.configPath = configPath;
        this.tweetScoring = new TweetScoring(tweet);
        this.scoring = tweetScoring.getTweetScore();
        this.words = TweetUtil.splitWords(tweet);
    }

    /**
     * Calculates your scoring and party
     * @return null if not american (=(), TweetResult if successful
     */
    public TweetResult election(){
        if(!tweetScoring.isProbablyAmerican()) return null;
        double republicanScore = 0.0D;
        double democratsScore = 0.0D;
        Map<String, Double> trump = PrototypeMachine.getInstance(configPath).getCountMapFakeHair();
        Map<String, Double> hillary = PrototypeMachine.getInstance(configPath).getCountMapHillary();

        for(String word : words){
            republicanScore = mapper(trump, word, republicanScore);
            democratsScore = mapper(hillary, word, democratsScore);
        }

        logger.debug("Candidate {} has republican score: {} and democrat score: {}");
        return new TweetResult(republicanScore, democratsScore, scoring, tweet);
    }

    /**
     * Crazy expensive, but for sake of demonstration
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
