package com.drunkbull.drunkbullcloudcashbook.network;

import android.util.Log;

import com.drunkbull.drunkbullcloudcashbook.protobuf.CBMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class RequestWriter {
    public static void writeRequest(Channel channel, CBMessage.Request.Builder builder){
        byte bytes[] = builder.build().toByteArray();
        ByteBuf buf = Unpooled.buffer(3 + bytes.length);
        buf.writeBytes(bytes);
        buf.writeBytes("$_$".getBytes());
        channel.writeAndFlush(buf);
    }
}
