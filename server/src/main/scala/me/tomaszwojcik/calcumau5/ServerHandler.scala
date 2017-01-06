package me.tomaszwojcik.calcumau5

import java.io.{File, FileOutputStream}
import java.net.{InetSocketAddress, URLClassLoader}

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{Channel, ChannelHandlerContext, SimpleChannelInboundHandler}
import me.tomaszwojcik.calcumau5.events.inbound.InMsg
import me.tomaszwojcik.calcumau5.exceptions.JarNotDeployedException
import me.tomaszwojcik.calcumau5.frames.{Frame, FrameHandler}
import me.tomaszwojcik.calcumau5.util.Logging

import scala.concurrent.ExecutionContext

@Sharable
class ServerHandler(implicit ec: ExecutionContext)
  extends SimpleChannelInboundHandler[Frame]
    with Logging {

  var channel: Channel = _

  val activeJarFileLock = new Object
  var activeJarFile: File = _
  var classLoader: URLClassLoader = _

  var nodeEnv: NodeEnv = _

  val frameHandler: FrameHandler = { frame => channel.writeAndFlush(frame) }

  override def channelRead0(ctx: ChannelHandlerContext, frame: Frame): Unit = frame match {
    case frames.Ping => ctx.writeAndFlush(frames.Pong)
    case frames.Pong => // ignore pongs
    case f: frames.Run => handleStartFrame(ctx, f)
    case f: frames.Message => handleMessageFrame(ctx, f)
    case f: frames.File => handleFileFrame(ctx, f)
    case _ => log.info("Received frame: {}", frame)
  }

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    channel = ctx.channel
    val address = channel.remoteAddress.asInstanceOf[InetSocketAddress]
    log.info(s"Accepted connection from ${address.getHostString}:${address.getPort}")
  }

  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    val address = channel.remoteAddress.asInstanceOf[InetSocketAddress]
    log.info(s"Client at ${address.getHostString}:${address.getPort} closed the connection")
  }

  private def handleStartFrame(ctx: ChannelHandlerContext, frame: frames.Run): Unit = activeJarFileLock synchronized {
    if (activeJarFile == null) throw JarNotDeployedException()
    if (classLoader != null) classLoader.close()

    val urls = Array(activeJarFile.toURI.toURL)
    classLoader = new URLClassLoader(urls, getClass.getClassLoader)

    nodeEnv = {
      val env = new NodeEnv(classLoader, frameHandler, frame.nodes)
      env.start()
      env
    }
  }

  private def handleMessageFrame(ctx: ChannelHandlerContext, frame: frames.Message): Unit = {
    val msg = Serializers.deserializeMsg(frame.msg, classLoader)
    val evt = InMsg(msg, frame.recipient, frame.sender)
    nodeEnv.pushEvent(evt)
  }

  private def handleFileFrame(ctx: ChannelHandlerContext, frame: frames.File): Unit = activeJarFileLock synchronized {
    val file = File.createTempFile(s"node-${System.nanoTime()}", ".jar")
    val os = new FileOutputStream(file)
    try {
      log.info(s"Set deployed JAR to: ${file.getPath}")
      os.write(frame.bytes)
      activeJarFile = file
    } finally {
      os.close()
    }
  }

}
