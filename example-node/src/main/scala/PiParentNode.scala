import me.tomaszwojcik.calcumau5.api.Node
import me.tomaszwojcik.calcumau5.util.Logging
import messages.{Result, Task}

class PiParentNode extends Node with Logging {

  val children = Seq(
    ctx.remoteNode("child-0"),
    ctx.remoteNode("child-1"),
    ctx.remoteNode("child-2")
  )

  object Constants {
    val TaskLength = BigDecimal("1000000")
    val MaxConcurrentTasks = children.length * 2
    val NumberOfTasks = 100
  }

  var pendingTasks = 0
  var lastTaskIdx = 0

  var pi: BigDecimal = 0

  for (_ <- 0 until Constants.MaxConcurrentTasks) sendNextTask()

  override def receive = {
    case Result(value) =>
      pi += value
      pendingTasks -= 1

      log.info(s"Progress: ${100.0 * lastTaskIdx / Constants.NumberOfTasks}, PI: $pi, Pending tasks: $pendingTasks")

      if (lastTaskIdx < Constants.NumberOfTasks && pendingTasks < Constants.MaxConcurrentTasks) {
        sendNextTask()
      }
  }

  private def sendNextTask(): Unit = {
    val node = children(lastTaskIdx % children.length)
    node ! Task(start = lastTaskIdx * Constants.TaskLength, end = (lastTaskIdx + 1) * Constants.TaskLength)
    lastTaskIdx += 1
    pendingTasks += 1
  }

}
