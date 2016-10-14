package com.otterinasuit.twitter.bolts;

import com.otterinasuit.twitter.helper.PropertyHelper;
import com.otterinasuit.twitter.machinelearning.Prototypical;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by christian on 13/10/2016.
 */
public class AnalysisBoltTest {
    @org.junit.Test

    public void prepare() throws Exception {
        // Wordcount from twitter to prepare the model
        try {
            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            Properties properties = PropertyHelper.readConfig("src/resources/auth.properties");
            configurationBuilder.setOAuthConsumerKey(properties.getProperty("consumerKey"))
                    .setOAuthConsumerSecret(properties.getProperty("consumerSecret"))
                    .setOAuthAccessToken(properties.getProperty("accessToken"))
                    .setOAuthAccessTokenSecret(properties.getProperty("accessTokenSecret"));

            Twitter unauthenticatedTwitter = new TwitterFactory(configurationBuilder.build()).getInstance();
            Paging paging = new Paging(1, 200);
            List<Status> hillaryStatuses = unauthenticatedTwitter.getUserTimeline("HillaryClinton", paging);
            List<Status> trumpStatuses = unauthenticatedTwitter.getUserTimeline("realDonaldTrump", paging);

            Prototypical proto = new Prototypical();

            Map<String, Double> countMapFakeHair = proto.getAnalysisForTweets(trumpStatuses);
            Map<String, Double> countMapHillary = proto.getAnalysisForTweets(hillaryStatuses);


            assert !countMapFakeHair.isEmpty();
            assert !countMapHillary.isEmpty();

        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void execute() throws Exception {

    }

}