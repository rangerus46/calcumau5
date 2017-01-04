import me.tomaszwojcik.calcumau5.api.{Node, NodeRef}
import me.tomaszwojcik.calcumau5.util.Logging
import messages.{Result, Task}

import scala.annotation.tailrec

class PiParentNode extends Node with Logging {

  val children = Seq(
    ctx.remoteNode("child-0"),
    ctx.remoteNode("child-1"),
    ctx.remoteNode("child-2"),
    ctx.remoteNode("child-3")
  )

  splitAndSend(Task(0, 100 * 1000 * 1000), children)

  var pi: BigDecimal = 0

  override def receive = {
    case Result(value) =>
      pi += value
      log.info(s"PI = $pi")
  }

  @tailrec private def splitAndSend(task: Task, nodes: Seq[NodeRef]): Unit = {
    if (task.start < task.end && nodes.nonEmpty) {
      val rangeLength = (task.end - task.start) / nodes.length
      val subTask = task.copy(end = task.start + rangeLength)

      nodes.head ! subTask
      log.info(s"Sent $subTask")

      splitAndSend(task.copy(start = subTask.end), nodes.tail)
    }
  }

}
