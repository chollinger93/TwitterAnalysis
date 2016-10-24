package com.otterinasuit.twitter.bolts;

import com.otterinasuit.twitter.objects.TweetResult;
import org.apache.hadoop.conf.Configuration;
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
import org.apache.storm.tuple.Fields;
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
    private OutputCollector collector;
    private Connection connection;
    private Table table;
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        Configuration conf = HBaseConfiguration.create();
        conf.set("fs.hdfs.impl",
                org.apache.hadoop.hdfs.DistributedFileSystem.class.getName()
        );
        conf.set("fs.file.impl",
                org.apache.hadoop.fs.LocalFileSystem.class.getName()
        );
        try {
            connection = ConnectionFactory.createConnection(conf);
            table = connection.getTable(TableName.valueOf("party"));
        } catch (IOException e) {
            logger.error("Error in opening HBase connection!");
            outputCollector.reportError(e);
            e.printStackTrace();
        }
    }
    @Override
    public void execute(Tuple tuple) {
        if(tuple != null) {
            logger.debug("Received HBase tuple for {}!", tuple.getValue(0));
            TweetResult result = (TweetResult) tuple.getValue(1);
            try {
                Put statement = result.getHbaseStatement();
                table.put(statement);
            } catch (IOException e) {
                logger.error("Error writing to HBase!");
                e.printStackTrace();
                collector.fail(tuple);
                return;
            }
            collector.ack(tuple);
        }else {
            logger.error("Tuple is null!");
            collector.fail(tuple);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("party",
                "tweet"));
    }
}
