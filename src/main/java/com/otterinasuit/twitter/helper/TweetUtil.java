package com.otterinasuit.twitter.helper;

import org.apache.commons.lang3.StringUtils;
import twitter4j.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TweetUtil {
    // TODO: we could use this to filter common words based on a dictionary
    public static List<String> splitWords(Status tweet) {
        List<String> words = new ArrayList<>();
        words.addAll(Arrays.asList(StringUtils.split(tweet.getText().toLowerCase(), " ")));
        if(tweet.getUser().getDescription() != null) words.addAll(Arrays.asList(StringUtils.split(tweet.getUser().getDescription().toLowerCase(), " ")));
        words.forEach(String::trim);
        return words;
    }
}
