package com.github.beercafeguy.rsvp.analytics

import kafka.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

import org.apache.spark.sql.functions.col
import org.apache.spark.sql.functions.from_json
import org.apache.spark.sql.functions.window


object StructuredStreamApp {
  val APP_NAME: String = "StructuredStreamApp"
  val CM = "local[*]"
  val BATCH_INTERVAL = Seconds(5000)


  def main(args: Array[String]): Unit = {

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "StructuredStreamAppGrp",
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (true: java.lang.Boolean)
    )

    val STREAM_FORMAT = "kafka"
    val CHECKPOINT_DIR = "str_checkpoint/"
    val topics = Array("rsvp-feed-string")
    val sparkConf: SparkConf = new SparkConf().
      setAppName(APP_NAME).setMaster(CM).set("spark.sql.caseSensitive","false")
    //val sparkContext:SparkContext=new SparkContext(sparkConf)

    val sparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    import sparkSession.implicits._

    val rsvpSchema = new StructType().add(
      "venue",
      new StructType().add("venue_name", StringType, true)
        .add("lon", DoubleType, true)
        .add("lat", DoubleType, true)
        .add("venue_id", LongType, true)
    ).add("visibility", StringType, true)
      .add("response", StringType, true)
      .add("guests", LongType, true)
      .add("member", new StructType()
        .add("member_id", LongType, true)
        .add("photo", StringType, true)
        .add("member_name", StringType, true))
      .add("rsvp_id", LongType, true)
      .add("mtime", LongType, true)
      .add("event", new StructType()
        .add("event_name", StringType, true)
        .add("event_id", StringType, true)
        .add("time", LongType, true)
        .add("event_url", StringType, true))
      .add("group", new StructType()
        .add("group_city", StringType, true)
        .add("group_country", StringType, true)
        .add("group_id", LongType, true)
        .add("group_lat", DoubleType, true)
        .add("group_long", DoubleType, true)
        .add("group_name", StringType, true)
        .add("group_state", StringType, true)
        .add("group_topics", DataTypes.createArrayType(
          new StructType()
            .add("topicName", StringType, true)
            .add("urlkey", StringType, true)), true)
        .add("group_urlname", StringType, true))


    val meetupDF=sparkSession.readStream.format(STREAM_FORMAT)
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", topics(0))
      .load();

    meetupDF.printSchema()

    val rsvpTsDF = meetupDF.select(
      col("timestamp"),
      from_json(col("value").cast("string"),rsvpSchema).alias("rsvp")
    ).alias("meetup").select("meetup.*")

    rsvpTsDF.printSchema()

    val windowCount=rsvpTsDF.withWatermark("timestamp","1 minute")
      .groupBy(window(col("timestamp"),"4 minutes","2 minutes"),col("rsvp.guests")).count
    val query=windowCount.writeStream.outputMode("complete")
      .format("console")
      .option("checkpointLocation",CHECKPOINT_DIR)
      .option("truncate",false)
      .start
    query.awaitTermination()



  }
}
