package com.otterinasuit.twitter.machinelearning;

import com.otterinasuit.twitter.helper.PropertyHelper;
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
 */
public class PrototypeMachine implements Serializable{
    private static PrototypeMachine _instance;
    private AtomicBoolean semaphore = new AtomicBoolean(false);
    private Map<String, Double> countMapHillary;
    private Map<String, Double> countMapFakeHair;

    private PrototypeMachine(String confPath){
        // Wordcount from twitter to prepare the model
        try {
            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            Properties properties = PropertyHelper.readConfig(confPath);
            configurationBuilder.setOAuthConsumerKey(properties.getProperty("consumerKey"))
                    .setOAuthConsumerSecret(properties.getProperty("consumerSecret"))
                    .setOAuthAccessToken(properties.getProperty("accessToken"))
                    .setOAuthAccessTokenSecret(properties.getProperty("accessTokenSecret"));
            Twitter twitter = new TwitterFactory(configurationBuilder.build()).getInstance();
            Paging paging = new Paging(1, 400);
            List<Status> hillaryStatuses = twitter.getUserTimeline("HillaryClinton", paging);
            List<Status> trumpStatuses = twitter.getUserTimeline("realDonaldTrump", paging);

            Prototypical proto = new Prototypical();

            this.setCountMapHillary(proto.getAnalysisForTweets(hillaryStatuses));
            this.setCountMapFakeHair(proto.getAnalysisForTweets(trumpStatuses));
            this.setSemaphore(true);

        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public static synchronized PrototypeMachine getInstance(String confPath){
        if(_instance == null)
         _instance = new PrototypeMachine(confPath);

        return _instance;
    }

    public synchronized Map<String, Double>[] getProtoypes(){
        if(getSemaphore() && !getCountMapFakeHair().isEmpty() && !getCountMapHillary().isEmpty()){
            Map[] mapper = {countMapFakeHair,countMapHillary};
            return mapper;
        }
        return null;
    }

    public synchronized Map<String, Double> getCountMapFakeHair() {
        if(getSemaphore() && !getCountMapFakeHair2().isEmpty()){
            return getCountMapFakeHair2();
        }
        return null;
    }

    public synchronized Map<String, Double> getCountMapHillary() {
        if(getSemaphore() && !getCountMapHillary2().isEmpty()){
            return getCountMapHillary2();
        }
        return null;
    }

    private synchronized Map<String, Double> getCountMapFakeHair2(){
        return countMapFakeHair;
    }

    private synchronized Map<String, Double> getCountMapHillary2(){
        return countMapHillary;
    }

    public synchronized boolean getSemaphore(){
        return semaphore.get();
    }

    public synchronized void setSemaphore(boolean val){
        this.semaphore.set(val);
    }


    public synchronized void setCountMapHillary(Map<String, Double> countMapHillary) {
        this.countMapHillary = countMapHillary;
    }

    public synchronized void setCountMapFakeHair(Map<String, Double> countMapFakeHair) {
        this.countMapFakeHair = countMapFakeHair;
    }
}
