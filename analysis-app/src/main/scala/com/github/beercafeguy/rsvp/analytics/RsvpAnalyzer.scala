package com.github.beercafeguy.rsvp.analytics

import com.mongodb.spark.MongoSpark
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, LogManager}
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.{SparkConf, SparkContext}

object RsvpAnalyzer {

  val APP_NAME:String="Demo App"
  val CM="local[*]"

  def main(args: Array[String]): Unit = {
    val log = LogManager.getRootLogger
    log.setLevel(Level.INFO)
    log.info("Starting Meetup RSVP Analyzer")
    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.GROUP_ID_CONFIG -> "meetup_group",
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "latest",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> (true: java.lang.Boolean)
    )

    val mongoURI="mongodb://localhost/meetup_db.rsvps"

    val topics = Array("topicA", "topicB")

    log.info("Starting demo app")
    val sparkConf:SparkConf=new SparkConf().
      setAppName(APP_NAME).
      setMaster(CM).set("spark.mongodb.output.uri",mongoURI);
    val sparkContext:SparkContext=new SparkContext(sparkConf)

    //Business Logic


    sparkContext.stop()
    log.info("Demo finished")
  }
}
