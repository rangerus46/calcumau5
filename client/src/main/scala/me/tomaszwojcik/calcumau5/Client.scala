package me.tomaszwojcik.calcumau5

import io.netty.bootstrap.Bootstrap
import io.netty.channel.group.{ChannelGroup, DefaultChannelGroup}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.{ChannelInitializer, EventLoopGroup}
import me.tomaszwojcik.calcumau5.util.Logging

object Client extends Logging {

  private val eventLoopGroup: EventLoopGroup = new NioEventLoopGroup(4)
  private val channelGroup: ChannelGroup = new DefaultChannelGroup(eventLoopGroup.next())
  private val channelInitializer: ChannelInitializer[SocketChannel] = new ClientChannelInitializer

  private val bootstrap = new Bootstrap()
    .group(eventLoopGroup)
    .channel(classOf[NioSocketChannel])
    .handler(channelInitializer)

  def main(args: Array[String]): Unit = try {
    for (node <- ClientConf.Nodes) {
      val f = bootstrap.connect(node.host, node.port).sync()
      channelGroup.add(f.channel)
    }
    channelGroup.newCloseFuture().sync()
  } finally {
    eventLoopGroup.shutdownGracefully()
  }

}
