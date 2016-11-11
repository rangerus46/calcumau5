package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.worker.WorkerModule
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}

object JettyRunner {

  object Modules extends WorkerModule

  def main(args: Array[String]): Unit = {
    val server = new Server(Config.Http.Port)
    try {
      server.setHandler {
        val handler = new ServletContextHandler()
        handler.addServlet(new ServletHolder(Modules.workerServlet), "/workers/*")
        handler
      }
      server.start()
      server.join()
    } finally {
      server.stop()
    }
  }
}
