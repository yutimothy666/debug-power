package io.github.future0923.debug.power.server;

import io.github.future0923.debug.power.base.config.AgentArgs;
import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.common.utils.DebugPowerClassUtils;
import io.github.future0923.debug.power.server.config.ServerConfig;
import io.github.future0923.debug.power.server.jvm.VmToolsUtils;

import java.lang.instrument.Instrumentation;

/**
 * @author future0923
 */
public class DebugPowerBootstrap {

    private static final Logger logger = Logger.getLogger(DebugPowerBootstrap.class);

    private static DebugPowerBootstrap debugBootstrap;

    private DebugPowerSocketServer socketServer;

    public static final ServerConfig serverConfig = new ServerConfig();

    private Integer port;

    private DebugPowerBootstrap(Instrumentation instrumentation, ClassLoader classloader) {
        DebugPowerClassUtils.setClassLoader(classloader);
        VmToolsUtils.init();
    }

    public static synchronized DebugPowerBootstrap getInstance(Instrumentation instrumentation, ClassLoader classloader) {
        if (debugBootstrap == null) {
            debugBootstrap = new DebugPowerBootstrap(instrumentation, classloader);
        }
        return debugBootstrap;
    }

    public void start(String agentArgs) {
        AgentArgs parse = AgentArgs.parse(agentArgs);
        int listenPort = Integer.parseInt(parse.getListenPort());
        serverConfig.setApplicationName(parse.getApplicationName());
        serverConfig.setPort(listenPort);
        if (socketServer == null) {
            socketServer = new DebugPowerSocketServer();
            socketServer.start();
        } else if (port != null && listenPort != port) {
            logger.error("The two ports are inconsistent. Stopping port {}, preparing to start port {}", port, listenPort);
            socketServer.close();
            socketServer = new DebugPowerSocketServer();
            socketServer.start();
        }
        port = listenPort;
    }
}
