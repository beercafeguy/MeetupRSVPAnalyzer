package com.github.beercafeguy.rsvp.analytics

import org.apache.log4j.Logger

import com.mongodb.spark.MongoSpark
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.{KafkaUtils, LocationStrategies, OffsetRange}
import org.apache.spark.{SparkConf, SparkContext}
import org.bson.Document

import scala.collection.JavaConverters._


object KafkaToMongoApp {

  val APP_NAME:String="KafkaToMongoApp"
  val CM="local[*]"
  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger(getClass.getName)
    logger.info("Starting KafkaToMongoApp")

    val rsvpsTopic="rsvp-feed-string";
    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.GROUP_ID_CONFIG -> "kafka_mongo_group_1",
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> (true: java.lang.Boolean)
    ).asJava

    val offsetRange:Array[OffsetRange]=Array(OffsetRange.create(rsvpsTopic,0,0,25))

    val mongoURI="mongodb://localhost/meetup_db.rsvps_general"

    val sparkConf:SparkConf=new SparkConf().
      setAppName(APP_NAME).
      setMaster(CM).set("spark.mongodb.output.uri",mongoURI);
    val sparkContext:SparkContext=new SparkContext(sparkConf)

    val rsvpsRDD=KafkaUtils.createRDD[String,String](sparkContext,
      kafkaParams,
      offsetRange
      ,LocationStrategies.PreferConsistent)

    MongoSpark.save(rsvpsRDD.map(rsvp => Document.parse(rsvp.value())))
    sparkContext.stop()
  }
}
