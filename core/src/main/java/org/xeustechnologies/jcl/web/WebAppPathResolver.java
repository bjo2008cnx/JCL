/**
 * Copyright 2015 Kamran Zafar
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xeustechnologies.jcl.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeustechnologies.jcl.utils.PathResolver;

import javax.servlet.ServletContext;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 解析webapp中的jar. PATH 应以webapp:开头</b>
 *
 * @author Kamran
 */
public class WebAppPathResolver implements PathResolver {

    private final transient Logger logger = LoggerFactory.getLogger(WebAppPathResolver.class);

    private static final String JAR = ".jar";
    private static final String WEB_APP = "webapp:";
    private final ServletContext servletContext;

    public WebAppPathResolver(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * 加载webapp下的jar包
     *
     * @see PathResolver#resolvePath(String)
     */
    public InputStream[] resolvePath(String path) {
        if (path.startsWith(WEB_APP)) {
            String webpath = "/" + path.split(":")[1];
            //如果是jar,直接返回
            if (isJar(webpath)) {
                logger.debug("Found jar: {}", webpath);
                return new InputStream[]{servletContext.getResourceAsStream(webpath)};
            }

            //如果是文件夹，返回全部jar
            Set<String> paths = servletContext.getResourcePaths(webpath);
            List<InputStream> streams = new ArrayList();

            Iterator<String> itr = paths.iterator();
            while (itr.hasNext()) {
                String source = itr.next();
                if (isJar(source)) {
                    InputStream stream = servletContext.getResourceAsStream(source);
                    if (stream != null) {
                        logger.debug("Found jar: {}", source);
                        streams.add(stream);
                    }
                }
            }
            return streams.toArray(new InputStream[streams.size()]);
        }

        return null;
    }

    private boolean isJar(String path) {
        return path.toLowerCase().endsWith(JAR);
    }
}
