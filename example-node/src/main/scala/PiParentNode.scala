import me.tomaszwojcik.calcumau5.api.Node
import me.tomaszwojcik.calcumau5.util.Logging
import messages.{Result, Stop, Task}

class PiParentNode extends Node with Logging {

  val children = Seq(
    ctx.remoteNode("child-0"),
    ctx.remoteNode("child-1"),
    ctx.remoteNode("child-2")
  )

  object Constants {
    val TaskLength = BigDecimal("1000000")
    val MaxConcurrentTasks = children.length * 4
    val NumberOfTasks = 500
  }

  var pendingTasks = 0
  var lastTaskIdx = 0

  var pi: BigDecimal = 0

  for (_ <- 0 until Constants.MaxConcurrentTasks) sendNextTask()

  override def receive = {
    case Result(value) =>
      pi += value
      pendingTasks -= 1

      ctx.log(s"Progress: ${100.0 * lastTaskIdx / Constants.NumberOfTasks}, PI: $pi, Pending tasks: $pendingTasks")

      if (lastTaskIdx < Constants.NumberOfTasks) {
        if (pendingTasks < Constants.MaxConcurrentTasks) sendNextTask()
      } else if (pendingTasks <= 0) {
        children.foreach(_ ! Stop)
        ctx.die()
      }
  }

  private def sendNextTask(): Unit = {
    val node = children(lastTaskIdx % children.length)
    node ! Task(start = lastTaskIdx * Constants.TaskLength, end = (lastTaskIdx + 1) * Constants.TaskLength)
    lastTaskIdx += 1
    pendingTasks += 1
  }

}
