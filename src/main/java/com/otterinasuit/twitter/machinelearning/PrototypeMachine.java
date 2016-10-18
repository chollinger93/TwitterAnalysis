package com.otterinasuit.twitter.machinelearning;

import com.otterinasuit.twitter.helper.PropertyHelper;
import com.otterinasuit.twitter.helper.TweetUtil;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Named so because it sounds cool
 * Lazy init via Singleton for every JVM - safes resources on getting the prototype tweets!
 * TODO: enable more than two candidates
 */
public class PrototypeMachine implements Serializable {
    private static PrototypeMachine _instance;
    private AtomicBoolean semaphore = new AtomicBoolean(false);
    private Map<String, Double> countMapPrototype2;
    private Map<String, Double> countMapPrototype1;

    private PrototypeMachine(String authPath) {
        // Wordcount from twitter to prepare the model
        try {
            ConfigurationBuilder configurationBuilder = TweetUtil.auth(authPath + "/auth.properties");
            Twitter twitter = new TwitterFactory(configurationBuilder.build()).getInstance();
            Paging paging = new Paging(1, 400);
            // Init seed users
            Properties prop = PropertyHelper.getInstance(authPath + "/config.properties").getProperties();
            List<Status> user1Status = twitter.getUserTimeline(prop.getProperty("seed.account1"), paging);
            List<Status> user2Status = twitter.getUserTimeline(prop.getProperty("seed.account"), paging);

            Prototypical proto = new Prototypical();

            this.setCountMapPrototype2(proto.getAnalysisForTweets(user2Status));
            this.setCountMapPrototype1(proto.getAnalysisForTweets(user1Status));
            this.setSemaphore(true);

        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get instance
     *
     * @param confPath to auth.properties directory
     * @return
     */
    public static synchronized PrototypeMachine getInstance(String confPath) {
        if (_instance == null)
            _instance = new PrototypeMachine(confPath);

        return _instance;
    }

    public synchronized Map<String, Double>[] getProtoypes() {
        if (getSemaphore() && !getCountMapPrototype1().isEmpty() && !getCountMapPrototype2().isEmpty()) {
            Map[] mapper = {countMapPrototype1, countMapPrototype2};
            return mapper;
        }
        return null;
    }

    /**
     * Get first prototype
     *
     * @return
     */
    public synchronized Map<String, Double> getCountMapPrototype1() {
        if (getSemaphore() && !getCountMapPrototype1_2().isEmpty()) {
            return getCountMapPrototype1_2();
        }
        return null;
    }

    /**
     * Get first prototype
     *
     * @return
     */
    public synchronized Map<String, Double> getCountMapPrototype2() {
        if (getSemaphore() && !getCountMapPrototype2_2().isEmpty()) {
            return getCountMapPrototype2_2();
        }
        return null;
    }

    private synchronized Map<String, Double> getCountMapPrototype1_2() {
        return countMapPrototype1;
    }

    private synchronized Map<String, Double> getCountMapPrototype2_2() {
        return countMapPrototype2;
    }

    public synchronized boolean getSemaphore() {
        return semaphore.get();
    }

    public synchronized void setSemaphore(boolean val) {
        this.semaphore.set(val);
    }


    public synchronized void setCountMapPrototype2(Map<String, Double> countMapPrototype2) {
        this.countMapPrototype2 = countMapPrototype2;
    }

    public synchronized void setCountMapPrototype1(Map<String, Double> countMapPrototype1) {
        this.countMapPrototype1 = countMapPrototype1;
    }
}
