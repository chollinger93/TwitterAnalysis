package com.otterinasuit.twitter.bolts;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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
 * Simple, heron-compatible, yet very (!) inefficient storm-hdfs implementation
 * Proof of concept!
 * TODO: merge storm-hdfs
 */
public class HdfsBolt extends BaseRichBolt {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private OutputCollector collector;

    private final String path;
    private String separator = ",";
    protected transient FileSystem fs;
    protected transient FSDataOutputStream recOutputWriter = null;
    protected transient Configuration hdfsConfig;

    public HdfsBolt(String path) {
        this.path = path;
    }

    public HdfsBolt withSeparator(String separator) {
        this.separator = separator;
        return this;
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        this.hdfsConfig = new Configuration();
        this.hdfsConfig.set("fs.hdfs.impl",
                org.apache.hadoop.hdfs.DistributedFileSystem.class.getName()
        );
        this.hdfsConfig.set("fs.file.impl",
                org.apache.hadoop.fs.LocalFileSystem.class.getName()
        );
        //this.hdfsConfig.addResource(new Path("/opt/hadoop/hadoop-2.7.3/etc/hadoop/core-site.xml"));
        this.hdfsConfig.set("fs.default.name", "hdfs://localhost:9000");

        try {
            fs = FileSystem.get(this.hdfsConfig);
            Path pathOut = new Path(path);
            recOutputWriter = fs.create(pathOut);
        } catch (IOException e) {
            collector.reportError(e);
            e.printStackTrace();
        }
    }

    @Override
    public void execute(Tuple tuple) {
        try {
            for (Object o : tuple.getValues()) {

                if (o != null) {
                    String col = (String) o;
                    logger.debug("Writing col: "+col);
                    recOutputWriter.writeChars(col);
                }
                recOutputWriter.writeChars(separator);
            }
            recOutputWriter.flush();
        } catch (IOException e) {
            collector.reportError(e);
            e.printStackTrace();
            collector.fail(tuple);
            return;
        }
        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

}
