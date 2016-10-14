package com.otterinasuit.twitter.machinelearning;

import com.otterinasuit.twitter.helper.MapUtil;
import org.apache.commons.lang3.StringUtils;
import twitter4j.Status;

import java.util.*;

/**
 * For reference, see Popesci. A., Pennacchiotti, M.: "A Machine Learning Approach To Twitter User Classification",
 * Yahoo! Labs, Sunnyvale, CA, USA
 * <p>
 * Excuse the shitty object orientation, I did too much Spark the last weeks
 * Also, this is WIP and only an example
 */
public class Prototypical {

    private int totalWords = 0;
    private final double factor = 10.0D;

    public Map<String, Double> getAnalysisForTweets(List<Status> list) {
        this.totalWords = 0;
        Map<String, Double> weightedMap;

        Map<String, Integer> wordCount = wordCount(list);
        wordCount = f_proto_filter(3, 200, 3, wordCount);
        totalWords = f_proto_count(wordCount);
        weightedMap = f_proto_wp(wordCount);

        return weightedMap;
    }

    /**
     * Sh*ty local wordcount for the sake of demonstration
     *
     * @return
     */
    private synchronized Map<String, Integer> wordCount(List<Status> list) {
        Map<String, Integer> countMap = new HashMap<>();
        List<String> words = new ArrayList<>();
        for (Status s : list) {
            words.addAll(Arrays.asList(StringUtils.split(s.getText(), " ")));
        }

        for (String word : words) {
            if (countMap.containsKey(word)) {
                int c = countMap.get(word);
                c++;
                countMap.put(word, c);
            } else {
                countMap.put(word, 1);
            }
        }

        countMap = MapUtil.sortByValue(countMap);
        return countMap;
    }

    /**
     * Filter by occurrence and length
     *
     * @param n         lower bound of count
     * @param k         limit of list
     * @param c         character lower bound
     * @param wordCount sorted by value map
     * @return filtered list
     */
    private synchronized Map<String, Integer> f_proto_filter(int n, int k, int c, Map<String, Integer> wordCount) {
        int x = 0;
        Map<String, Integer> _wordCount = new HashMap<>(wordCount);
        Iterator it = wordCount.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            int count = wordCount.get(key);
            if (count < n || key.length() < c || x > k) {
                _wordCount.remove(key);
            }
            x++;
        }

        return _wordCount;
    }

    private synchronized int f_proto_count(Map<String, Integer> originalWordCount) {
        totalWords = 0;
        for (String key : originalWordCount.keySet()) {
            totalWords += originalWordCount.get(key);
        }
        return totalWords;
    }

    /**
     * Relative weight per word
     *
     * @return
     */
    private synchronized Map<String, Double> f_proto_wp(Map<String, Integer> wordCount) {
        Map<String, Double> weightedMap = new HashMap<>();

        for (String key : wordCount.keySet()) {
            double u = wordCount.get(key)*factor / totalWords;
            weightedMap.put(key, u);
        }

        return weightedMap;
    }
}
