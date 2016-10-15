package com.otterinasuit.twitter.machinelearning;

import com.otterinasuit.twitter.objects.Tweet;
import org.apache.commons.lang.StringUtils;

public class TweetScoring {
    private Tweet tweet;
    private double scoring = 0.0D;
    public TweetScoring(Tweet tweet) {
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
        return (tweet.getPlace() != null && !StringUtils.isEmpty(tweet.getPlace())
                && tweet.getPlace().equals("United States"));
    }
}
