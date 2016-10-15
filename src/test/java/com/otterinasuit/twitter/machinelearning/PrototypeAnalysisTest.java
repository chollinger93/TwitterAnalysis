package com.otterinasuit.twitter.machinelearning;

import com.otterinasuit.twitter.objects.Parties;
import com.otterinasuit.twitter.objects.Tweet;
import com.otterinasuit.twitter.objects.TweetResult;
import org.junit.Test;
import twitter4j.User;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by christian on 14/10/2016.
 */
public class PrototypeAnalysisTest {
    @Test
    public void election() throws Exception {
        Tweet status = mock(Tweet.class);
        User user = mock(User.class);
        when(status.getText()).thenReturn("Make America great again! #MAGMA");
        when(user.getDescription()).thenReturn("Patriotic American");
        when(status.getUserDescription()).thenReturn("Patriotic American");

        PrototypeAnalysis proto = new PrototypeAnalysis(status, "src/main/resources/auth.properties");
        TweetResult result = proto.election();

        assert(result.getParty().equals(Parties.REPUBLICANS));
    }

}