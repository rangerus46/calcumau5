package me.tomaszwojcik.calcumau5

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.string.{StringDecoder, StringEncoder}
import io.netty.handler.codec.{LengthFieldBasedFrameDecoder, LengthFieldPrepender}
import io.netty.handler.logging.LoggingHandler
import me.tomaszwojcik.calcumau5.Constants.Frame
import me.tomaszwojcik.calcumau5.util.Logging

class ServerChannelInitializer extends ChannelInitializer[SocketChannel] with Logging {

  private val loggingHandler = new LoggingHandler
  private val lengthFieldPrepender = new LengthFieldPrepender(4)
  private val stringDecoder = new StringDecoder(Constants.DefaultCharset)
  private val stringEncoder = new StringEncoder(Constants.DefaultCharset)
  private val serverHandler = new ServerHandler

  override def initChannel(ch: SocketChannel): Unit = {
    ch.pipeline()
      .addLast(loggingHandler)
      .addLast(new LengthFieldBasedFrameDecoder(Frame.MaxLength, 0, Frame.LengthFieldLength, 0, Frame.LengthFieldLength))
      .addLast(lengthFieldPrepender)
      .addLast(stringDecoder)
      .addLast(stringEncoder)
      .addLast(serverHandler)
  }

}

