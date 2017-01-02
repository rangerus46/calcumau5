import me.tomaszwojcik.calcumau5.api.Node
import me.tomaszwojcik.calcumau5.util.Logging
import messages.{Result, SubTask}

class PiChildNode extends Node with Logging {
  override def receive = {
    case SubTask(s, e) =>
      var sum = 0.0
      for (n <- s until e) {
        sum += 4.0 * (1 - (n % 2) * 2) / (2 * n + 1)
      }
      sender tell Result(sum)
  }
}
