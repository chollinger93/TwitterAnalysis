package com.otterinasuit.twitter.helper;

import com.otterinasuit.twitter.objects.Tweet;
import org.apache.commons.lang3.StringUtils;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class TweetUtil {
    // TODO: we could use this to filter common words based on a dictionary
    public static List<String> splitWords(Tweet tweet) {
        List<String> words = new ArrayList<>();
        words.addAll(Arrays.asList(StringUtils.split(tweet.getText().toLowerCase(), " ")));
        if(tweet.getUserDescription() != null) words.addAll(Arrays.asList(StringUtils.split(tweet.getUserDescription().toLowerCase(), " ")));
        words.forEach(String::trim);
        return words;
    }

    public static ConfigurationBuilder auth(String authPath){
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        Properties properties = PropertyHelper.getInstance(authPath).getProperties();
        configurationBuilder = configurationBuilder
                .setOAuthConsumerKey(properties.getProperty("consumerKey"))
                .setOAuthConsumerSecret(properties.getProperty("consumerSecret"))
                .setOAuthAccessToken(properties.getProperty("accessToken"))
                .setOAuthAccessTokenSecret(properties.getProperty("accessTokenSecret"));
        return configurationBuilder;
    }
}
