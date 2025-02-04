package com.trup10ka.xiba.handler;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ClientInputHandler implements CompletionHandler<Void, AsynchronousSocketChannel>
{
    @Override
    public void completed(Void result, AsynchronousSocketChannel attachment)
    {

    }

    @Override
    public void failed(Throwable exc, AsynchronousSocketChannel attachment)
    {

    }
}
