package com.github.beercafeguy.rsvp.analytics


import org.apache.spark.sql.{Encoders, SparkSession}

object DataSetDemo {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("DataFrameDemo")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._
    val intDS=spark.createDataset[Int](List(1,2,3,4,5,6))

    val colorJsonDS=spark.read.json("src/main/resources/color.json").as[Color]
    //colorJsonDS.filter(color => color.name.equalsIgnoreCase("green")).show()
    val c1=Color("white",255)
    val c2=Color("black",0)
    val colorDS=spark.createDataset[Color](List(c1,c2))
    colorDS.show()
  }
}
