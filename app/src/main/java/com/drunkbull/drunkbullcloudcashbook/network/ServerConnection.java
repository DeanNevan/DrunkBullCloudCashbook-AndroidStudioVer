package com.drunkbull.drunkbullcloudcashbook.network;

import android.util.Log;

import com.drunkbull.drunkbullcloudcashbook.protobuf.CBMessage;
import com.drunkbull.drunkbullcloudcashbook.singleton.Auth;
import com.drunkbull.drunkbullcloudcashbook.singleton.GSignalManager;
import com.drunkbull.drunkbullcloudcashbook.utils.data.DateUtil;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ServerConnection {

    private volatile static ServerConnection singleton;
    public static ServerConnection getSingleton() {
        if (singleton == null) {
            synchronized (ServerConnection.class) {
                if (singleton == null) {
                    singleton = new ServerConnection();
                }
            }
        }
        return singleton;
    }

    private ServerConnection(){
        GSignalManager.getSingleton().addGSignal(this, "connection_disconnected");
        GSignalManager.getSingleton().addGSignal(this, "connection_reconnecting");
        GSignalManager.getSingleton().addGSignal(this, "connection_connected");
        GSignalManager.getSingleton().addGSignal(this, "responsed");
    }

    //private String serverHost = "10.0.2.2";
    private String serverHost = "123.56.105.106";
    private int serverPort = 8989;

    /// 通过nio方式来接收连接和处理连接
    private EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();
    public Channel channel;

    public ChannelHandlerContext ctx;

    /**
     * Netty创建全部都是实现自AbstractBootstrap。
     * 客户端的是Bootstrap，服务端的则是    ServerBootstrap。
     **/

    public void initChannel() {
        try {
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new CustomChannelInitializer());
            bootstrap.remoteAddress(serverHost, serverPort);
            // 连接服务端
            ChannelFuture f = bootstrap.connect();
            f.addListener(new ConnectionListener());
            channel = f.channel();
        } catch (NumberFormatException e) {
            Log.e(this.getClass().getSimpleName(), "客户端连接失败，请检查ip与端口！");
        }
    }

    public void sendRequestConnect(){
        CBMessage.Request.Builder requestBuilder = CBMessage.Request.newBuilder();
        requestBuilder.setType(CBMessage.Type.CONNECT);
        CBMessage.RequestConnect.Builder requestConnectBuilder = CBMessage.RequestConnect.newBuilder();
        requestBuilder.setRequestConnect(requestConnectBuilder.build());
        sendRequest(requestBuilder);
    }

    public void sendRequestHeartBeat(){
        CBMessage.Request.Builder requestBuilder = CBMessage.Request.newBuilder();
        requestBuilder
                .setTip("ping")
                .setType(CBMessage.Type.HEARTBEAT);
        sendRequest(requestBuilder);
    }



    public boolean sendRequest(CBMessage.Request.Builder builder){
        if (channel == null) return false;
        if (!channel.isWritable()) return false;

        builder
                .setTimestamp(DateUtil.getTimeStamp())
                .setClientId(Auth.getSingleton().clientID);

        //Log.d(this.getClass().getSimpleName(), "sendRequest:" + builder.build().toString());
        //Log.d(this.getClass().getSimpleName(), "ctx:" + ctx);

        RequestWriter.writeRequest(channel, builder);
        return true;

    }

    public void doConnect() {
        ChannelFuture f = null;
        try {
            if (bootstrap != null) {
                f = bootstrap.connect().addListener(new ConnectionListener());
                channel = f.channel();
            }
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "客户端连接失败!");
        }

    }


}
