package com.drunkbull.drunkbullcloudcashbook.network;

import android.util.Log;

import com.drunkbull.drunkbullcloudcashbook.protobuf.CBMessage;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;

public class CustomProtobufDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() > 3) { // 如果可读长度小于包头长度，退出。
            in.markReaderIndex();

            // 获取包头中的body长度
            byte l0 = in.readByte();
            byte l1 = in.readByte();
            byte l2 = in.readByte();
            byte l3 = in.readByte();
            int s0 = (int) (l0 & 0xff);
            int s1 = (int) (l1 & 0xff);
            int s2 = (int) (l2 & 0xff);
            int s3 = (int) (l3 & 0xff);
            s0 <<= 24;
            s1 <<= 16;
            s2 <<= 8;
            int length = (int) (s0 | s1 | s2 | s3);
            // 如果可读长度小于body长度，恢复读指针，退出。
            if (in.readableBytes() < length) {
                in.resetReaderIndex();
                return;
            }

            // 读取body
            ByteBuf bodyByteBuf = in.readBytes(length);

            byte[] array;
            int offset;

            int readableLen= bodyByteBuf.readableBytes();
            if (bodyByteBuf.hasArray()) {
                array = bodyByteBuf.array();
                offset = bodyByteBuf.arrayOffset() + bodyByteBuf.readerIndex();
            } else {
                array = new byte[readableLen];
                bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
                offset = 0;
            }

            //反序列化
            MessageLite result = decodeBody(array, offset, readableLen);
            out.add(result);
            ReferenceCountUtil.release(bodyByteBuf);
        }
    }

    public MessageLite decodeBody(byte[] array, int offset, int length) throws Exception {
        return CBMessage.Response.getDefaultInstance().getParserForType().parseFrom(array, offset, length);
    }
}
