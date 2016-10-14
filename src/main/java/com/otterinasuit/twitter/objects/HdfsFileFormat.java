package com.otterinasuit.twitter.objects;

import org.apache.storm.hdfs.bolt.format.FileNameFormat;
import org.apache.storm.task.TopologyContext;

import java.util.Map;


public class HdfsFileFormat implements FileNameFormat {
    @Override
    public void prepare(Map map, TopologyContext topologyContext) {

    }

    @Override
    public String getName(long l, long l1) {
        return "results.txt";
    }

    @Override
    public String getPath() {
        return "/results/";
    }
}
