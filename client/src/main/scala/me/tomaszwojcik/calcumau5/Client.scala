package me.tomaszwojcik.calcumau5

import io.netty.bootstrap.Bootstrap
import io.netty.channel.EventLoopGroup
import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import me.tomaszwojcik.calcumau5.actions.{Action, Opts}
import me.tomaszwojcik.calcumau5.util.Logging

object Client extends Logging {

  private val eventLoopGroup: EventLoopGroup = new NioEventLoopGroup(4)

  def main(args: Array[String]): Unit = {
    val parser = new ArgsParser(args.toList)
    parser.result match {
      case (actions.Help, _) =>
      case (action: Action, opts: Opts) => start(action, opts)
    }
  }

  private def start(action: Action, opts: Opts): Unit = {
    val channels = new DefaultChannelGroup(eventLoopGroup.next())

    val bootstrap = new Bootstrap()
      .group(eventLoopGroup)
      .channel(classOf[NioSocketChannel])
      .handler(new ClientChannelInitializer(action, opts, channels))

    for (server <- ClientConf.Servers) {
      bootstrap
        .attr(ClientConstants.ServerAttr, server)
        .connect(server.host, server.port).sync()
    }

    channels.newCloseFuture().sync()
  }

}
