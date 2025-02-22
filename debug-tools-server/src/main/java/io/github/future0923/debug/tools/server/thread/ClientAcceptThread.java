package io.github.future0923.debug.tools.server.thread;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.handler.PacketHandleService;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.server.scoket.handler.ServerPacketHandleService;
import lombok.Getter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author future0923
 */
public class ClientAcceptThread extends Thread {

    private static final Logger logger = Logger.getLogger(ClientAcceptThread.class);

    @Getter
    private final Map<ClientHandleThread, Long> lastUpdateTime2Thread = new ConcurrentHashMap<>();

    private final PacketHandleService packetHandleService = new ServerPacketHandleService();

    private ServerSocket serverSocket;

    private final CountDownLatch countDownLatch;

    public ClientAcceptThread(CountDownLatch countDownLatch) {
        setName("DebugTools-ClientAccept-Thread");
        setDaemon(true);
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(DebugToolsBootstrap.serverConfig.getTcpPort());
            int bindPort = serverSocket.getLocalPort();
            logger.info("start server trans and bind port in {}", bindPort);
            countDownLatch.countDown();
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    serverSocket.close();
                    return;
                }
                logger.info("get client conn start handle thread socket: {}", socket);
                ClientHandleThread socketHandleThread = new ClientHandleThread(socket, lastUpdateTime2Thread, packetHandleService);
                socketHandleThread.start();
                lastUpdateTime2Thread.put(socketHandleThread, System.currentTimeMillis());
            }
        } catch (Exception e) {
            logger.error("运行过程中发生异常，关闭对应链接:{}", e);
        }
    }

    public void close() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {

            }
        }
        this.interrupt();
        for (ClientHandleThread clientHandleThread : lastUpdateTime2Thread.keySet()) {
            clientHandleThread.interrupt();
        }
    }
}
