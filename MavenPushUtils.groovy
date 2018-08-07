/*
 * Copyright 2018 Vorlonsoft LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

final class MavenPushUtils {

    private static volatile MavenPushUtils singleton = null

    private final def project

    private MavenPushUtils(project) {
        this.project = project
    }

    static MavenPushUtils with(project) {
        if (singleton == null) {
            synchronized (MavenPushUtils.class) {
                if (singleton == null) {
                    singleton = new MavenPushUtils(project)
                }
            }
        }
        return singleton
    }

    static String getJavaAPISpecificationLink(String currentJavaVersion) {
        switch (currentJavaVersion) {
            case '1.5':
                return 'https://docs.oracle.com/javase/1.5.0/docs/api/'
            case '1.6':
                return 'https://docs.oracle.com/javase/6/docs/api/'
            case '1.7':
                return 'https://docs.oracle.com/javase/7/docs/api/'
            case '1.8':
                return 'https://docs.oracle.com/javase/8/docs/api/'
            case '1.9':
                return 'https://docs.oracle.com/javase/9/docs/api/overview-summary.html'
            case '1.10':
            case '11':
            case '12':
            case '13':
            case '14':
                return 'https://docs.oracle.com/javase/10/docs/api/overview-summary.html'
            default:
                return ''
        }
    }

    static void pomFinalizer(def pom, def pomPackagingConfig, def pomConfig) {
        pom.withXml {
            final def root = asNode()
            final def name = root.name
            final def description = root.description
            if (root.packaging.size() == 1) {
                root.remove(root.packaging)
            }
            root.remove(root.name)
            root.remove(root.description)
            root.children().last() + pomPackagingConfig
            root.append(name)
            root.append(description)
            root.children().last() + pomConfig
            if (root.dependencies.size() == 1) {
                final def dependencies = root.dependencies
                root.remove(root.dependencies)
                root.append(dependencies)
            }
            if (root.repositories.size() == 1) {
                final def repositories = root.repositories
                root.remove(root.repositories)
                root.append(repositories)
            }
        }
    }

    static void downloadLib(String url, String path, String version, String name) {
        File file = new File("${System.properties['user.home']}/.m2/repository/${path}/${version}/${name}")
        file.parentFile.mkdirs()
        if (!file.exists()) {
            new URL(url).withInputStream { downloadStream ->
                file.withOutputStream { fileOutputStream ->
                    fileOutputStream << downloadStream
                }
            }
        }
    }

    boolean isAndroid() {
        return project.getPlugins().hasPlugin('com.android.application') ||
                project.getPlugins().hasPlugin('com.android.library') ||
                project.getPlugins().hasPlugin('android') ||
                project.getPlugins().hasPlugin('android-library')
    }

    boolean isKotlin() {
        return project.getPlugins().hasPlugin('kotlin') ||
                project.getPlugins().hasPlugin('kotlin-platform-common') ||
                project.getPlugins().hasPlugin('kotlin-platform-jvm') ||
                project.getPlugins().hasPlugin('kotlin-platform-js') ||
                project.getPlugins().hasPlugin('org.jetbrains.kotlin') ||
                project.getPlugins().hasPlugin('org.jetbrains.kotlin.jvm') ||
                project.getPlugins().hasPlugin('org.jetbrains.kotlin.js') ||
                project.getPlugins().hasPlugin('kotlin2js') ||
                project.getPlugins().hasPlugin('kotlin-android') ||
                project.getPlugins().hasPlugin('kotlin-android-extensions')
    }

    boolean isDokkaPlugin() {
        return project.getPlugins().hasPlugin('org.jetbrains.dokka-android') ||
                project.getPlugins().hasPlugin('org.jetbrains.dokka')
    }
}