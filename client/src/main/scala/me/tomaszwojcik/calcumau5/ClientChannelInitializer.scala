package me.tomaszwojcik.calcumau5

import java.nio.file.Paths

import io.netty.channel.group.ChannelGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.{ChannelHandler, ChannelInitializer}
import io.netty.handler.codec.serialization.{ClassResolvers, ObjectDecoder, ObjectEncoder}
import io.netty.handler.codec.{LengthFieldBasedFrameDecoder, LengthFieldPrepender}
import io.netty.handler.logging.LoggingHandler
import io.netty.handler.timeout.IdleStateHandler
import me.tomaszwojcik.calcumau5.ArgsParser.Opts
import me.tomaszwojcik.calcumau5.ClientConf.Tcp.{PingInterval, Timeout}
import me.tomaszwojcik.calcumau5.Constants.Frame
import me.tomaszwojcik.calcumau5.handler.{DeployHandler, RunHandler}
import me.tomaszwojcik.calcumau5.util.Logging

class ClientChannelInitializer(action: Symbol, opts: Opts, channels: ChannelGroup)
  extends ChannelInitializer[SocketChannel]
    with Logging {

  private val loggingHandler = new LoggingHandler
  private val lengthFieldPrepender = new LengthFieldPrepender(4)
  private val objectEncoder = new ObjectEncoder

  private def idleStateHandler = new IdleStateHandler(Timeout.toSeconds.toInt, PingInterval.toSeconds.toInt, 0)

  private def clientHandler: ChannelHandler = action match {
    case 'run => new RunHandler(channels)
    case 'deploy =>
      val path = opts.get("file").map(_.asInstanceOf[String]).map(Paths.get(_)).get
      new DeployHandler(channels, path)
    case _ => ???
  }

  override def initChannel(ch: SocketChannel): Unit = {
    ch.pipeline()
      .addLast(loggingHandler)
      .addLast(idleStateHandler)
      .addLast(new LengthFieldBasedFrameDecoder(Frame.MaxLength, 0, Frame.LengthFieldLength, 0, Frame.LengthFieldLength))
      .addLast(lengthFieldPrepender)
      .addLast(new ObjectDecoder(ClassResolvers.weakCachingResolver(getClass.getClassLoader)))
      .addLast(objectEncoder)
      .addLast(clientHandler)
  }

}

