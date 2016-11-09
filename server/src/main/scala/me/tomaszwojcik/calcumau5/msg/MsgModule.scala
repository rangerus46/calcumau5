package me.tomaszwojcik.calcumau5.msg

import com.softwaremill.macwire.wire

trait MsgModule {
  lazy val msgCoder: MsgCoder = wire[MsgCoder]
  lazy val msgQueue: MsgQueue = wire[MsgQueue]
}
