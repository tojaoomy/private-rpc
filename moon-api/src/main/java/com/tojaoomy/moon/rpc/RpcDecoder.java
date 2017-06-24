package com.tojaoomy.moon.rpc;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class RpcDecoder extends ByteToMessageDecoder {
	
	private Class<?> genericClass;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		//消息长度
		if(in.readableBytes() < 4){
			return;
		}
		
		in.markReaderIndex();
		int length = in.readInt();
		if(length <= 0){
			ctx.close();
		}
		
		if(in.readableBytes() < length){
			in.resetReaderIndex();
			return;
		}
		
		byte[] data = new byte[length];
		in.readBytes(data);
		
		Object result = SerializationUtil.deserialize(data, genericClass);
		out.add(result);
	}

}
