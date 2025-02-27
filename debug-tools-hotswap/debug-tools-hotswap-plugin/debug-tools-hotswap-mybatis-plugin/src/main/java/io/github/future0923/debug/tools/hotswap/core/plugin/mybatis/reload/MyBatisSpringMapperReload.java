package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload;

import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.dto.MyBatisSpringMapperReloadDTO;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.utils.MyBatisUtils;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.session.Configuration;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 重新载入 mybatis spring 的 mapper 资源
 *
 * @author future0923
 */
@SuppressWarnings("unchecked")
public class MyBatisSpringMapperReload extends AbstractMyBatisResourceReload<MyBatisSpringMapperReloadDTO> {

    private static final Logger logger = Logger.getLogger(MyBatisSpringMapperReload.class);

    public static final MyBatisSpringMapperReload INSTANCE = new MyBatisSpringMapperReload();

    private static final Set<String> RELOADING_CLASS = ConcurrentHashMap.newKeySet();

    private MyBatisSpringMapperReload() {

    }

    @Override
    protected void doReload(MyBatisSpringMapperReloadDTO dto) throws Exception {
        String className = dto.getClassName();
        if (RELOADING_CLASS.contains(className)) {
            if (ProjectConstants.DEBUG) {
                logger.info("{} is currently processing reload task.", className);
            }
            return;
        }
        String loadedResource = buildLoadedResource(className);
        for (Configuration configuration : MyBatisSpringResourceManager.getConfigurationList()) {
            if (configuration.getClass().getName().equals("com.baomidou.mybatisplus.core.MybatisConfiguration")) {
                continue;
            }
            synchronized (MyBatisUtils.getReloadLockObject()) {
                if (!RELOADING_CLASS.add(className)) {
                    if (ProjectConstants.DEBUG) {
                        logger.info("{} is currently processing reload task.", className);
                    }
                    return;
                }
                Set<String> loadedResources = (Set<String>) ReflectionHelper.get(configuration, LOADED_RESOURCES_FIELD);
                loadedResources.remove(loadedResource);
                MapperRegistry mapperRegistry = (MapperRegistry) ReflectionHelper.get(configuration, "mapperRegistry");
                Map<Class<?>, MapperProxyFactory<?>> knownMappers = (Map<Class<?>, MapperProxyFactory<?>>) ReflectionHelper.get(mapperRegistry, "knownMappers");
                knownMappers.keySet().removeIf(mapperClass -> loadedResource.contains(mapperClass.getName()));
                new MapperAnnotationBuilder(configuration, Class.forName(className)).parse();
                defineBean(className, dto.getBytes());
                RELOADING_CLASS.remove(className);
            }
            logger.reload("reload {} in {}", className, configuration);
        }
    }

    private String buildLoadedResource(String className) {
        return INTERFACE + " " + className;
    }
}
