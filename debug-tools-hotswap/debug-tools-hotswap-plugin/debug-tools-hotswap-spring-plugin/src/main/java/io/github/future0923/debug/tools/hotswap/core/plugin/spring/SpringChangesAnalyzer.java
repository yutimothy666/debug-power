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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.LoaderClassPath;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.utils.ClassSignatureComparer;

/**
 * Determines if a full Spring reload is needed. Changes to synthetic and known generated classes are ignored. For other
 * classes, changes to method bodies are ignored.
 *
 * @author Erki Ehtla
 */
public class SpringChangesAnalyzer {

    private static final Logger LOGGER = Logger.getLogger(SpringPlugin.class);

    private final ClassPool cp;

    public SpringChangesAnalyzer(final ClassLoader classLoader) {
        this.cp = new ClassPool() {
            @Override
            public ClassLoader getClassLoader() {
                return classLoader;
            }
        };
        cp.appendSystemPath();
        cp.appendClassPath(new LoaderClassPath(classLoader));
    }

    public boolean isReloadNeeded(Class<?> classBeingRedefined, byte[] classfileBuffer) {
        if (classBeingRedefined.isSynthetic() || isSyntheticClass(classBeingRedefined)) {
            return false;
        }
        return classChangeNeedsReload(classBeingRedefined, classfileBuffer);
    }

    private boolean classChangeNeedsReload(Class<?> classBeingRedefined, byte[] classfileBuffer) {
        CtClass makeClass = null;
        try {
            makeClass = cp.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
            return ClassSignatureComparer.isPoolClassDifferent(classBeingRedefined, cp);
        } catch (Exception e) {
            LOGGER.error("Error analyzing class {} for reload necessity. Defaulting to yes.", e,
                    classBeingRedefined.getName());
        } finally {
            if (makeClass != null) {
                makeClass.detach();
            }
        }
        return true;
    }

    protected boolean isSyntheticClass(Class<?> classBeingRedefined) {
        return classBeingRedefined.getSimpleName().contains("$$_javassist")
                || classBeingRedefined.getName().startsWith("com.sun.proxy.$Proxy")
                || classBeingRedefined.getSimpleName().contains("$$Enhancer")
                || classBeingRedefined.getSimpleName().contains("$$_jvst") // javassist proxy
                || classBeingRedefined.getSimpleName().contains("$HibernateProxy$")
                ;
    }
}
