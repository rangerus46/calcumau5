package me.tomaszwojcik.calcumau5

import io.netty.bootstrap.Bootstrap
import io.netty.channel.group.{ChannelGroupFuture, ChannelGroupFutureListener, DefaultChannelGroup}
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
    val threadsToUse = Runtime.getRuntime.availableProcessors() * 3
    val eventLoopGroup: EventLoopGroup = new NioEventLoopGroup(threadsToUse)
    log.info(s"Using max $threadsToUse threads")

    val channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE)

    val bootstrap = new Bootstrap()
      .group(eventLoopGroup)
      .channel(classOf[NioSocketChannel])
      .handler(new ClientChannelInitializer(action, opts, channels))
      .option(ChannelOption.SO_KEEPALIVE, java.lang.Boolean.TRUE)

    for (server <- ClientConf.Servers) {
      bootstrap
        .attr(ClientConstants.ServerAttr, server)
        .connect(server.host, server.port)
        .sync()
    }

    val f = channels.newCloseFuture()

    f.addListener(new ChannelGroupFutureListener {
      override def operationComplete(future: ChannelGroupFuture) = {
        log.info("Gracefully shutting down...")
        eventLoopGroup.shutdownGracefully()
      }
    })

    f.sync()
  }

}
