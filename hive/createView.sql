CREATE DATABASE IF NOT EXISTS test;
CREATE TABLE IF NOT EXISTS test.party_hdfs(
`key` string,
`party` string,
`democratsScore` double,
`republicansScore` double,
`score` double
`tweet` string,
)
ROW FORMAT DELIMITED
DELIMITED BY ','
STORED AS TEXTFILE
LOCATION '/results/data_9';

SELECT COUNT(*) FROM test.action_hdfs;