package com.otterinasuit.twitter.objects;


import java.io.Serializable;

public class Tweet implements Serializable{

    private String userName;
    private String userDisplayName;
    private String text;
    private String userDescription;
    private long userId;
    private String lang;
    private String place;
    private boolean isFavorited;
    private int favoriteCount;
    private boolean isRetweet;
    private int retweetCount;
    private long id;


    public Tweet(){

    }
    /**
     * Simple implementation of Twitter4js "Status" interface, as this creates a
     * "Class is not registered: twitter4j.StatusJSONImp" exception
     * @param userName
     * @param userDisplayName
     * @param text
     * @param userDescription
     * @param lang
     */
    public Tweet(long userId, long id, String userName, String userDisplayName, String text, String userDescription, String lang) {
        this.userId = userId;
        this.id = id;
        this.userName = userName;
        this.userDisplayName = userDisplayName;
        this.text = text;
        this.userDescription = userDescription;
        this.lang = lang;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public boolean isRetweet() {
        return isRetweet;
    }

    public void setRetweet(boolean retweet) {
        isRetweet = retweet;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "userName='" + userName + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", text='" + text + '\'' +
                ", userDescription='" + userDescription + '\'' +
                ", userId=" + userId +
                ", lang='" + lang + '\'' +
                ", place='" + place + '\'' +
                ", isFavorited=" + isFavorited +
                ", favoriteCount=" + favoriteCount +
                ", isRetweet=" + isRetweet +
                ", retweetCount=" + retweetCount +
                '}';
    }
}
