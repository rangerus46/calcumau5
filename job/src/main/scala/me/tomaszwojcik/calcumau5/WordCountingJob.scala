package me.tomaszwojcik.calcumau5

import java.io.File

import me.tomaszwojcik.calcumau5.Job.Args
import org.slf4j.LoggerFactory

import scala.io.Source

class WordCountingJob(args: Args) extends Job(args) {

  import WordCountingJob._

  reader = classOf[FileLineByLineReader]
  mapper = classOf[MapperImpl]
  reducer = classOf[ReducerImpl]
  writer = classOf[Slf4jWriter]
}

object WordCountingJob {

  private class FileLineByLineReader(args: Args) extends Reader[Long, String](args) {
    private val file = new File(getClass.getResource("/test-file.txt").getFile)
    private val lines = Source.fromFile(file).getLines()
    private var lineNo: Long = -1

    override def readAll(ctx: Context[Long, String]): Unit = {
      while (lines.hasNext) {
        lineNo += 1
        ctx.emit(lineNo, lines.next())
      }
    }

    override def close(): Unit = {}
  }

  private class MapperImpl extends Mapper[Long, String, String, Long] {
    override def map(ln: Long, line: String, ctx: Context[String, Long]): Unit = {
      for (word <- line.split("\\s+")) {
        ctx.emit(word, 1L)
      }
    }
  }

  private class ReducerImpl extends Reducer[String, Long, String, Long] {
    override def reduce(word: String, counts: Seq[Long], ctx: Context[String, Long]): Unit = {
      ctx.emit(word, counts.sum)
    }
  }

  private class Slf4jWriter extends Writer[Any, Any] {
    private val log = LoggerFactory.getLogger(getClass)

    override def write(key: Any, value: Any): Unit = log.info("Writing: ({} <- {})", key, value)

    override def close(): Unit = {}
  }

}
