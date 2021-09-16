package fountain.servers

import fountain.testutils.TestCase
import fountain.testutils.types.SimpleSample
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

/**
 * Please DO NOT execute with sbt shell command `test`.
 * This is not a usual unit test.
 * Please execute it with IntelliJ [Run] command to see if it's working
 * in Run window.
 *
 * I will make it work as complete unit tests in future, I think.
 */
class EachLineFromFileSpec extends TestCase {

  val spark = SparkSession.builder()
    .appName("testing")
    .master("local[2]")
    .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._

  "run" should {
    "give data to spark streaming socket" in {


      EachLineFromFile.run("localhost", 9999, "data/sample.json", 300)

      val lines = spark.readStream
        .format("socket")
        .option("dateFormat", "YYYY-MM-dd")
        .option("host", "localhost")
        .option("port", 9999)
        .load()
        .select(from_json(col("value"), SimpleSample.schema).as("sample"))
        .selectExpr("sample.*")
        .as[SimpleSample]

      lines.writeStream
        .format("console")
        .outputMode("append")
        .start()
        .awaitTermination()

    }
  }
}
