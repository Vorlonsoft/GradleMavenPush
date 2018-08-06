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

final class MavenPush {

    private static volatile MavenPush singleton = null

    private final def project

    private MavenPush(project) {
        this.project = project
    }

    static MavenPush with(project) {
        if (singleton == null) {
            synchronized (MavenPush.class) {
                if (singleton == null) {
                    singleton = new MavenPush(project)
                }
            }
        }
        return singleton
    }

    static String getJavaAPISpecificationLink() {
        if (JavaVersion.current().isJava10Compatible()) {
            return 'https://docs.oracle.com/javase/10/docs/api/overview-summary.html'
        } else if (JavaVersion.current().isJava9()) {
            return 'https://docs.oracle.com/javase/9/docs/api/overview-summary.html'
        } else if (JavaVersion.current().isJava8()) {
            return 'https://docs.oracle.com/javase/8/docs/api/'
        } else if (JavaVersion.current().isJava7()) {
            return 'https://docs.oracle.com/javase/7/docs/api/'
        } else if (JavaVersion.current().isJava6()) {
            return 'https://docs.oracle.com/javase/6/docs/api/'
        } else if (JavaVersion.current().isJava5()) {
            return 'https://docs.oracle.com/javase/1.5.0/docs/api/'
        } else {
            return ''
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

    boolean isJCenter() {
        return (project.hasProperty('IS_JCENTER') && 'true'.equalsIgnoreCase(project.IS_JCENTER))
    }

    boolean isReleaseBuild() {
        return !getPomVersionName().contains('SNAPSHOT')
    }

    String getReleaseRepositoryUrl() {
        if (project.hasProperty('RELEASE_REPOSITORY_URL')) {
            return project.RELEASE_REPOSITORY_URL
        } else if (isJCenter()) {
            // https://bintray.com/api/v1/maven/{project.NEXUS_USERNAME}/maven/{project.POM_ARTIFACT_ID}/;publish=1
            return 'https://bintray.com/api/v1/maven/' +
                    getRepositoryUsername() +
                    '/maven/' +
                    getPomArtifactUrl() +
                    '/;publish=1'
        } else {
            return 'https://oss.sonatype.org/service/local/staging/deploy/maven2/'
        }
    }

    String getSnapshotRepositoryUrl() {
        if (project.hasProperty('SNAPSHOT_REPOSITORY_URL')) {
            return project.SNAPSHOT_REPOSITORY_URL
        } else if (isJCenter()) {
            return 'https://oss.jfrog.org/artifactory/oss-snapshot-local/'
        } else {
            return 'https://oss.sonatype.org/content/repositories/snapshots/'
        }
    }

    String getRepositoryUsername() {
        if (System.getenv().containsKey('NEXUS_USERNAME')) {
            return System.getenv('NEXUS_USERNAME')
        } else {
            return project.hasProperty('NEXUS_USERNAME') ? project.NEXUS_USERNAME : ''
        }
    }

    String getRepositoryPassword() {
        if (System.getenv().containsKey('NEXUS_PASSWORD')) {
            return System.getenv('NEXUS_PASSWORD')
        } else {
            return project.hasProperty('NEXUS_PASSWORD') ? project.NEXUS_PASSWORD : ''
        }
    }

    boolean getApklibArtifact() {
        return project.hasProperty('APKLIB_ARTIFACT') ? 'true'.equalsIgnoreCase(project.APKLIB_ARTIFACT) : false
    }

    boolean getAndroidJarArtifact() {
        return project.hasProperty('ANDROID_JAR_ARTIFACT') ? 'true'.equalsIgnoreCase(project.ANDROID_JAR_ARTIFACT) : false
    }

    String getAndroidJarMainClass() {
        return project.hasProperty('ANDROID_JAR_MAIN_CLASS') ? project.ANDROID_JAR_MAIN_CLASS : ''
    }

    boolean getFatjarArtifact() {
        return project.hasProperty('FATJAR_ARTIFACT') ? 'true'.equalsIgnoreCase(project.FATJAR_ARTIFACT) : false
    }

    boolean isDokka() {
        return project.hasProperty('JAVADOC_BY_DOKKA') ? 'true'.equalsIgnoreCase(project.JAVADOC_BY_DOKKA) : false
    }

    boolean getDoclintCheck() {
        return project.hasProperty('DOCLINT_CHECK') ? 'true'.equalsIgnoreCase(project.DOCLINT_CHECK) : false
    }

    String getJavadocEncoding() {
        return project.hasProperty('JAVADOC_ENCODING') ? project.JAVADOC_ENCODING : 'UTF-8'
    }

    String getJavadocDocEncoding() {
        return project.hasProperty('JAVADOC_DOC_ENCODING') ? project.JAVADOC_DOC_ENCODING : 'UTF-8'
    }

    String getJavadocCharSet() {
        return project.hasProperty('JAVADOC_CHARSET') ? project.JAVADOC_CHARSET : 'UTF-8'
    }

    /**
     * HTML version in the document comments.
     *
     * @return boolean true if HTML version in the document comments is 5, false if something else.
     */
    boolean isHtml5() {
        return project.hasProperty('JAVADOC_HTML_VERSION') ? (project.JAVADOC_HTML_VERSION == '5') : false
    }

    String getPomGroupId() {
        if (project.hasProperty('GROUP')) {
            return project.GROUP
        } else if (isAndroid() && (project.android.libraryVariants != null) && (project.android.libraryVariants.size() > 0)) {
            return project.android.libraryVariants[0].applicationId
        } else {
            throw new InvalidUserDataException('You must set GROUP in gradle.properties file.')
        }
    }

    String getPomArtifactId() {
        if (project.hasProperty('POM_ARTIFACT_ID')) {
            return project.POM_ARTIFACT_ID
        } else {
            throw new InvalidUserDataException('You must set POM_ARTIFACT_ID in gradle.properties file.')
        }
    }

    String getPomArtifactUrl() {
        return project.hasProperty('POM_ARTIFACT_URL') ? project.POM_ARTIFACT_URL : getPomArtifactId()
    }

    String getPomVersionName() {
        final String versionNameExtras = (System.getenv().containsKey('VERSION_NAME_EXTRAS')) ? System.getenv('VERSION_NAME_EXTRAS') : ''
        if (project.hasProperty('VERSION_NAME')) {
            return project.VERSION_NAME + versionNameExtras
        } else if (isAndroid() && (project.android.defaultConfig.versionName != null)) {
            return project.android.defaultConfig.versionName + versionNameExtras
        } else {
            throw new InvalidUserDataException('You must set VERSION_NAME in gradle.properties file.')
        }
    }

    String getPomPackaging() {
        if (project.hasProperty('POM_PACKAGING')) {
            return project.POM_PACKAGING
        } else {
            return isAndroid() ? 'aar' : 'jar'
        }
    }

    String getPomName() {
        if (project.hasProperty('POM_NAME')) {
            return project.POM_NAME
        } else {
            throw new InvalidUserDataException('You must set POM_NAME in gradle.properties file.')
        }
    }

    String getPomDescription() {
        if (project.hasProperty('POM_DESCRIPTION')) {
            return project.POM_DESCRIPTION
        } else {
            throw new InvalidUserDataException('You must set POM_DESCRIPTION in gradle.properties file.')
        }
    }

    boolean getPomUniqueVersion() {
        return project.hasProperty('POM_GENERATE_UNIQUE_SNAPSHOTS') ? 'true'.equalsIgnoreCase(project.POM_GENERATE_UNIQUE_SNAPSHOTS) : true
    }

    String getPomUrl() {
        if (project.hasProperty('POM_URL')) {
            return project.POM_URL
        } else {
            throw new InvalidUserDataException('You must set POM_URL in gradle.properties file.')
        }
    }

    String getPomInceptionYear() {
        return project.hasProperty('POM_INCEPTION_YEAR') ? project.POM_INCEPTION_YEAR : ''
    }

    String getPomScmUrl() {
        return project.hasProperty('POM_SCM_URL') ? project.POM_SCM_URL : getPomUrl()
    }

    String getPomScmConnection() {
        if (project.hasProperty('POM_SCM_CONNECTION')) {
            return project.POM_SCM_CONNECTION
        } else {
            throw new InvalidUserDataException('You must set POM_SCM_CONNECTION in gradle.properties file.')
        }
    }

    String getPomScmDevConnection() {
        return project.hasProperty('POM_SCM_DEV_CONNECTION') ? project.POM_SCM_DEV_CONNECTION : getPomScmConnection()
    }

    String getPomScmTag() {
        return project.hasProperty('POM_SCM_TAG') ? project.POM_SCM_TAG : 'HEAD'
    }

    String getPomLicenseName() {
        if (project.hasProperty('POM_LICENCE_NAME')) {
            return project.POM_LICENCE_NAME
        } else {
            throw new InvalidUserDataException('You must set POM_LICENCE_NAME in gradle.properties file.')
        }
    }

    String getPomLicenseUrl() {
        if (project.hasProperty('POM_LICENCE_URL')) {
            return project.POM_LICENCE_URL
        } else {
            throw new InvalidUserDataException('You must set POM_LICENCE_URL in gradle.properties file.')
        }
    }

    String getPomLicenseDist() {
        return project.hasProperty('POM_LICENCE_DIST') ? project.POM_LICENCE_DIST : 'repo'
    }

    String getPomLicenseComments() {
        return project.hasProperty('POM_LICENCE_COMMENTS') ? project.POM_LICENCE_COMMENTS : ''
    }

    String getOrg() {
        return project.hasProperty('POM_ORG') ? project.POM_ORG : ''
    }

    String getOrgUrl() {
        return project.hasProperty('POM_ORG_URL') ? project.POM_ORG_URL : ''
    }

    String getDeveloperId() {
        if (project.hasProperty('POM_DEVELOPER_ID')) {
            return project.POM_DEVELOPER_ID
        } else {
            throw new InvalidUserDataException('You must set POM_DEVELOPER_ID in gradle.properties file.')
        }
    }

    String getDeveloperName() {
        if (project.hasProperty('POM_DEVELOPER_NAME')) {
            return project.POM_DEVELOPER_NAME
        } else {
            throw new InvalidUserDataException('You must set POM_DEVELOPER_NAME in gradle.properties file.')
        }
    }

    String getDeveloperEmail() {
        return project.hasProperty('POM_DEVELOPER_EMAIL') ? project.POM_DEVELOPER_EMAIL : 'lazy-developer-who-does-not-read-readme@example.com'
    }

    String getDeveloperUrl() {
        return project.hasProperty('POM_DEVELOPER_URL') ? project.POM_DEVELOPER_URL : ''
    }

    String getDeveloperOrg() {
        return project.hasProperty('POM_DEVELOPER_ORG') ? project.POM_DEVELOPER_ORG : getOrg()
    }

    String getDeveloperOrgUrl() {
        return project.hasProperty('POM_DEVELOPER_ORG_URL') ? project.POM_DEVELOPER_ORG_URL : getOrgUrl()
    }

    String[] getDeveloperRoles() {
        if (project.hasProperty('POM_DEVELOPER_ROLES')) {
            if (project.hasProperty('POM_DEVELOPER_ROLE')) {
                final String developerRoles = project.getProperty('POM_DEVELOPER_ROLE') + ',' + project.getProperty('POM_DEVELOPER_ROLES')
                return developerRoles.split(',')
            } else {
                return project.getProperty('POM_DEVELOPER_ROLES').split(',')
            }
        } else {
            return project.hasProperty('POM_DEVELOPER_ROLE') ? [project.POM_DEVELOPER_ROLE] : ['Software Developer']
        }
    }

    String getDeveloperTimezone() {
        return project.hasProperty('POM_DEVELOPER_TIMEZONE') ? project.POM_DEVELOPER_TIMEZONE : ''
    }

    String[] getDevelopers() {
        return project.hasProperty('POM_DEVELOPERS') ? project.getProperty('POM_DEVELOPERS').split(',') : ['']
    }

    String[] getContributors() {
        return project.hasProperty('POM_CONTRIBUTORS') ? project.getProperty('POM_CONTRIBUTORS').split(',') : ['']
    }

    String getIssueSystem() {
        return project.hasProperty('POM_ISSUE_SYSTEM') ? project.POM_ISSUE_SYSTEM : ''
    }

    String getIssueSystemUrl() {
        return project.hasProperty('POM_ISSUE_SYSTEM_URL') ? project.POM_ISSUE_SYSTEM_URL : ''
    }

    String getCiSystem() {
        return project.hasProperty('POM_CI_SYSTEM') ? project.POM_CI_SYSTEM : ''
    }

    String getCiSystemUrl() {
        return project.hasProperty('POM_CI_SYSTEM_URL') ? project.POM_CI_SYSTEM_URL : ''
    }

    String[] getMailingLists() {
        return project.hasProperty('POM_MAILING_LISTS') ? project.getProperty('POM_MAILING_LISTS').split(',') : ['']
    }

    String[] getRepositories() {
        if (isReleaseBuild()) {
            return project.hasProperty('POM_REPOSITORIES') ? project.getProperty('POM_REPOSITORIES').split(',') : ['']
        } else {
            if (project.hasProperty('POM_SNAPSHOT_REPOSITORIES')) {
                return project.getProperty('POM_SNAPSHOT_REPOSITORIES').split(',')
            } else {
                return project.hasProperty('POM_REPOSITORIES') ? project.getProperty('POM_REPOSITORIES').split(',') : ['']
            }
        }
    }

    String getDistDownloadUrl() {
        return project.hasProperty('POM_DIST_DOWNLOAD_URL') ? project.POM_DIST_DOWNLOAD_URL : ''
    }
}