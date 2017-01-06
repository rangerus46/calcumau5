import me.tomaszwojcik.calcumau5.api.Node
import me.tomaszwojcik.calcumau5.util.Logging
import messages.{Result, Task}

class PiParentNode extends Node with Logging {

  val numberOfTasks: Int = 1000
  val taskLength: Int = 1000 * 1000 * 1000

  var numberOfFinishedTasks: Int = 0

  val children = Seq(
    ctx.remoteNode("child-0"),
    ctx.remoteNode("child-1"),
    ctx.remoteNode("child-2"),
    ctx.remoteNode("child-3")
  )

  sendTasks(numberOfTasks, taskLength)

  var pi: BigDecimal = 0

  override def receive = {
    case Result(value) =>
      numberOfFinishedTasks += 1
      pi += value
      log.info(s"Progress: ${100.0 * numberOfFinishedTasks / numberOfTasks} PI = $pi")
  }

  private def sendTasks(n: Int, rangeLength: BigDecimal): Unit = {
    for (i <- 0 until n) {
      val node = children(i % children.length)
      node ! Task(start = i * rangeLength, end = (i + 1) * rangeLength)
    }
  }

}
