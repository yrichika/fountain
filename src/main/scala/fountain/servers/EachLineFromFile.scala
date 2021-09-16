package fountain.servers

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Source, Tcp}
import akka.util.ByteString
import fountain.shutdownreasons.{EndOfStreamShutdown, ErrorShutdown}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}


object EachLineFromFile {
  implicit val actorSystem = ActorSystem("EachLineFromFile")
  implicit val materializer = ActorMaterializer

  implicit val dispatcher = actorSystem.dispatchers.lookup("dispatcher")

  /**
   * Start a streaming server which sends text data from a file.
   * It will shutdown automatically when it finishes sending all lines from the file.
   *
   * @param host The hostname/interface of the streaming server
   * @param port The port to listen to
   * @param fileName The file you want to send as streaming by each line
   * @param interval The unit is millisecond. Each line of the file is sent at this intervals.
   * @return
   */
  def run(host: String, port: Int, fileName: String, interval: Int) = {
    val connections = Tcp().bind(host, port)
    connections.runForeach { connection =>
      val flow = connection.flow.watchTermination()(Keep.right)
      startStreamingFromFile(fileName, interval, flow)
    }
  }


  def startStreamingFromFile(filePath: String, interval: Int, flow: Flow[ByteString, ByteString, Future[Done]]): Unit = {
    val source = getFileSource(filePath, interval)
    val watched = source.viaMat(flow)(Keep.right).run()
    watched.onComplete {
      case Success(_) => CoordinatedShutdown(actorSystem).run(EndOfStreamShutdown)
      case Failure(_) => CoordinatedShutdown(actorSystem).run(ErrorShutdown)
    }
  }


  def getFileSource(filePath: String, interval: Int) = {
    val lines = scala.io.Source.fromFile(filePath).getLines()
    Source.fromIterator(() => lines)
      .map(line => ByteString(line + "\n"))
      .throttle(1, interval.milliseconds)
  }


}
