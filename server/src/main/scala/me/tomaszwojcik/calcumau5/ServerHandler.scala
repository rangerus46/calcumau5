package me.tomaszwojcik.calcumau5

import java.io.{File, FileOutputStream}
import java.net.{InetSocketAddress, URLClassLoader}

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{Channel, ChannelHandlerContext, SimpleChannelInboundHandler}
import me.tomaszwojcik.calcumau5.api.Node
import me.tomaszwojcik.calcumau5.exceptions.JarNotDeployedException
import me.tomaszwojcik.calcumau5.frames.{Frame, FrameHandler}
import me.tomaszwojcik.calcumau5.impl.NodeContextImpl
import me.tomaszwojcik.calcumau5.util.Logging

import scala.collection.mutable

@Sharable
class ServerHandler
  extends SimpleChannelInboundHandler[Frame]
    with Logging {

  var channel: Channel = _
  val nodesByID = new mutable.HashMap[String, Node]

  val activeJarFileLock = new Object
  var activeJarFile: File = _
  var classLoader: URLClassLoader = _

  val frameHandler: FrameHandler = { frame: Frame => channel.writeAndFlush(frame) }

  override def channelRead0(ctx: ChannelHandlerContext, frame: Frame): Unit = frame match {
    case frames.Ping => ctx.writeAndFlush(frames.Pong)
    case frames.Pong => // ignore pongs
    case f: frames.Run => handleStartFrame(f)
    case f: frames.Message => handleMessageFrame(f)
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

  private def handleStartFrame(frame: frames.Run): Unit = activeJarFileLock synchronized {
    if (activeJarFile == null) throw JarNotDeployedException()
    nodesByID.clear()

    val urls = Array(activeJarFile.toURI.toURL)
    classLoader = new URLClassLoader(urls, getClass.getClassLoader)

    for ((id, className) <- frame.nodes) {
      val c = classLoader.loadClass(className)
      val instance = c.newInstance().asInstanceOf[Node]
      nodesByID.put(id, instance)
    }

    nodesByID.mapValues(_.ctx).foreach {
      case (id: String, ctx: NodeContextImpl) =>
        val frameHandler = frameHandlerForNodeID(id)
        ctx.swapHandler(frameHandler)
      case _ =>
    }
  }

  private def frameHandlerForNodeID(id: String): FrameHandler = {
    case f: frames.Message => channel.writeAndFlush(f.copy(fromID = id))
    case f: frames.Frame => channel.writeAndFlush(f)
  }

  private def handleMessageFrame(frame: frames.Message): Unit = {
    for (node <- nodesByID.find(_._1 == frame.toID).map(_._2)) {
      val deserializer = new PayloadDeserializer(classLoader, frame)
      val msg = deserializer.get

      node.sender = node.ctx.remoteNode(frame.fromID)
      node.receive(msg)
      node.sender = null
    }
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
