import me.tomaszwojcik.calcumau5.api.Node
import me.tomaszwojcik.calcumau5.util.Logging
import messages._

class PiChildNode extends Node with Logging {

  val maxStepLength: Int = 1000000
  val parent = ctx.remoteNode("parent")

  override def receive = {
    case Task(start, end) =>
      if (end - start > maxStepLength) {
        calculateAndSend(start, start + maxStepLength)
        self ! Task(start + maxStepLength, end)
      } else {
        calculateAndSend(start, end)
      }
  }

  def calculateAndSend(start: BigDecimal, end: BigDecimal): Unit = {
    if (start < end) {
      var sum: BigDecimal = 0
      for (n <- start until end by 1) {
        sum += 4 * (1 - (n % 2) * 2) / (2 * n + 1)
      }
      parent ! Result(sum)
    }
  }

}
