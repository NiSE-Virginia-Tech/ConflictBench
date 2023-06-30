package org.fengfei.lanproxy.server.handlers;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;
import io.netty.buffer.Unpooled;
import org.fengfei.lanproxy.protocol.Constants;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.fengfei.lanproxy.protocol.ProxyMessage;
import org.fengfei.lanproxy.server.ProxyChannelManager;
import org.fengfei.lanproxy.server.config.ProxyConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;

/**
 * 处理服务端 channel.
 */
public class UserChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {
  private static AtomicLong userIdProducer = new AtomicLong(0);

  @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    ctx.close();
  }

  @Override protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
    Channel userChannel = ctx.channel();
    Channel proxyChannel = userChannel.attr(Constants.NEXT_CHANNEL).get();
    if (proxyChannel == null) {
      ctx.writeAndFlush(Unpooled.copiedBuffer("HTTP/1.0 503 Service Unavailable\r\nContent-Length: 14\r\n\r\nCLIENT OFFLINE".getBytes())).addListener(ChannelFutureListener.CLOSE);
    } else {
      byte[] bytes = new byte[buf.readableBytes()];
      buf.readBytes(bytes);
      String userId = ProxyChannelManager.getUserChannelUserId(userChannel);
      ProxyMessage proxyMessage = new ProxyMessage();
      proxyMessage.setType(ProxyMessage.P_TYPE_TRANSFER);
      proxyMessage.setUri(userId);
      proxyMessage.setData(bytes);
      proxyChannel.writeAndFlush(proxyMessage);
    }
  }

  @Override public void channelActive(ChannelHandlerContext ctx) throws Exception {
    Channel userChannel = ctx.channel();
    InetSocketAddress sa = (InetSocketAddress) userChannel.localAddress();
    Channel cmdChannel = ProxyChannelManager.getCmdChannel(sa.getPort());
    if (cmdChannel == null) {

<<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/left/proxy-server/src/main/java/org/fengfei/lanproxy/server/handlers/UserChannelHandler.java
      FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SERVICE_UNAVAILABLE);
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/left/proxy-server/src/main/java/org/fengfei/lanproxy/server/handlers/UserChannelHandler.java
      ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
=======
>>>>>>> Unknown file: This is a bug in JDime.
    } else {
      String userId = newUserId();
      String lanInfo = ProxyConfig.getInstance().getLanInfo(sa.getPort());
      userChannel.config().setOption(ChannelOption.AUTO_READ, false);
      ProxyChannelManager.addUserChannelToCmdChannel(cmdChannel, userId, userChannel);
      ProxyMessage proxyMessage = new ProxyMessage();
      proxyMessage.setType(ProxyMessage.TYPE_CONNECT);
      proxyMessage.setUri(userId);
      proxyMessage.setData(lanInfo.getBytes());
      cmdChannel.writeAndFlush(proxyMessage);
    }
    super.channelActive(ctx);
  }

  @Override public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    Channel userChannel = ctx.channel();
    InetSocketAddress sa = (InetSocketAddress) userChannel.localAddress();
    Channel cmdChannel = ProxyChannelManager.getCmdChannel(sa.getPort());
    if (cmdChannel == null) {
      ctx.channel().close();
    } else {
      String userId = ProxyChannelManager.getUserChannelUserId(userChannel);
      ProxyChannelManager.removeUserChannelFromCmdChannel(cmdChannel, userId);
      Channel proxyChannel = userChannel.attr(Constants.NEXT_CHANNEL).get();
      if (proxyChannel != null && proxyChannel.isActive()) {
        proxyChannel.attr(Constants.NEXT_CHANNEL).remove();
        proxyChannel.attr(Constants.CLIENT_KEY).remove();
        proxyChannel.attr(Constants.USER_ID).remove();
        proxyChannel.config().setOption(ChannelOption.AUTO_READ, true);
        ProxyMessage proxyMessage = new ProxyMessage();
        proxyMessage.setType(ProxyMessage.TYPE_DISCONNECT);
        proxyMessage.setUri(userId);
        proxyChannel.writeAndFlush(proxyMessage);
      }
    }
    super.channelInactive(ctx);
  }

  @Override public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
    Channel userChannel = ctx.channel();
    InetSocketAddress sa = (InetSocketAddress) userChannel.localAddress();
    Channel cmdChannel = ProxyChannelManager.getCmdChannel(sa.getPort());
    if (cmdChannel == null) {
      ctx.channel().close();
    } else {
      Channel proxyChannel = userChannel.attr(Constants.NEXT_CHANNEL).get();
      if (proxyChannel != null) {
        proxyChannel.config().setOption(ChannelOption.AUTO_READ, userChannel.isWritable());
      }
    }
    super.channelWritabilityChanged(ctx);
  }

  /**
     * 为用户连接产生ID
     *
     * @return
     */
  private static String newUserId() {
    return String.valueOf(userIdProducer.incrementAndGet());
  }
}