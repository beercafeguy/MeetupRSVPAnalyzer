package com.github.beercafeguy.rsvp.analytics

import java.util.Properties

import org.apache.spark.sql.SparkSession



object DataFrameDemo {

  def main(args: Array[String]): Unit = {

    //session can be created using SparkConf object as well
    val spark = SparkSession
      .builder()
      .appName("DataFrameDemo")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val jsonDF=spark.read.json("src/main/resources/employee.json")
    //val jdbcDF=spark.read.jdbc("url","",new Properties())
    //val parquetDF=spark.read.parquet("")

    jsonDF.printSchema()
    jsonDF.createOrReplaceTempView("user_data")
    spark.sqlContext.sql("select gender,count(*) from user_data group by gender").show()

    //jsonDF.filter($"gender".equalTo("Male")).groupBy($"gender").count().show()
    //jsonDF.filter("gender == 'Male'").groupBy($"gender").count().show()
    //jsonDF.filter(jsonDF.col("gender").equalTo("Male")).groupBy($"gender").count().show()
    //println(jsonDF.count())
  }
}
