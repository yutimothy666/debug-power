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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.boot.env.v2;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.boot.env.BasePropertiesPropertySourceLoader;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.boot.env.ListPropertySourceReloader;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;


public class PropertiesPropertySourceLoader extends BasePropertiesPropertySourceLoader<List<Map<String, ?>>> {

    private static final Logger LOGGER = Logger.getLogger(PropertiesPropertySourceLoader.class);

    private org.springframework.boot.env.PropertiesPropertySourceLoader propertiesPropertySourceLoader;
    private Resource resource;


    public PropertiesPropertySourceLoader(
        org.springframework.boot.env.PropertiesPropertySourceLoader propertiesPropertySourceLoader,
                                               String name, Resource resource) {
        super(new ListPropertySourceReloader(name, resource));
        this.propertiesPropertySourceLoader = propertiesPropertySourceLoader;
        this.resource = resource;
    }

    /**
     * spring boot 2.x higher version
     *
     * @return
     */
    protected List<Map<String, ?>> doLoad() {
        return (List<Map<String, ?>>) ReflectionHelper.invoke(propertiesPropertySourceLoader, org.springframework.boot.env.PropertiesPropertySourceLoader.class,
                "loadProperties", new Class[]{Resource.class}, resource);
    }
}
