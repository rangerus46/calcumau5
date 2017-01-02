import me.tomaszwojcik.calcumau5.api.Node
import me.tomaszwojcik.calcumau5.util.Logging
import messages.{Result, SubTask}

class PiChildNode extends Node with Logging {
  override def receive = {
    case SubTask(start, end) =>
      log.info(s"Received: start = $start, end = $end")

      var sum = 0.0
      for (n <- start until end) {
        sum += 4.0 * (1 - (n % 2) * 2) / (2 * n + 1)
      }

      val result = Result(sum)
      sender.tell(result)

      log.info(s"Sent $result")
  }
}
