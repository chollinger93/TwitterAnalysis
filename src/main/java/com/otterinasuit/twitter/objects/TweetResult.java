package com.otterinasuit.twitter.objects;

import com.otterinasuit.twitter.helper.PropertyHelper;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.Serializable;
import java.util.Date;

public class TweetResult implements Serializable {
    private final String party;
    private double score1 = 0.0D;
    private double score2 = 0.0D;
    private double scoring;
    private double difference;
    private Tweet tweet;
    private final String configPath;

    public TweetResult(double score1, double score2, double scoring, Tweet tweet, String configPath) {
        this.score1 = score1;
        this.score2 = score2;
        this.tweet = tweet;
        this.scoring = scoring;
        this.configPath = configPath;
        this.difference = Math.abs(score1 - score2);
        if(score1 > score2) {
            this.party = PropertyHelper.getInstance(configPath).getOptionN(1);
        } else if(score1 < score2){
            this.party = PropertyHelper.getInstance(configPath).getOptionN(2);
        }else if(difference <= .5D){
            this.party = PropertyHelper.getInstance(configPath).getOptionN(0);
        } else {
            this.party = PropertyHelper.getInstance(configPath).getOptionN(0);
        }
    }

    public String getParty() {
        return party;
    }

    public double getScore1() {
        return score1;
    }

    public void setScore1(double score1) {
        this.score1 = score1;
    }

    public double getScore2() {
        return score2;
    }

    public void setScore2(double score2) {
        this.score2 = score2;
    }

    public Tweet getTweet() {
        return tweet;
    }

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
    }

    public double getScoring() {
        return scoring;
    }

    public void setScoring(double scoring) {
        this.scoring = scoring;
    }

    @Override
    public String toString() {
        return "TweetResult{" +
                "party=" + party +
                ", score1=" + score1 +
                ", score2=" + score2 +
                ", scoring=" + scoring +
                ", difference=" + difference +
                ", tweeter=" + tweet.getUserName() +
                ", lang=" + tweet.getLang() +
                ", tweet=" + tweet.getText() +
                '}';
    }

    public Put getHbaseStatement(){
        String cf;
        if(this.party.equals(PropertyHelper.getInstance(configPath).getOptionN(1))) {
            cf = "df";
        } else if(this.party.equals(PropertyHelper.getInstance(configPath).getOptionN(2))) {
            cf = "rf";
        } else {
            cf = "uf";
        }

        return new Put(getKey())
                .addColumn(Bytes.toBytes(cf), Bytes.toBytes("party"), Bytes.toBytes(party.toString()))
                .addColumn(Bytes.toBytes(cf), Bytes.toBytes("score1"), Bytes.toBytes(score1))
                .addColumn(Bytes.toBytes(cf), Bytes.toBytes("score2"), Bytes.toBytes(score2))
                .addColumn(Bytes.toBytes(cf), Bytes.toBytes("scoring"), Bytes.toBytes(scoring))
                .addColumn(Bytes.toBytes(cf), Bytes.toBytes("difference"), Bytes.toBytes(difference))
                .addColumn(Bytes.toBytes(cf), Bytes.toBytes("tweet"), Bytes.toBytes(tweet.getText()));
    }

    private byte[] getKey(){
        return Bytes.toBytes(this.getTweet().getUserName()+"_"+new Date().getTime());
    }

}
