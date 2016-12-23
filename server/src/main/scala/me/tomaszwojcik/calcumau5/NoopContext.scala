package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.util.Logging

class NoopContext extends Context[Any, Any] with Logging {
  override def emit(key: Any, value: Any): Unit = {
    log.info("Emitting: {} <- {}", key, value)
  }
}
