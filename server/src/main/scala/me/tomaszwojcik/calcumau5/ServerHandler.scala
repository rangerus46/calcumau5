package me.tomaszwojcik.calcumau5

import java.io.{File, FileOutputStream}
import java.net.{InetSocketAddress, URLClassLoader}

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{Channel, ChannelHandlerContext, SimpleChannelInboundHandler}
import me.tomaszwojcik.calcumau5.api.Node
import me.tomaszwojcik.calcumau5.events.EventHandler
import me.tomaszwojcik.calcumau5.exceptions.JarNotDeployedException
import me.tomaszwojcik.calcumau5.frames.Frame
import me.tomaszwojcik.calcumau5.impl.ContextImpl
import me.tomaszwojcik.calcumau5.util.Logging

import scala.annotation.tailrec

@Sharable
class ServerHandler
  extends SimpleChannelInboundHandler[Frame]
    with Logging {

  var channel: Channel = _
  var nodeHolders: List[NodeHolder] = Nil

  val activeJarFileLock = new Object
  var activeJarFile: File = _
  var classLoader: URLClassLoader = _

  val eventHandler: EventHandler = { evt => channel.pipeline.fireUserEventTriggered(evt) }

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

    nodeHolders = createInstances(frame.nodes)

    @tailrec def createInstances(nodes: Map[String, String], instances: List[NodeHolder] = Nil): List[NodeHolder] = {
      nodes.headOption match {
        case Some((id, className)) =>
          val c = classLoader.loadClass(className)
          val instance = c.newInstance().asInstanceOf[Node]
          createInstances(nodes.tail, NodeHolder(id, instance) :: instances)
        case None =>
          instances
      }
    }

    for (holder <- nodeHolders) {
      val nodeCtx = holder.node.ctx.asInstanceOf[ContextImpl]
      nodeCtx.init(new ReadyContextState(holder.id, eventHandler))
    }

  }

  private def handleMessageFrame(ctx: ChannelHandlerContext, frame: frames.Message): Unit = {
    val payload = Serializers.deserializePayload(frame.payload, classLoader)
    val evt = events.InboundMessage(payload, frame.toID, frame.fromID)
    ctx.pipeline.fireUserEventTriggered(evt)
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

  override def userEventTriggered(ctx: ChannelHandlerContext, evt: AnyRef): Unit = evt match {
    case events.InboundMessage(payload, to, _) => // TODO: set sender
      for (node <- nodeHolders.find(_.id == to).map(_.node)) {
        node.receive(payload)
      }

    case events.OutboundMessage(payload, to, from) =>
      val frame = frames.Message(from, to, payload = Serializers.serializePayload(payload))
      ctx.writeAndFlush(frame)

    case _ =>
      super.userEventTriggered(ctx, evt)
  }

}
