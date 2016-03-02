package com.on36.haetae.http.core;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.handler.codec.http.multipart.MemoryAttribute;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import com.on36.haetae.http.Container;
import com.on36.haetae.http.request.HttpRequestExt;

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpRequest> {
	private final Container container;

	public HttpServerHandler(Container container) {
		this.container = container;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpRequest request) {

		long start = System.currentTimeMillis();
		if (HttpHeaders.is100ContinueExpected(request)) {
			ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
		}
		InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		boolean keepAlive = HttpHeaders.isKeepAlive(request);
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,NOT_FOUND, Unpooled.directBuffer());
		HttpRequestExt httpRequestExt = new HttpRequestExt(request, remoteAddress, start);
		//HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
		if (container != null)
			container.handle(httpRequestExt, response);

		response.headers().set(CONTENT_LENGTH,
				response.content().readableBytes());

		if (!keepAlive) {
			ctx.write(response).addListener(ChannelFutureListener.CLOSE);
		} else {
			response.headers().set(CONNECTION, Values.KEEP_ALIVE);
			ctx.write(response);
		}
	}

	private ByteBuf writeHttpData(InterfaceHttpData data) throws IOException {
        if (data.getHttpDataType() == HttpDataType.FileUpload) {
            FileUpload fileUpload = (FileUpload) data;
            if (fileUpload.isCompleted()) {
                  
                StringBuffer fileNameBuf = new StringBuffer();
                fileNameBuf.append(DiskFileUpload.baseDirectory);
  
                fileUpload.renameTo(new File(fileNameBuf.toString()));
                
                return fileUpload.content();
            }
        } else if (data.getHttpDataType() == HttpDataType.Attribute) {
            MemoryAttribute attribute = (MemoryAttribute) data;
            return attribute.content();
        }
        return null;
    }  
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}