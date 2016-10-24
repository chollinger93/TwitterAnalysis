package com.otterinasuit.twitter.bolts;

import com.otterinasuit.twitter.helper.Constants;
import com.otterinasuit.twitter.helper.PropertyHelper;
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
import java.util.Properties;

/**
 * Runs a very basic analysis and clustering on tweets
 * TODO: enable Apache openNLP
 */
public class AnalysisBolt extends BaseRichBolt {
    private final String configPath;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private OutputCollector collector;
    private int numHdfs, numHbase;

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
        Properties prop = PropertyHelper.getInstance(configPath+"/config.properties").getProperties();
        numHdfs = Integer.parseInt(prop.getProperty("topology.hdfs", "0"));
        numHbase = Integer.parseInt(prop.getProperty("topology.hbase", "1"));
    }

    @Override
    public void execute(Tuple tuple) {
        Object o = tuple.getValueByField("tweet");

        Tweet tweet = (Tweet) o;

        // Run acual analysis
        TweetResult result = new PrototypeAnalysis(tweet, configPath, new TweetScoring()).election();

        // Emit as single values - this is useless big overhead, but enabled for the simple hdfs bolt
        if (result != null) {
            //logger.info(result.toString());

            logger.info("Hdfs: {} / Hbase: {}", numHdfs, numHbase);
            if (numHdfs > 0) {
                final Values values = new Values(
                        result.getTweet().getUserId() + "_" + result.getTweet().getId(),
                        result.getParty().toString(),
                        Double.toString(result.getScore2()),
                        Double.toString(result.getScore1()),
                        Double.toString(result.getScoring()),
                        result.getTweet().getText());
                collector.emit(Constants.STREAM_HDFS, values);
            }

            if(numHbase > 0) {
                final Values values2 = new Values(
                        result.getParty(),
                        result);
                logger.info("Emitting to stream {}, party: {}", Constants.STREAM_HBASE, result.getParty());
                collector.emit(Constants.STREAM_HBASE, values2);
            }

            collector.ack(tuple);
        } else {
            logger.warn("Result is null!");
            collector.fail(tuple);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream(Constants.STREAM_HBASE, new Fields("party",
                "tweet"));

        outputFieldsDeclarer.declareStream(Constants.STREAM_HDFS, new Fields("key",
                "party",
                "democratsScore",
                "republicansScore",
                "scoring",
                "tweet"));

    }
}
