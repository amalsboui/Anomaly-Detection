#!/bin/bash

# Navigate to agents folder
cd ~/Desktop/Projects/AnomalyDetection/jade_agents || exit

echo "Starting Main Container + ServerAgent..."
java -cp ".:$CLASSPATH:$SQLITE_JDBC" jade.Boot \
  -gui \
  -agents "server:ServerAgent" &
SERVER_PID=$!

# Give the main container time to start
sleep 3

echo "Starting LocalAgent 1..."
java -cp ".:$CLASSPATH:$SQLITE_JDBC" jade.Boot \
  -container \
  -host localhost \
  -agents "local1:LocalAgent" &

echo "Starting LocalAgent 2..."
java -cp ".:$CLASSPATH:$SQLITE_JDBC" jade.Boot \
  -container \
  -host localhost \
  -agents "local2:LocalAgent" &

echo "Starting LocalAgent 3..."
java -cp ".:$CLASSPATH:$SQLITE_JDBC" jade.Boot \
  -container \
  -host localhost \
  -agents "local3:LocalAgent" &

# Optional: wait for main container
wait $SERVER_PID

