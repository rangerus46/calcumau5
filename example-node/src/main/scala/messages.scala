object messages {

  case class Task(n: Long)

  case class SubTask(startInclusive: Long, endExclusive: Long)

  case class Result(value: Double)

}
