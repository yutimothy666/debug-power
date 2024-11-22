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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;
import io.github.future0923.debug.tools.hotswap.core.command.Command;
import io.github.future0923.debug.tools.hotswap.core.command.MergeableCommand;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.util.IOUtils;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchFileEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 通过byte[]或者URI刷新SpringBean命令，相同的可以merge
 */
public class ClassPathBeanRefreshCommand extends MergeableCommand {

    private static final Logger LOGGER = Logger.getLogger(ClassPathBeanRefreshCommand.class);

    ClassLoader appClassLoader;

    String basePackage;

    String className;

    // either event or classDefinition is set by constructor (watcher or transformer)
    WatchFileEvent event;
    byte[] classDefinition;

    Scheduler scheduler;

    public ClassPathBeanRefreshCommand(ClassLoader appClassLoader, String basePackage, String className,
                                       byte[] classDefinition, Scheduler scheduler) {
        this.appClassLoader = appClassLoader;
        this.basePackage = basePackage;
        this.className = className;
        this.classDefinition = classDefinition;
        this.scheduler = scheduler;
    }

    public ClassPathBeanRefreshCommand(ClassLoader appClassLoader, String basePackage, String className,
                                       WatchFileEvent event, Scheduler scheduler) {
        this.appClassLoader = appClassLoader;
        this.basePackage = basePackage;
        this.event = event;
        this.className = className;
        this.scheduler = scheduler;
    }

    @Override
    public void executeCommand() {
        if (isDeleteEvent()) {
            LOGGER.trace("Skip Spring reload for delete event on class '{}'", className);
            return;
        }

        try {
            if (classDefinition == null) {
                try {
                    this.classDefinition = IOUtils.toByteArray(event.getURI());
                } catch (IllegalArgumentException e) {
                    LOGGER.debug("File {} not found on filesystem (deleted?). Unable to refresh associated Spring bean.", event.getURI());
                    return;
                }
            }

            LOGGER.debug("Executing ClassPathBeanDefinitionScannerAgent.refreshClass('{}')", className);

            Class<?> clazz = Class.forName("io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.ClassPathBeanDefinitionScannerAgent", true, appClassLoader);
            Method method  = clazz.getDeclaredMethod("refreshClassAndCheckReload", ClassLoader.class, String.class, String.class, byte[].class);
            method.invoke(null, appClassLoader , basePackage, basePackage, classDefinition);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Plugin error, method not found", e);
        } catch (InvocationTargetException e) {
            LOGGER.error("Error refreshing class {} in classLoader {}", e, className, appClassLoader);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Plugin error, illegal access", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Plugin error, Spring class not found in application classloader", e);
        }

    }

    /**
     * Check all merged events for delete and create events. If delete without create is found, than assume
     * file was deleted.
     */
    private boolean isDeleteEvent() {
        List<ClassPathBeanRefreshCommand> mergedCommands = new ArrayList<>();
        for (Command command : getMergedCommands()) {
            mergedCommands.add((ClassPathBeanRefreshCommand) command);
        }
        mergedCommands.add(this);
        boolean createFound = false;
        boolean deleteFound = false;
        for (ClassPathBeanRefreshCommand command : mergedCommands) {
            if (command.event != null) {
                if (command.event.getEventType().equals(FileEvent.CREATE)) {
                    createFound = true;
                }
                if (command.event.getEventType().equals(FileEvent.DELETE)) {
                    deleteFound = true;
                }
            }
        }
        LOGGER.trace("isDeleteEvent result {}: createFound={}, deleteFound={}", createFound, deleteFound);
        return !createFound && deleteFound;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassPathBeanRefreshCommand that = (ClassPathBeanRefreshCommand) o;

        if (!appClassLoader.equals(that.appClassLoader)) return false;
        if (!className.equals(that.className)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = appClassLoader.hashCode();
        result = 31 * result + className.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ClassPathBeanRefreshCommand{" +
                "appClassLoader=" + appClassLoader +
                ", basePackage='" + basePackage + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}
