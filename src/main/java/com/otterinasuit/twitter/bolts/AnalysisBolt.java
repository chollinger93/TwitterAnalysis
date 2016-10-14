package com.otterinasuit.twitter.bolts;

import com.otterinasuit.twitter.machinelearning.PrototypeAnalysis;
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
import twitter4j.Status;

import java.util.Date;
import java.util.Map;

public class AnalysisBolt extends BaseRichBolt{
    private final String configPath;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private OutputCollector collector;

    public AnalysisBolt(String configPath){
        this.configPath = configPath;
    }

    /**
     * Read prototype words
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
        Status tweet = (Status) tuple.getValue(0);

        TweetResult result = new PrototypeAnalysis(tweet, configPath).election();
        if(result != null) {
            logger.info(result.toString());
            collector.emit(new Values(
                    result.getTweet().getUser().getId()+new Date().getTime(),
                    result.getParty().toString(),
                    Double.toString(result.getDemocratsScore()),
                    Double.toString(result.getRepulicanScore()),
                    Double.toString(result.getScoring()),
                    result.getTweet().getText())).toString();
        }
        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("key",
                "party",
                "democratsScore",
                "republicansScore",
                "scoring",
                "tweet"));
    }


}
