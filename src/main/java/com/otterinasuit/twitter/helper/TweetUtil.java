package com.otterinasuit.twitter.helper;

import com.otterinasuit.twitter.objects.Tweet;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TweetUtil {
    // TODO: we could use this to filter common words based on a dictionary
    public static List<String> splitWords(Tweet tweet) {
        List<String> words = new ArrayList<>();
        words.addAll(Arrays.asList(StringUtils.split(tweet.getText().toLowerCase(), " ")));
        if(tweet.getUserDescription() != null) words.addAll(Arrays.asList(StringUtils.split(tweet.getUserDescription().toLowerCase(), " ")));
        words.forEach(String::trim);
        return words;
    }
}
