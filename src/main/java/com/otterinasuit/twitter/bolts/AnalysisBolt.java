package com.otterinasuit.twitter.bolts;

import com.otterinasuit.twitter.machinelearning.PrototypeAnalysis;
import com.otterinasuit.twitter.machinelearning.TweetScoring;
import com.otterinasuit.twitter.objects.Tweet;
import com.otterinasuit.twitter.objects.TweetResult;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Runs a very basic analysis and clustering on tweets
 * TODO: enable Apache openNLP
 */
public class AnalysisBolt extends BaseRichBolt {
    private final String configPath;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private OutputCollector collector;

    public AnalysisBolt(String configPath) {
        this.configPath = configPath;
    }

    /**
     * Read prototype words
     *
     * @param map
     * @param topologyContext
     * @param outputCollector
     */
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        Object o = tuple.getValueByField("tweet");

        Tweet tweet = (Tweet) o;

        // Run acual analysis
        TweetResult result = new PrototypeAnalysis(tweet, configPath, new TweetScoring()).election();

        // Emit as single values - this is useless big overhead, but enabled for the simple hdfs bolt
        if (result != null) {
            logger.info(result.toString());
            final Values values = new Values(
                    result.getTweet().getUserId() + "_" + result.getTweet().getId(),
                    result.getParty().toString(),
                    Double.toString(result.getScore2()),
                    Double.toString(result.getScore1()),
                    Double.toString(result.getScoring()),
                    result.getTweet().getText());
            collector.emit(values);
        }
        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(final OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("key",
                "party",
                "democratsScore",
                "republicansScore",
                "scoring",
                "tweet"));
    }


}
