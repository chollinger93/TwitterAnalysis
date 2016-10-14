package com.otterinasuit.twitter.spouts;

import com.otterinasuit.twitter.helper.PropertyHelper;
import org.apache.storm.Config;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

public class TwitterSpout implements IRichSpout {
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    private final String configPath;
    //Queue for tweets
    private LinkedBlockingQueue<Status> queue;
    //stream of tweets
    private TwitterStream twitterStream;

    private SpoutOutputCollector collector;

    public TwitterSpout(String configPath){
        this.configPath = configPath;
    }

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        logger.info("Loading config: "+configPath);
        Properties properties = PropertyHelper.readConfig(configPath);
        configurationBuilder.setOAuthConsumerKey(properties.getProperty("consumerKey"))
                .setOAuthConsumerSecret(properties.getProperty("consumerSecret"))
                .setOAuthAccessToken(properties.getProperty("accessToken"))
                .setOAuthAccessTokenSecret(properties.getProperty("accessTokenSecret"));

        twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();

        //Create the queue
        this.queue = new LinkedBlockingQueue<>();

        this.collector = spoutOutputCollector;

        final StatusListener listener = new StatusListener() {

            @Override
            public void onStatus(Status status) {
                queue.offer(status);
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice sdn) {
            }

            @Override
            public void onTrackLimitationNotice(int i) {
            }

            @Override
            public void onScrubGeo(long l, long l1) {
            }

            @Override
            public void onException(Exception e) {
            }

            @Override
            public void onStallWarning(StallWarning warning) {
            }
        };

        twitterStream.addListener(listener);

        //Create a filter for the topics we want
        //to find trends for
        final FilterQuery query = new FilterQuery();
        //topics
        query.track("#MAGA", "#AmericaFirst", "America", "Vote", "US", "Election", "Trump", "Hillary", "Clinton");

        twitterStream.filter(query);
    }

    //Clean up the things opened in open()
    @Override
    public void close() {
        twitterStream.shutdown();
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void nextTuple() {
        final Status status = queue.poll();
        if (status == null) {
            Utils.sleep(50);
        } else {
            collector.emit(new Values(status));
        }
    }

    @Override
    public void ack(Object o) {

    }

    @Override
    public void fail(Object o) {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("tweet"));
    }

    //Get configuration
    @Override
    public Map getComponentConfiguration() {
        return new Config();
    }

}