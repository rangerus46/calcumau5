object messages {

  case class Task(start: BigDecimal, end: BigDecimal)

  case class Result(value: BigDecimal)

  case object Stop

}
