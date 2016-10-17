package com.otterinasuit.twitter.interfaces;

import com.otterinasuit.twitter.objects.Tweet;

public interface ITweetScoring {

    double getTweetScore(Tweet tweet);

    boolean fitsCountryCriteria(Tweet tweet);

    boolean fitsDemographicCriteria(Tweet tweet);

}
