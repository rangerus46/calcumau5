import me.tomaszwojcik.calcumau5.api.{Node, NodeRef}
import me.tomaszwojcik.calcumau5.util.Logging
import messages.{Result, Task}

import scala.annotation.tailrec
import scala.collection.mutable

class PiParentNode extends Node with Logging {

  val children = Seq(
    ctx.remoteNode("child-0"),
    ctx.remoteNode("child-1")
  )

  splitAndSend(Task(0, 100 * 1000 * 1000), children)

  val partialSums = new mutable.ArrayBuffer[BigDecimal]

  override def receive = {
    case Result(value) =>
      partialSums += value
      if (partialSums.size >= children.size) {
        log.info(s"Result: ${partialSums.sum}")
        ctx.die()
      }
  }

  @tailrec private def splitAndSend(task: Task, nodes: Seq[NodeRef]): Unit = {
    if (task.start < task.end && nodes.nonEmpty) {
      val rangeLength = (task.end - task.start) / nodes.length
      val subTask = task.copy(end = task.start + rangeLength)
      log.info("Sending sub task: {}", subTask)
      nodes.head.tell(subTask)
      splitAndSend(task.copy(start = subTask.end), nodes.tail)
    }
  }

}
