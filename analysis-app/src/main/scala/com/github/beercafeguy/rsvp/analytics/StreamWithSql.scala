package com.github.beercafeguy.rsvp.analytics

import com.mongodb.spark.MongoSpark
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.bson.Document

object StreamWithSql {

  def main(args: Array[String]): Unit = {
    val APP_NAME:String="StreamWithSql"
    val CM="local[*]"
    val BATCH_INTERVAL=Seconds(5000)

    def main(args: Array[String]): Unit = {

      val logger = Logger.getLogger(getClass.getName)
      logger.info("Starting StreamWithSql")

      val rsvpsTopic="rsvp-feed-string";
      val kafkaParams = Map[String, Object](
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
        ConsumerConfig.GROUP_ID_CONFIG -> "StreamingRsvpsDStreamGrp",
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest",
        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> (true: java.lang.Boolean)
      )



      val topics = Array(rsvpsTopic)
      val mongoURI="mongodb://localhost/meetup_db.rsvps_with_guests"

      val sparkConf:SparkConf=new SparkConf().
        setAppName(APP_NAME).
        setMaster(CM).set("spark.mongodb.output.uri",mongoURI);
      //val sparkContext:SparkContext=new SparkContext(sparkConf)

      val sparkSession=SparkSession.builder().config(sparkConf).getOrCreate()
      import sparkSession.implicits._


      val streamingContext:StreamingContext=new StreamingContext(sparkConf,BATCH_INTERVAL)

      val rsvps=KafkaUtils.createDirectStream[String, String](
        streamingContext,
        PreferConsistent,
        ConsumerStrategies.Subscribe[String,String](topics,kafkaParams)
      )

      rsvps.map(_.value()).foreachRDD(rsvpValue => {
        if(!rsvpValue.isEmpty()){
          val rsvpRecord=sparkSession.read.json(sparkSession.createDataset[String](rsvpValue))
          rsvpRecord.printSchema()
          rsvpRecord.createOrReplaceTempView("meetup_view")
          val selected=sparkSession.sql("select * from meetup_view where venue.lat >1").show()
          // MongoSpark.save(selected)
        }
      })


      streamingContext.start()
      streamingContext.awaitTermination()
    }
  }
}
