package com.otterinasuit.twitter.bolts;

import com.otterinasuit.twitter.objects.TweetResult;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Simple HBase bolt implementation as alternative to the hdfs bolt
 */
public class HBaseBolt extends BaseRichBolt{
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    OutputCollector collector;
    private Connection connection;
    private Table table;
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        Configuration conf = HBaseConfiguration.create();
        conf.addResource(new Path("/usr/local/opt/hbase/hbase-site.xml"));
        conf.addResource(new Path("/opt/hadoop/hadoop-2.7.3/conf/hdfs-site.xml"));
        logger.info(conf.get("hbase.zookeeper.quorum"));
        try {
            connection = ConnectionFactory.createConnection(conf);
            table = connection.getTable( TableName.valueOf("party"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void execute(Tuple tuple) {
        TweetResult result = (TweetResult) tuple.getValue(1);
        try {
            Put statement = result.getHbaseStatement();
            table.put(statement);
        } catch (IOException e) {
            collector.fail(tuple);
            e.printStackTrace();
        }
        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
