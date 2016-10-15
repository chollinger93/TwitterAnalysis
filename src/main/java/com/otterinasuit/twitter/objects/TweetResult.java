package com.otterinasuit.twitter.objects;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.Serializable;
import java.util.Date;

public class TweetResult implements Serializable {
    private Parties party;
    private double repulicanScore = 0.0D;
    private double democratsScore = 0.0D;
    private double scoring;
    private double difference;
    private Tweet tweet;

    public TweetResult(double repulicanScore, double democratsScore, double scoring, Tweet tweet) {
        this.repulicanScore = repulicanScore;
        this.democratsScore = democratsScore;
        this.tweet = tweet;
        this.scoring = scoring;
        this.difference = Math.abs(repulicanScore - democratsScore);
        if(repulicanScore > democratsScore) {
            this.party = Parties.REPUBLICANS;
        } else if(repulicanScore < democratsScore){
            this.party = Parties.DEMOCRATS;
        }else if(difference <= .5D){
            this.party = Parties.UNSURE;
        } else {
            this.party = Parties.UNSURE;
        }
    }

    public Parties getParty() {
        return party;
    }

    public void setParty(Parties party) {
        this.party = party;
    }

    public double getRepulicanScore() {
        return repulicanScore;
    }

    public void setRepulicanScore(double repulicanScore) {
        this.repulicanScore = repulicanScore;
    }

    public double getDemocratsScore() {
        return democratsScore;
    }

    public void setDemocratsScore(double democratsScore) {
        this.democratsScore = democratsScore;
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
                ", repulicanScore=" + repulicanScore +
                ", democratsScore=" + democratsScore +
                ", scoring=" + scoring +
                ", difference=" + difference +
                ", tweeter=" + tweet.getUserName() +
                ", lang=" + tweet.getLang() +
                ", tweet=" + tweet.getText() +
                '}';
    }

    public Put getHbaseStatement(){
        String cf;
        if(this.party.equals(Parties.DEMOCRATS)) {
            cf = "df";
        } else if(this.party.equals(Parties.REPUBLICANS)){
            cf = "rf";
        } else {
            cf = "uf";
        }

        return new Put(getKey())
                .addColumn(Bytes.toBytes(cf), Bytes.toBytes("party"), Bytes.toBytes(party.toString()))
                .addColumn(Bytes.toBytes(cf), Bytes.toBytes("repulicanScore"), Bytes.toBytes(repulicanScore))
                .addColumn(Bytes.toBytes(cf), Bytes.toBytes("democratsScore"), Bytes.toBytes(democratsScore))
                .addColumn(Bytes.toBytes(cf), Bytes.toBytes("scoring"), Bytes.toBytes(scoring))
                .addColumn(Bytes.toBytes(cf), Bytes.toBytes("difference"), Bytes.toBytes(difference))
                .addColumn(Bytes.toBytes(cf), Bytes.toBytes("tweet"), Bytes.toBytes(tweet.getText()));
    }

    private byte[] getKey(){
        return Bytes.toBytes(this.getTweet().getUserName()+"_"+new Date().getTime());
    }

}
