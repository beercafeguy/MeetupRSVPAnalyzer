package com.github.beercafeguy.rsvp.analytics

import org.apache.log4j.{Level, LogManager}
import org.apache.spark.{SparkConf, SparkContext}

object DemoApp {
  val APP_NAME: String = "Demo App"
  val CM = "local[*]"


  def main(args: Array[String]): Unit = {

    val log = LogManager.getRootLogger
    log.setLevel(Level.INFO)

    log.info("Starting demo app")
    val sparkConf: SparkConf = new SparkConf().setAppName(APP_NAME).setMaster(CM)
    val sparkContext: SparkContext = new SparkContext(sparkConf)

    val stringLine = "Discover the RDD data structure specific to " +
      "Apache Spark and be aware of its main characteristics." +
      " Implement the code lines needed to ingest Meetup RSVPs " +
      "from Kafka in RDDs and write these RDDs in a MongoDB collection"


    val names = List("Hem", "Chandra", "Aman", "Pooja", "Bob", "Foo")

    val namesMarks = List(("Hem", 98), ("Chandra", 45), ("Aman", 90), ("Pooja", 30), ("Bob", 45), ("Foo", 76))


    val firstRDD = sparkContext.range(0, 100, 2)

    val pair=firstRDD.aggregate((0L,0L))(
      ((rt,value) => (rt._1+value,rt._2+1)),
      ((acc1,acc2)=>(acc1._1+acc2._1,acc1._2+acc2._2))
    )

    println(pair._1/pair._2)
    //firstRDD.collect().foreach(println)
    val namesRDD = sparkContext.parallelize(names)

    //namesRDD
    val cartRDD = namesRDD.cartesian(namesRDD)
    //cartRDD.collect().foreach(println)

    val wcRDD = sparkContext.parallelize(stringLine.split(" ")).
      map(word => (word.toLowerCase, 1)).reduceByKey((sum, value) => sum + value)
    //wcRDD.collect().foreach(println)

    //wcRDD.dependencies.foreach(println)

    sparkContext.stop()

    log.info("Demo finished")
  }
}
