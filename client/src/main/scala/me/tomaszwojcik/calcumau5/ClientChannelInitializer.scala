package me.tomaszwojcik.calcumau5

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.string.{StringDecoder, StringEncoder}
import io.netty.handler.codec.{LengthFieldBasedFrameDecoder, LengthFieldPrepender}
import io.netty.handler.logging.LoggingHandler
import io.netty.handler.timeout.IdleStateHandler
import me.tomaszwojcik.calcumau5.ClientConf.Tcp.{PingInterval, Timeout}
import me.tomaszwojcik.calcumau5.Constants.Frame
import me.tomaszwojcik.calcumau5.util.Logging

class ClientChannelInitializer extends ChannelInitializer[SocketChannel] with Logging {

  private val loggingHandler = new LoggingHandler
  private val idleStateHandler = new IdleStateHandler(Timeout.toSeconds.toInt, PingInterval.toSeconds.toInt, 0)
  private val lengthFieldPrepender = new LengthFieldPrepender(4)
  private val stringDecoder = new StringDecoder(Constants.DefaultCharset)
  private val stringEncoder = new StringEncoder(Constants.DefaultCharset)
  private val clientHandler = new ClientHandler

  override def initChannel(ch: SocketChannel): Unit = {
    ch.pipeline()
      .addLast(loggingHandler)
      .addLast(idleStateHandler)
      .addLast(new LengthFieldBasedFrameDecoder(Frame.MaxLength, 0, Frame.LengthFieldLength, 0, Frame.LengthFieldLength))
      .addLast(lengthFieldPrepender)
      .addLast(stringDecoder)
      .addLast(stringEncoder)
      .addLast(clientHandler)
  }

}

