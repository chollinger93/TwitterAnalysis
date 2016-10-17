package com.otterinasuit.twitter.spouts;

import com.otterinasuit.twitter.helper.PropertyHelper;
import com.otterinasuit.twitter.helper.TweetUtil;
import com.otterinasuit.twitter.objects.Tweet;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.Config;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class TwitterSpout implements IRichSpout {
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    private final String configPath;
    //Queue for tweets
    private LinkedBlockingQueue<Status> queue;
    //stream of tweets
    private TwitterStream twitterStream;

    private SpoutOutputCollector collector;

    public TwitterSpout(String configPath) {
        this.configPath = configPath;
    }

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        ConfigurationBuilder configurationBuilder = TweetUtil.auth(configPath+"/auth.properties");

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
        String topics = PropertyHelper.getInstance(configPath+"/config.properties").getProperties()
                .getProperty("seed.keywords");
        if(StringUtils.isEmpty(topics)){
            logger.error("seed.keywords are missing, cannot init spout!");
            spoutOutputCollector.reportError(new IOException("seed.keywords missing!"));
            return;
        }
        String[] _topics = StringUtils.split(topics, ',');
        query.track(_topics);

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
            //Utils.sleep(50);
        } else {
            // Overwrite twitter4j, as it cannot by kryo'd
            Tweet tweet = new Tweet(status.getUser().getId(), status.getId(), status.getUser().getName(), status.getUser().getScreenName(), status.getText(),
                    status.getUser().getDescription(), status.getLang());
            if (status.getPlace() != null && !StringUtils.isEmpty(status.getPlace().getCountry()))
                tweet.setPlace(status.getPlace().getCountry());
            tweet.setFavorited(status.isFavorited());
            tweet.setFavoriteCount(status.getFavoriteCount());
            tweet.setRetweet(status.isRetweet());
            tweet.setRetweetCount(status.getRetweetCount());
            final Values values = new Values(tweet);
            collector.emit(values);
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