package com.drunkbull.drunkbullcloudcashbook.network;


import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.drunkbull.drunkbullcloudcashbook.R;
import com.drunkbull.drunkbullcloudcashbook.protobuf.CBMessage;
import com.drunkbull.drunkbullcloudcashbook.singleton.Auth;
import com.drunkbull.drunkbullcloudcashbook.singleton.GSignalManager;
import com.drunkbull.drunkbullcloudcashbook.singleton.NoSuchGSignalException;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.util.ReferenceCountUtil;

public class MessageHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Log.i("通道注册", ctx.toString());
        super.channelRegistered(ctx);
    }

    /**
     * 建立连接时
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ServerConnection.getSingleton().ctx = ctx;
        Log.i("客户端与服务器建立连接", ctx.toString());
        ctx.fireChannelActive();
        GSignalManager.getSingleton().emitGSignal(ServerConnection.getSingleton(), "connection_connected");
        Log.i("!!!!!!!!!!!!!!", "");
    }

    /**
     * 关闭连接时
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        GSignalManager.getSingleton().emitGSignal(ServerConnection.getSingleton(), "connection_disconnected");
        Log.e("客户端失去服务端连接",ctx.toString());
        //使用过程中断线重连
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                Log.i("客户端尝试重新连接服务器", "");
                ServerConnection.getSingleton().doConnect();
            }
        }, 1L, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Log.e("客户端异常,异常信息:{}", cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

    //业务逻辑处理
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //非protobuf格式数据
        if (!(msg instanceof CBMessage.Response)) {
            Log.e("channelRead", msg.toString());
            return;
        }
        try {
            CBMessage.Response message = (CBMessage.Response) msg;

            switch (message.getType()){
                default:
                    break;
            }

            GSignalManager.getSingleton().emitGSignal(ServerConnection.getSingleton(), "responsed", CBMessage.Response.class, message);
        }catch (Exception e) {
            Log.e("channelRead", e.getMessage());
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

}
