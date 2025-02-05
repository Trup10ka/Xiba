package com.trup10ka.xiba.handlers;

import com.trup10ka.xiba.XibaTimeoutDaemon;
import com.trup10ka.xiba.commands.Command;
import com.trup10ka.xiba.commands.CommandIdentifier;
import com.trup10ka.xiba.commands.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;

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
        byte[] result = (commandInstance.execute(commandArguments) + "\r\n").getBytes(StandardCharsets.US_ASCII);
        logger.info("Resolved command: {} to command instance: {}", command, commandInstance);

        client.write(ByteBuffer.wrap(result), client, clientInputReaderHandler);
        logger.info("Executed command: {} for client: {}", command, client);
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
}
