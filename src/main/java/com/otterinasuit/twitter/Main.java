package com.otterinasuit.twitter;

import com.otterinasuit.twitter.bolts.AnalysisBolt;
import com.otterinasuit.twitter.bolts.HdfsBolt;
import com.otterinasuit.twitter.helper.PropertyHelper;
import com.otterinasuit.twitter.objects.Tweet;
import com.otterinasuit.twitter.objects.TweetResult;
import com.otterinasuit.twitter.spouts.TwitterSpout;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * Starting point
 */
public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Date start = new Date();
        Config conf = new Config();
        String configPath = args[0];
        configPath = (configPath.endsWith("/")) ? configPath.substring(0, configPath.length()-1) : configPath;

        if (args == null || args.length < 1 || StringUtils.isEmpty(args[0]))
            throw new IOException("Usage: TwitterAnalysis.jar path/to/auth/and/conf/properties");


        // Heron specific settings
        /*
         * TODO: These don't work properly atm
         * Storm uses the twitter4j Status ("Tweet"), but Heron cannot serialize these

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("twitter4j"))
                .setScanners(new SubTypesScanner(false)));
        Set<Class<? extends Object>> types = reflections.getSubTypesOf(Object.class);
        for (Class c : types) {
            conf.registerSerialization(c.getClass());
        }
        */
        // Register custom classes we will move over as tuples
        conf.registerSerialization(TweetResult.class);
        conf.registerSerialization(Tweet.class);

        // Create topology
        Properties prop = PropertyHelper.getInstance(configPath+"/config.properties").getProperties();
        TopologyBuilder builder = new TopologyBuilder();
        int numSpouts = Integer.parseInt(prop.getProperty("topology.spouts", "1"));
        int numAnalysis = Integer.parseInt(prop.getProperty("topology.analysis", "1"));
        int numHdfs = Integer.parseInt(prop.getProperty("topology.hdfs", "1"));
        builder.setSpout("twitterSpout",
                new TwitterSpout(configPath),
                numSpouts);
        builder.setBolt("AnalysisBolt",
                new AnalysisBolt(configPath),
                numAnalysis)
                .shuffleGrouping("twitterSpout");
        builder.setBolt("HdfsBolt",
                new HdfsBolt(prop.getProperty("hdfs.path"))
                        .withSeparator(new char[]{0x01}),
                numHdfs)
                .fieldsGrouping("AnalysisBolt", new Fields("party"));

        // Not compatible with Heron yet!
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
         */


        conf.setDebug(true);
        conf.setNumWorkers(Integer.parseInt(
                prop.getProperty("topology.workers", "1")));

        LocalCluster cluster = null;
        if (args.length > 1 && args[1].equals("debug")) {
            cluster = new LocalCluster();
            logger.info("Debug mode!");
            cluster.submitTopology("TwitterAnalysis", conf, builder.createTopology());
        } else {
            logger.info("Cluster mode!");
            StormSubmitter.submitTopology("TwitterAnalysis", conf, builder.createTopology());
        }
    }

}
