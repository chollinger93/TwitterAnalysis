# Twitter Analysis
This is an example project for my [blog](https://otter-in-a-suit.com/blog). 

It compares Apache Storm with Twitter's Heron by analyzing a bunch of structured tweets, filtering them by "Americans" and try to figure out if they are Repulbican, Democrat or Undecided by using an prototype-based scoring algorithms which learns by analyzing Hillary Clinton's and Donald Trump's recent tweets.

## Install
Fill in auth.properties with your Twitter API credentials
```sh
$ cp src/main/resources/auth-example.properties ~/auth.properties
$ vi ~/auth.properties
$ mvn clean install  -Dmaven.test.skip=true -Pprod
```
### Storm
```sh
$ storm jar target/TwitterAnalysis-1.0-SNAPSHOT.jar com.otterinasuit.twitter.Main ~/auth.properties
```
### Heron
Follow [this](http://twitter.github.io/heron/docs/upgrade-storm-to-heron/). Remove / comment out Storm dependency
```sh
$ heron submit local TwitterAnalysis-1.0-SNAPSHOT.jar com.otterinasuit.twitter.Main TwitterAnalysis --topology-args ~/auth.properties
```
## Disclaimer
This is WORK IN PROGRESS AND WILL PROBABLY NOT RUN YET OR PRODUCE MEDICORE RESULTS