package com.trup10ka.xiba.handlers;

import com.trup10ka.xiba.XibaServer;
import com.trup10ka.xiba.XibaTimeoutDaemon;
import com.trup10ka.xiba.client.XibaClient;
import com.trup10ka.xiba.commands.Command;
import com.trup10ka.xiba.commands.CommandIdentifier;
import com.trup10ka.xiba.commands.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;

import static com.trup10ka.xiba.util.ClientUtils.PROXY_SIGN;
import static com.trup10ka.xiba.util.ClientUtils.sendErrorMessageToClient;


public class ClientCommandHandler extends ClientHandler<Integer, AsynchronousSocketChannel>
{
    private static final Logger logger = LoggerFactory.getLogger(ClientCommandHandler.class);

    private final ClientInputReaderHandler clientInputReaderHandler;

    public ClientCommandHandler(ClientInputReaderHandler clientInputReaderHandler, XibaTimeoutDaemon timeoutDaemon)
    {
        super(timeoutDaemon);
        this.clientInputReaderHandler = clientInputReaderHandler;
    }

    public void executeCommand(String command, AsynchronousSocketChannel client)
    {
        String commandCode = command.split(" ")[0];
        String commandArguments = command.substring(commandCode.length()).trim();

        CommandIdentifier identifier = CommandIdentifier.fromString(commandCode);
        if (identifier == null)
        {
            logger.error("Client {} provided invalid command identifier: {}", client, commandCode);
            sendErrorMessageToClient(client, "Invalid command identifier", clientInputReaderHandler);
            return;
        }

        Command commandInstance = CommandManager.getCommand(identifier);
        String executionResult = commandInstance.execute(commandArguments);

        if (executionResult.split("-")[0].equals(PROXY_SIGN))
            tryProxy(client, command, executionResult);
        else
            respondWithCommandResult(client, command, executionResult);
    }

    @Override
    public void completed(Integer result, AsynchronousSocketChannel client)
    {
        getTimeoutDaemon().resetTimeout(client);
        client.read(clientInputReaderHandler.getClientBuffer(), client, clientInputReaderHandler);
    }

    @Override
    public void failed(Throwable exc, AsynchronousSocketChannel attachment)
    {
        logger.error("Failed to write response to client: {}", exc.getMessage());
    }

    private void respondWithCommandResult(AsynchronousSocketChannel client, String command, String executionResult)
    {
        byte[] result = (executionResult + "\r\n").getBytes(StandardCharsets.US_ASCII);

        client.write(ByteBuffer.wrap(result), client, clientInputReaderHandler);
        logger.info("Executed command: {} for client: {}", command, client);
    }

    private void tryProxy(AsynchronousSocketChannel client, String command, String executionResult)
    {
        logger.info("Client {} requested proxying", client);
        try
        {
            XibaClient xibaClient = new XibaClient(client);
            int port = XibaClient.findBank(executionResult.split(" ")[1].split("/")[1]);
            if (port != -1)
            {
                xibaClient.executeProxyCommand(executionResult.split(" ")[1].split("/")[1], port, command);
                return;
            }

            logger.error("Failed to find available port for proxying to client request bank: {}", executionResult.split(" ")[1]);
            sendErrorMessageToClient(client, "Failed to find available port for proxying", clientInputReaderHandler);
        }
        catch (IOException e)
        {
            logger.error("Failed to create XibaClient instance: {}", e.getMessage());
            sendErrorMessageToClient(client, "Failed to create proxy client, please try again later, cannot open more connections", clientInputReaderHandler);
        }
    }
}
