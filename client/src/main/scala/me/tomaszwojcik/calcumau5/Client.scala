package me.tomaszwojcik.calcumau5

import io.netty.bootstrap.Bootstrap
import io.netty.channel.EventLoopGroup
import io.netty.channel.group.{ChannelGroup, DefaultChannelGroup}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import me.tomaszwojcik.calcumau5.actions.{Action, Opts}
import me.tomaszwojcik.calcumau5.util.Logging

object Client extends Logging {

  private val eventLoopGroup: EventLoopGroup = new NioEventLoopGroup(4)
  private val channelGroup: ChannelGroup = new DefaultChannelGroup(eventLoopGroup.next())

  def main(args: Array[String]): Unit = {
    val parser = new ArgsParser(args.toList)
    parser.result match {
      case (actions.Help, _) =>
      case (action: Action, opts: Opts) =>
        try {
          val bootstrap = new Bootstrap()
            .group(eventLoopGroup)
            .channel(classOf[NioSocketChannel])
            .handler(new ClientChannelInitializer(action, opts))

          for (server <- ClientConf.Servers) {
            val f = bootstrap.connect(server.host, server.port).sync()
            channelGroup.add(f.channel)
          }

          channelGroup.newCloseFuture().sync()
        } finally {
          eventLoopGroup.shutdownGracefully()
        }
    }

  }

}
