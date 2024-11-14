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
package io.github.future0923.debug.tools.hotswap.core.watch;

import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;

import java.net.URI;

/**
 * 文件变化事件
 */
public interface WatchFileEvent {

    /**
     * 获取事件类型
     */
    FileEvent getEventType();

    /**
     * 获取事件变化时的URI
     */
    URI getURI();

    /**
     * URI是否是文件
     */
    boolean isFile();

    /**
     * URI是否是文件夹
     */
    boolean isDirectory();
}
