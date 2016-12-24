package me.tomaszwojcik.calcumau5

object Entities {

  import slick.driver.HsqldbDriver.api._

  case class JobRef(
    id: Option[Long],
    name: String,
    className: String,
    jarName: String
  )

  class JobRefs(tag: Tag) extends Table[JobRef](tag, "JOB_REFS") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def className = column[String]("CLASS_NAME")

    def jarName = column[String]("JAR_NAME")

    def * = (id.?, name, className, jarName) <> (JobRef.tupled, JobRef.unapply)
  }

  val jobRefs = TableQuery[JobRefs]

  def init()(implicit db: Database): Unit = {
    db.run(DBIO.seq(
      jobRefs.schema.create,
      jobRefs += JobRef(None, "countWords", "me.tomaszwojcik.calcumau5.WordCountingJob", "job-0.jar")
    ))
  }

}
