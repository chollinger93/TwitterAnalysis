package com.otterinasuit.twitter.machinelearning;

import org.apache.commons.lang.StringUtils;
import twitter4j.Status;

public class TweetScoring {
    private Status tweet;
    private double scoring = 0.0D;
    public TweetScoring(Status tweet) {
        this.tweet = tweet;
    }

    /**
     * Super-basic scoring model
     * If our user is not American (based on language and location), s/he is scored 0
     */
    public double getTweetScore(){
        if(!isProbablyAmerican()) return 0.0D;

        scoring += tweet.getRetweetCount();
        scoring += tweet.getFavoriteCount();

        return scoring;
    }

    public boolean isProbablyAmerican(){
        return (tweet.getText() != null && isEnglishSpeaker());
    }

    private boolean isEnglishSpeaker() {
        return (tweet.getLang() != null && tweet.getLang().toLowerCase().equals("en"));
    }

    private boolean isAmerican() {
        return (tweet.getPlace() != null && !StringUtils.isEmpty(tweet.getPlace().getCountry())
                && tweet.getPlace().getCountry().equals("United States"));
    }
}
