package me.tomaszwojcik.calcumau5

import java.lang.Boolean

import io.netty.bootstrap.Bootstrap
import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.{ChannelOption, EventLoopGroup}
import io.netty.util.concurrent.GlobalEventExecutor
import me.tomaszwojcik.calcumau5.actions.{Action, Opts}
import me.tomaszwojcik.calcumau5.util.Logging

object Client extends Logging {

  def main(args: Array[String]): Unit = {
    val parser = new ArgsParser(args.toList)
    parser.result match {
      case (actions.Help, _) =>
      case (action: Action, opts: Opts) => start(action, opts)
    }
  }

  private def start(action: Action, opts: Opts): Unit = {
    val eventLoopGroup: EventLoopGroup = new NioEventLoopGroup(4)

    try {

      val channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE)

      val bootstrap = new Bootstrap()
        .group(eventLoopGroup)
        .channel(classOf[NioSocketChannel])
        .handler(new ClientChannelInitializer(action, opts, channels))
        .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)

      for (server <- ClientConf.Servers) {
        bootstrap
          .attr(ClientConstants.ServerAttr, server)
          .connect(server.host, server.port)
          .sync()
      }

      channels.newCloseFuture().sync()
    } finally {
      log.info("Gracefully shutting down...")
      eventLoopGroup.shutdownGracefully()
    }
  }

}
