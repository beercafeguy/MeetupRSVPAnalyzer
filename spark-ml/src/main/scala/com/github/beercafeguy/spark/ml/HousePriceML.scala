package com.github.beercafeguy.spark.ml



import org.apache.spark.SparkConf
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.{Pipeline, PipelineStage}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.{FloatType, LongType, StringType, StructType}
import org.apache.spark.ml.feature.{Imputer, VectorAssembler}
import org.apache.spark.ml.regression.LinearRegression

object HousePriceML {

  val DATA_FILE_LOCATION="src/main/resources/houses.json"

  def main(args: Array[String]): Unit = {

    val schema=new StructType().add("House",LongType,true)
      .add("Taxes",LongType,true)
      .add("Bedrooms",LongType,true)
      .add("Baths",FloatType,true)
      .add("Quadrant",StringType,true)
      .add("NW",StringType,true)
      .add("Price($)",LongType,true)
      .add("Size(sqft)",LongType,true)
      .add("lot",LongType,true)

    val sparkConf=new SparkConf()
      .setMaster("local[*]")
      .setAppName("House Price Predictor")
      .set("spark.sql.caseSensitive","false")
    val spark=SparkSession.builder().config(sparkConf)
      .getOrCreate()
    import spark.implicits._

    val housesDF=spark.read.schema(schema).json(DATA_FILE_LOCATION)

    val preparedDF=housesDF.select($"Taxes",$"Bedrooms",$"Baths",$"Size(sqft)",col("Price($)"))
      .withColumnRenamed("Size(sqft)","Size")
      .withColumnRenamed("Price($)","Price")


    //preparedDF.groupBy($"Bedrooms").agg(sum($"Price").as("Total_Price")).show

    //ML part starts Here

    val labelDF=preparedDF.withColumnRenamed("Price","label")

    val imputer = new Imputer()
      .setInputCols(Array[String]("Baths"))
      .setOutputCols(Array[String]("~Baths~"))


    val assembler=new VectorAssembler()
      .setInputCols(Array[String]("Taxes", "Bedrooms", "~Baths~", "Size"))
      .setOutputCol("features")

    val linearRegression=new LinearRegression
    linearRegression.setMaxIter(1000)

    val pipeline=new Pipeline()
      .setStages(Array[PipelineStage](
        imputer,assembler,linearRegression
      ))

    val splitDFs = labelDF.randomSplit(Array[Double](0.8,0.2))
    val trainDF = splitDFs(0)
    val evaluationDF = splitDFs(1)

    val pipelineModel = pipeline.fit(trainDF)

    val predictionsDF=pipelineModel.transform(evaluationDF)
    predictionsDF.show()

    val forEvaluationDF=predictionsDF.select(col("label"), col("prediction"));

    val evaluteR2=new RegressionEvaluator().setMetricName("r2")
    val evaluteRMSE=new RegressionEvaluator().setMetricName("rmse")

    val r2=evaluteR2.evaluate(forEvaluationDF)
    val rmse=evaluteRMSE.evaluate(forEvaluationDF)

    println("R2: -> "+r2)
    println("RMSE" -> rmse)

  }
}
