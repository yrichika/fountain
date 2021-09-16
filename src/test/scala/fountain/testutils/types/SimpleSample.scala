package fountain.testutils.types

import org.apache.spark.sql.types.{DateType, LongType, StringType, StructField, StructType}

case class SimpleSample(index: Long, word: String, date: String)

object SimpleSample {
  val schema = StructType(Array(
    StructField("index", LongType),
    StructField("word", StringType),
    StructField("date", DateType)
  ))
}