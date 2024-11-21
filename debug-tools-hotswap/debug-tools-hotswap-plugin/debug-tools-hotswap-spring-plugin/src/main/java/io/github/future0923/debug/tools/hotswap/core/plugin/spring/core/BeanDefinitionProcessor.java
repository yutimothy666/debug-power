/*
 * Copyright 2013-2024 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.core;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.SpringPlugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.files.XmlBeanDefinitionScannerAgent;
import io.github.future0923.debug.tools.hotswap.core.util.spring.util.ObjectUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * {@link SpringPlugin#register(CtClass)}时 修改 registerBeanDefinition 方法，让 XmlBeanDefinitionScannerAgent 可以追踪 Xml 配置中 Bean 的定义
 */
public class BeanDefinitionProcessor {

    private static final Logger LOGGER = Logger.getLogger(BeanDefinitionProcessor.class);

    public static void registerBeanDefinition(DefaultListableBeanFactory defaultListableBeanFactory, String beanName, BeanDefinition beanDefinition) {
        LOGGER.debug("register new BeanDefinition '{}' into '{}'", beanName, ObjectUtils.identityToString(defaultListableBeanFactory));
        XmlBeanDefinitionScannerAgent.registerBean(beanName, beanDefinition);
    }

    public static void removeBeanDefinition(DefaultListableBeanFactory defaultListableBeanFactory, String beanName) {
        LOGGER.debug("remove BeanDefinition '{}' from '{}'", beanName, ObjectUtils.identityToString(defaultListableBeanFactory));
    }
}
