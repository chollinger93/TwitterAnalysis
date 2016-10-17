package com.otterinasuit.twitter.bolts;

import com.otterinasuit.twitter.machinelearning.PrototypeAnalysis;
import com.otterinasuit.twitter.machinelearning.TweetScoring;
import com.otterinasuit.twitter.objects.Tweet;
import com.otterinasuit.twitter.objects.TweetResult;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by christian on 14/10/2016.
 */
public class HBaseBoltTest {
    @Test
    public void prepare() throws Exception {
        HBaseBolt bolt = new HBaseBolt();
        Map map = new HashMap();
        TopologyContext topologyContext = mock(TopologyContext.class);
        OutputCollector outputCollector = mock(OutputCollector.class);
        bolt.prepare(map, topologyContext, outputCollector);
    }

    @Test
    public void execute() throws Exception{
        HBaseBolt bolt = new HBaseBolt();
        Tuple tuple = mock(Tuple.class);
        Map map = new HashMap();
        TopologyContext topologyContext = mock(TopologyContext.class);
        OutputCollector outputCollector = mock(OutputCollector.class);
        bolt.prepare(map, topologyContext, outputCollector);
        // Tweet
        Tweet status = mock(Tweet.class);
        when(status.getText()).thenReturn("Make America great again! #MAGMA");
        when(status.getUserDescription()).thenReturn("Patriotic American");
        PrototypeAnalysis proto = new PrototypeAnalysis(status, "src/resources/main/auth.properties", new TweetScoring());
        when(status.getLang()).thenReturn("en");
        TweetResult result = proto.election();
        when(tuple.getValue(1)).thenReturn(result);
        bolt.execute(tuple);
    }
}