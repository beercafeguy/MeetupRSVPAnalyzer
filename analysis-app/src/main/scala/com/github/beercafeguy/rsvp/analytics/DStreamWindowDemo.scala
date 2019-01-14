package com.github.beercafeguy.rsvp.analytics

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Duration, Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.codehaus.jackson.map.ObjectMapper

object DStreamWindowDemo extends App{

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> "localhost:9092",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "DStreamWindowDemo",
    "auto.offset.reset" -> "earliest",
    "enable.auto.commit" -> (true: java.lang.Boolean)
  )

  val topics = Array("rsvp-feed-string")
  val conf = new SparkConf().setAppName("DStreamWindowDemo").setMaster("local[*]")
  val streamingContext = new StreamingContext(conf, Seconds(1))

  streamingContext.checkpoint("checkpoint/")
  val rsvpsStream = KafkaUtils.createDirectStream[String, String](
    streamingContext,
    PreferConsistent,
    Subscribe[String, String](topics, kafkaParams)
  )

  val countStream=rsvpsStream.countByWindow(Seconds(30),Seconds(5))
  val countByValueStream=rsvpsStream
    .map(rsvp => {
      val objectMapper = new ObjectMapper()
      val jsonNode=objectMapper.readTree(rsvp.value())
      (rsvp.key,jsonNode.path("visibility").asText())
    })
    .filter(root => root._2!=null)
    .countByValueAndWindow(Seconds(30),Seconds(5))

  countStream.foreachRDD(
    rsvpRDD => rsvpRDD.foreach(println)
  )

  streamingContext.start()
  streamingContext.awaitTermination()
}
