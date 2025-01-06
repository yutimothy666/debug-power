package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.hscglib;

import io.github.future0923.debug.tools.base.logging.Logger;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author future0923
 */
public class GeneratorParametersRecorder {

    private static final Logger LOGGER = Logger.getLogger(GeneratorParametersRecorder.class);

    // 在 App ClassLoader 中使用
    public static ConcurrentHashMap<String, GeneratorParams> generatorParams = new ConcurrentHashMap<>();



    /**
     *
     * @param generatorStrategy
     *            Cglib generator strategy instance that generated the bytecode
     * @param classGenerator
     *            parameter used to generate the bytecode with generatorStrategy
     * @param bytes
     *            generated bytecode
     */
    public static void register(Object generatorStrategy, Object classGenerator,
                                byte[] bytes) {
        try {
            generatorParams.putIfAbsent(getClassName(bytes),
                    new GeneratorParams(generatorStrategy, classGenerator));
        } catch (Exception e) {
            LOGGER.error(
                    "Error saving parameters of a creation of a Cglib proxy",
                    e);
        }
    }

    /**
     * http://stackoverflow.com/questions/1649674/resolve-class-name-from-bytecode
     */
    public static String getClassName(byte[] bytes) throws Exception {
        DataInputStream dis = new DataInputStream(
                new ByteArrayInputStream(bytes));
        dis.readLong(); // skip header and class version
        int cpcnt = (dis.readShort() & 0xffff) - 1;
        int[] classes = new int[cpcnt];
        String[] strings = new String[cpcnt];
        for (int i = 0; i < cpcnt; i++) {
            int t = dis.read();
            if (t == 7)
                classes[i] = dis.readShort() & 0xffff;
            else if (t == 1)
                strings[i] = dis.readUTF();
            else if (t == 5 || t == 6) {
                dis.readLong();
                i++;
            } else if (t == 8)
                dis.readShort();
            else
                dis.readInt();
        }
        dis.readShort(); // skip access flags
        return strings[classes[(dis.readShort() & 0xffff) - 1] - 1].replace('/', '.');
    }
}
