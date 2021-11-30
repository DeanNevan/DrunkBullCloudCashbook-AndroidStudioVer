package com.drunkbull.drunkbullcloudcashbook.network;


import android.util.Log;

import com.drunkbull.drunkbullcloudcashbook.singleton.GSignalManager;
import com.drunkbull.drunkbullcloudcashbook.singleton.NoSuchGSignalException;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;

public class ConnectionListener implements ChannelFutureListener {

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (!channelFuture.isSuccess()) {
            final EventLoop loop = channelFuture.channel().eventLoop();
            loop.schedule(()->{
                Log.e(this.getClass().getSimpleName(), "客户端连接服务器失败，开始重连操作...");
                ServerConnection.getSingleton().doConnect();
            }, 5L, TimeUnit.SECONDS);
            Log.e(this.getClass().getSimpleName(), "客户端连接服务器失败，开始重连操作...");
            try {
                GSignalManager.getSingleton().emitGSignal(ServerConnection.getSingleton(), "connection_reconnecting");
            } catch (NoSuchGSignalException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(this.getClass().getSimpleName(), "客户端连接服务器成功！");
        }
    }

}
