package com.otterinasuit.twitter;

import com.otterinasuit.twitter.bolts.AnalysisBolt;
import com.otterinasuit.twitter.bolts.HdfsBolt;
import com.otterinasuit.twitter.objects.TweetResult;
import com.otterinasuit.twitter.spouts.TwitterSpout;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Date start = new Date();
        Config conf = new Config();
        String configPath;
        boolean isHeron = false;
        if (args == null || args.length < 1 || StringUtils.isEmpty(args[0]))
            throw new IOException("No config path provided!");
        configPath = args[0];
        // TODO remove
        //configPath = "/Users/christian/IdeaProjects/TwitterAnalysis/src/main/resources/auth.properties";
        logger.info("Config path: "+configPath);

        // Heron specific settings
        conf.registerSerialization(Status.class);
        conf.registerSerialization(TweetResult.class);
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("twitter4j"))
                .setScanners(new SubTypesScanner(false)));
        Set<Class<? extends Object>> types = reflections.getSubTypesOf(Object.class);
        for (Class c : types) {
            conf.registerSerialization(c.getClass());
        }

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("twitterSpout", new TwitterSpout(configPath), 2);
        builder.setBolt("AnalysisBolt", new AnalysisBolt(configPath), 5)
                .shuffleGrouping("twitterSpout");

        builder.setBolt("HdfsBolt",
                new HdfsBolt("hdfs://localhost:9000/results/results.txt", new Configuration())
                        .withSeparator("|"), 1)
                .fieldsGrouping("AnalysisBolt", new Fields("party"));

        // Not compatible with Heron!
        /**
         RecordFormat format = new DelimitedRecordFormat()
         .withFieldDelimiter("|");
         // Synchronize the filesystem after every 1000 tuples
         SyncPolicy syncPolicy = new CountSyncPolicy(1000);
         // Rotate data files when they reach 5 MB
         FileRotationPolicy rotationPolicy = new FileSizeRotationPolicy(5.0f, FileSizeRotationPolicy.Units.MB);
         // Use default, Storm-generated file names
         FileNameFormat fileNameFormat = new HdfsFileFormat();

        // Instantiate the HdfsBolt
        HdfsBolt hdfsBolt = new HdfsBolt()
                .withFsUrl("hdfs://localhost:9000")
                .withFileNameFormat(fileNameFormat)
                .withRecordFormat(format)
                .withRotationPolicy(rotationPolicy)
                .withSyncPolicy(syncPolicy);


        builder.setBolt("HdfsBolt", hdfsBolt, 2)
                .fieldsGrouping("AnalysisBolt", new Fields("party"));


         builder.setBolt("NotificationBolt", new AnalysisBolt(), 5)
         .fieldsGrouping("AnalysisBolt", new Fields("word"));

         KafkaBolt kafka = new KafkaBolt()
         .withTopicSelector(new DefaultTopicSelector("test"))
         .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper());
         builder.setBolt("kafkaWal", kafka, 5)
         .shuffleGrouping("twitterSpout");
         */
        conf.setDebug(false);
        conf.setNumWorkers(3);
        LocalCluster cluster = new LocalCluster();
        if (args.length > 2) {
            logger.info("Debug mode!");
            cluster.submitTopology("TwitterAnalysis", conf, builder.createTopology());
        } else {
            logger.info("Cluster mode!");
            StormSubmitter.submitTopology("TwitterAnalysis", conf, builder.createTopology());
        }
        if (args.length > 2) {
            Utils.sleep(30 * 1000);
            Date end = new Date();
            logger.info("Total runtime: " + (end.getTime() - start.getTime()));
            cluster.killTopology("TwitterAnalysis");
            cluster.shutdown();
        }
    }

}
