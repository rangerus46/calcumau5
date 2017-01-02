import me.tomaszwojcik.calcumau5.api.{Node, NodeRef}
import me.tomaszwojcik.calcumau5.util.Logging
import messages.{Result, SubTask}

import scala.annotation.tailrec
import scala.collection.mutable

class PiParentNode extends Node with Logging {

  val children = Seq(
    ctx.remoteNode("child-0"),
    ctx.remoteNode("child-1")
  )

  sendSubTasks(0, 1500000, children)

  val partialSums = new mutable.ArrayBuffer[Double]

  override def receive = {
    case Result(value) =>
      partialSums += value
      if (partialSums.size >= children.size) {
        log.info(s"Result: ${partialSums.sum}")
        ctx.die()
      }
  }

  @tailrec private def sendSubTasks(start: Int, end: Int, nodes: Seq[NodeRef]): Unit = {
    if (start < end && nodes.nonEmpty) {
      val rangeLength = (end - start) / nodes.length
      val subTask = SubTask(start, start + rangeLength)
      log.info("Sending sub task: {}", subTask)
      nodes.head tell subTask
      sendSubTasks(start + rangeLength, end, nodes.drop(1))
    }
  }

}
