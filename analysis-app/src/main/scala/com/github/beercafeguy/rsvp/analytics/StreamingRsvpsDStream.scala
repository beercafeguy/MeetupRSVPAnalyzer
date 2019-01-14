package com.github.beercafeguy.rsvp.analytics

import java.util

import com.mongodb.spark.MongoSpark
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, OffsetAndMetadata}
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.bson.Document


object StreamingRsvpsDStream {

  val APP_NAME:String="StreamingRsvpsDStream"
  val CM="local[*]"
  val BATCH_INTERVAL=Seconds(5000)

  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger(getClass.getName)
    logger.info("Starting StreamingRsvpsDStream")

    val rsvpsTopic="rsvp-feed-string";
    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.GROUP_ID_CONFIG -> "StreamingRsvpsDStreamGrp",
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> (false: java.lang.Boolean)
    )

    val offsetRange:Array[OffsetRange]=Array(OffsetRange.create(rsvpsTopic,0,0,25))

    val topics = Array(rsvpsTopic)
    val mongoURI="mongodb://localhost/meetup_db.rsvps_with_guests"

    val sparkConf:SparkConf=new SparkConf().
      setAppName(APP_NAME).
      setMaster(CM).set("spark.mongodb.output.uri",mongoURI);
    //val sparkContext:SparkContext=new SparkContext(sparkConf)

    val streamingContext:StreamingContext=new StreamingContext(sparkConf,BATCH_INTERVAL)

    val rsvps=KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      ConsumerStrategies.Subscribe[String,String](topics,kafkaParams)
    )


    val rsvpsWithGuests=rsvps.filter(rsvp => ! rsvp.value().contains("\"guests\":0"))
    val docRDD=rsvpsWithGuests.map(rsvp => Document.parse(rsvp.value()))
    docRDD.foreachRDD(MongoSpark.save(_))

    /*rsvps.foreachRDD(meetupRDD => {
      val offsetRanges=meetupRDD.asInstanceOf[HasOffsetRanges].offsetRanges
      rsvps.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges,
        (offsets: util.Map[TopicPartition, OffsetAndMetadata], exception: Exception) =>{
          println("-----------------------------------------------------")
          println("{0} | {1}",offsets,exception)
          println("-----------------------------------------------------")
        })
    })*/


    rsvps.foreachRDD(meetupRDD => {
      val offsetRanges=meetupRDD.asInstanceOf[HasOffsetRanges].offsetRanges
      rsvps.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
    })

    streamingContext.start()
    streamingContext.awaitTermination()
  }
}
