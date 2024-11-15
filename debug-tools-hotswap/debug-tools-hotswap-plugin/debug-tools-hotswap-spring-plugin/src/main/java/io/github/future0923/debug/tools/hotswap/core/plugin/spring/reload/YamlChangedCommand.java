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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.command.MergeableCommand;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;

import java.lang.reflect.Method;
import java.net.URL;

/**
 * Add changed property to SpringChangedAgent.
 */
public class YamlChangedCommand extends MergeableCommand {
    private static final Logger LOGGER = Logger.getLogger(YamlChangedCommand.class);

    ClassLoader appClassLoader;

    URL url;
    Scheduler scheduler;

    public YamlChangedCommand(ClassLoader appClassLoader, URL url, Scheduler scheduler) {
        this.appClassLoader = appClassLoader;
        this.url = url;
        this.scheduler = scheduler;
    }

    @Override
    public void executeCommand() {
        try {
            Class<?> clazz = Class.forName("io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload.SpringChangedAgent", true, appClassLoader);
            Method method = clazz.getDeclaredMethod(
                    "addChangedYaml", new Class[]{URL.class});
            method.invoke(null, url);
        } catch (Exception e) {
            throw new RuntimeException("YamlChangedCommand.execute error", e);
        }
    }
}
