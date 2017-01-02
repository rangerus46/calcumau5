import me.tomaszwojcik.calcumau5.api.Node
import me.tomaszwojcik.calcumau5.util.Logging
import messages.{Result, Task}

class PiChildNode extends Node with Logging {
  override def receive = {
    case Task(start, end) =>
      log.info(s"Received Task($start,$end)")

      var sum: BigDecimal = 0
      for (n <- start until end by 1) {
        sum += 4 * (1 - (n % 2) * 2) / (2 * n + 1)
      }

      val result = Result(sum)
      sender.tell(result)

      log.info(s"Sent $result")
      ctx.die()
  }
}
