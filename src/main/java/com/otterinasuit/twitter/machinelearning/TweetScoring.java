package com.otterinasuit.twitter.machinelearning;

import com.otterinasuit.twitter.interfaces.ITweetScoring;
import com.otterinasuit.twitter.objects.Tweet;
import org.apache.commons.lang.StringUtils;

public class TweetScoring implements ITweetScoring {
    private double scoring = 0.0D;

    /**
     * Super-basic scoring model
     * If our user is not American (based on language and location), s/he is scored 0
     */
    @Override
    public double getTweetScore(Tweet tweet){
        if(!fitsCountryCriteria(tweet)) return 0.0D;

        scoring += tweet.getRetweetCount();
        scoring += tweet.getFavoriteCount();

        return scoring;
    }

    @Override
    public boolean fitsCountryCriteria(Tweet tweet){
        return (tweet.getText() != null && isEnglishSpeaker(tweet));
    }

    @Override
    public boolean fitsDemographicCriteria(Tweet tweet) {
        return false;
    }

    private boolean isEnglishSpeaker(Tweet tweet) {
        return (tweet.getLang() != null && tweet.getLang().toLowerCase().equals("en"));
    }

    private boolean isAmerican(Tweet tweet) {
        return (tweet.getPlace() != null && !StringUtils.isEmpty(tweet.getPlace())
                && tweet.getPlace().equals("United States"));
    }
}
