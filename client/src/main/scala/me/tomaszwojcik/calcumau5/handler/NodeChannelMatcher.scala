package me.tomaszwojcik.calcumau5.handler

import io.netty.channel.Channel
import io.netty.channel.group.ChannelMatcher
import me.tomaszwojcik.calcumau5.ClientConf.{Node, Server}
import me.tomaszwojcik.calcumau5.ClientConstants

class NodeChannelMatcher(node: Node) extends ChannelMatcher {

  override def matches(channel: Channel): Boolean = serverByChannel(channel).contains(node.server)

  private def serverByChannel(channel: Channel): Option[Server] = Option {
    channel.attr(ClientConstants.ServerAttr).get()
  }

}
