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

/**
 * <p>GradleMavenPush is a helper to upload Gradle Android Artifacts,
 * Gradle Java Artifacts and Gradle Kotlin Artifacts to Maven repositories (JCenter,
 * Maven Central, Corporate staging/snapshot servers and local Maven repositories).</p>
 * <p>MavenPush Class - class with gradle properties getters, thread-safe and
 * a fast singleton implementation.</p>
 *
 * @author Alexander Savin
 * @version 1.6.0 Yokohama
 * @since 1.5.0 Tokyo
 */
final class MavenPush {

    private static volatile MavenPush singleton = null

    private final def project

    private MavenPush(project) {
        this.project = project
    }

    final private class InvalidUserDataException extends Exception {
        InvalidUserDataException(String message) {
            super(message)
        }
    }

    /**
     * Only method to get singleton object of MavenPush Class
     *
     * @param project project
     * @return thread-safe singleton
     */
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

    /**
     * Checks Android or non-Android project.
     *
     * @return true if Android project, false otherwise
     */
    private boolean isAndroid() {
        return project.getPlugins().hasPlugin('com.android.application') ||
                project.getPlugins().hasPlugin('com.android.library') ||
                project.getPlugins().hasPlugin('android') ||
                project.getPlugins().hasPlugin('android-library')
    }

    /**
     * Checks if you're putting builds of your project on JCenter.
     *
     * @return true if JCenter, false otherwise
     */
    boolean isJCenter() {
        return (project.hasProperty('IS_JCENTER') && 'true'.equalsIgnoreCase(project.IS_JCENTER))
    }

    /**
     * Checks if it's Release Build or SNAPSHOT Build of your project.
     *
     * @return true if Release Build, false otherwise
     */
    boolean isReleaseBuild() {
        return !getPomVersionName().contains('SNAPSHOT')
    }

    /**
     * Returns release repository url.
     *
     * @return RELEASE_REPOSITORY_URL gradle property value or
     * "https://bintray.com/api/v1/maven/{project.NEXUS_USERNAME}/maven/{project.POM_ARTIFACT_ID}/;publish=1"
     * for JCenter and "https://oss.sonatype.org/service/local/staging/deploy/maven2/" for Maven Central
     * if project hasn't this property
     */
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

    /**
     * Returns snapshot repository url.
     *
     * @return SNAPSHOT_REPOSITORY_URL gradle property value or
     * "https://oss.jfrog.org/artifactory/oss-snapshot-local/" for JCenter and
     * "https://oss.sonatype.org/content/repositories/snapshots/" for Maven Central
     * if project hasn't this property
     */
    String getSnapshotRepositoryUrl() {
        if (project.hasProperty('SNAPSHOT_REPOSITORY_URL')) {
            return project.SNAPSHOT_REPOSITORY_URL
        } else if (isJCenter()) {
            return 'https://oss.jfrog.org/artifactory/oss-snapshot-local/'
        } else {
            return 'https://oss.sonatype.org/content/repositories/snapshots/'
        }
    }

    /**
     * Returns repository username.
     *
     * @return NEXUS_USERNAME environment variable value or NEXUS_USERNAME gradle property value
     * if System hasn't this environment variable or "" if System hasn't this environment variable
     * and project hasn't this property
     */
    String getRepositoryUsername() {
        if (System.getenv().containsKey('NEXUS_USERNAME')) {
            return System.getenv('NEXUS_USERNAME')
        } else {
            return project.hasProperty('NEXUS_USERNAME') ? project.NEXUS_USERNAME : ''
        }
    }

    /**
     * Returns repository password.
     *
     * @return NEXUS_PASSWORD environment variable value or NEXUS_PASSWORD gradle property value
     * if System hasn't this environment variable or "" if System hasn't this environment variable
     * and project hasn't this property
     */
    String getRepositoryPassword() {
        if (System.getenv().containsKey('NEXUS_PASSWORD')) {
            return System.getenv('NEXUS_PASSWORD')
        } else {
            return project.hasProperty('NEXUS_PASSWORD') ? project.NEXUS_PASSWORD : ''
        }
    }

    /**
     * Checks if apklib artifact must be generated.
     *
     * @return true if APKLIB_ARTIFACT gradle property value is "true", false otherwise
     */
    boolean getApklibArtifact() {
        return project.hasProperty('APKLIB_ARTIFACT') ? 'true'.equalsIgnoreCase(project.APKLIB_ARTIFACT) : false
    }

    /**
     * Checks if Android jar artifact must be generated.
     *
     * @return true if ANDROID_JAR_ARTIFACT gradle property value is "true", false otherwise
     */
    boolean getAndroidJarArtifact() {
        return project.hasProperty('ANDROID_JAR_ARTIFACT') ? 'true'.equalsIgnoreCase(project.ANDROID_JAR_ARTIFACT) : false
    }

    /**
     * Returns "Main-Class" attribute for Android's "jar" and "fatjar" MANIFEST.MF files.
     *
     * @return ANDROID_JAR_MAIN_CLASS gradle property value or "" if project hasn't this property
     */
    String getAndroidJarMainClass() {
        return project.hasProperty('ANDROID_JAR_MAIN_CLASS') ? project.ANDROID_JAR_MAIN_CLASS : ''
    }

    /**
     * Checks if fatjar artifact must be generated.
     *
     * @return true if FATJAR_ARTIFACT gradle property value is "true", false otherwise
     */
    boolean getFatjarArtifact() {
        return project.hasProperty('FATJAR_ARTIFACT') ? 'true'.equalsIgnoreCase(project.FATJAR_ARTIFACT) : false
    }

    /**
     * Checks if Dokka documentation engine must be used.
     *
     * @return true if JAVADOC_BY_DOKKA gradle property value is "true", false otherwise
     */
    boolean isDokka() {
        return project.hasProperty('JAVADOC_BY_DOKKA') ? 'true'.equalsIgnoreCase(project.JAVADOC_BY_DOKKA) : false
    }

    /**
     * Returns Dokka fatjar version
     *
     * @return DOKKA_FATJAR_VERSION gradle property value or "0.9.17" if project hasn't
     * this property
     */
    String getDokkaFatJarVersion() {
        return project.hasProperty('DOKKA_FATJAR_VERSION') ? project.DOKKA_FATJAR_VERSION : '0.9.17'
    }

    /**
     * Returns Dokka output format
     *
     * @return DOKKA_OUTPUT_FORMAT gradle property value or "javadoc" if project hasn't
     * this property
     */
    String getDokkaOutputFormat() {
        return project.hasProperty('DOKKA_OUTPUT_FORMAT') ? project.DOKKA_OUTPUT_FORMAT : 'javadoc'
    }

    /**
     * Checks if doclint check on Javadoc generation must be enable.
     *
     * @return true if DOCLINT_CHECK gradle property value is "true", false otherwise
     */
    boolean getDoclintCheck() {
        return project.hasProperty('DOCLINT_CHECK') ? 'true'.equalsIgnoreCase(project.DOCLINT_CHECK) : false
    }

    /**
     * Returns Javadoc encoding.
     *
     * @return JAVADOC_ENCODING gradle property value or "UTF-8" if project hasn't this property
     */
    String getJavadocEncoding() {
        return project.hasProperty('JAVADOC_ENCODING') ? project.JAVADOC_ENCODING : 'UTF-8'
    }

    /**
     * Returns Javadoc doc encoding.
     *
     * @return JAVADOC_DOC_ENCODING gradle property value or "UTF-8" if project hasn't this property
     */
    String getJavadocDocEncoding() {
        return project.hasProperty('JAVADOC_DOC_ENCODING') ? project.JAVADOC_DOC_ENCODING : 'UTF-8'
    }

    /**
     * Returns Javadoc charset.
     *
     * @return JAVADOC_CHARSET gradle property value or "UTF-8" if project hasn't this property
     */
    String getJavadocCharSet() {
        return project.hasProperty('JAVADOC_CHARSET') ? project.JAVADOC_CHARSET : 'UTF-8'
    }

    /**
     * Checks HTML version in the Javadoc comments.
     *
     * @return true if JAVADOC_HTML_VERSION gradle property value is "5", false otherwise
     */
    boolean isHtml5() {
        return project.hasProperty('JAVADOC_HTML_VERSION') ? (project.JAVADOC_HTML_VERSION == '5') : false
    }

    /**
     * Returns Group ID.
     *
     * @return GROUP gradle property value or libraryVariants[0].applicationId value
     * if project hasn't this property and it's Android project
     * @throws InvalidUserDataException If Group ID can't be return.
     */
    String getPomGroupId() throws InvalidUserDataException {
        if (project.hasProperty('GROUP')) {
            return project.GROUP
        } else if (isAndroid() && (project.android.libraryVariants != null) && (project.android.libraryVariants.size() > 0)) {
            return project.android.libraryVariants[0].applicationId
        } else {
            throw new InvalidUserDataException('You must set GROUP in gradle.properties file.')
        }
    }

    /**
     * Returns Artifact ID.
     *
     * @return POM_ARTIFACT_ID gradle property value
     * @throws InvalidUserDataException If Artifact ID can't be return
     */
    String getPomArtifactId() throws InvalidUserDataException {
        if (project.hasProperty('POM_ARTIFACT_ID')) {
            return project.POM_ARTIFACT_ID
        } else {
            throw new InvalidUserDataException('You must set POM_ARTIFACT_ID in gradle.properties file.')
        }
    }

    /**
     * Returns Artifact URL.
     *
     * @return POM_ARTIFACT_URL gradle property value or POM_ARTIFACT_ID gradle property value
     * if project hasn't this property
     * @throws InvalidUserDataException If Artifact URL can't be return
     */
    String getPomArtifactUrl() throws InvalidUserDataException {
        return project.hasProperty('POM_ARTIFACT_URL') ? project.POM_ARTIFACT_URL : getPomArtifactId()
    }

    /**
     * Returns version name.
     *
     * @return VERSION_NAME gradle property value + VERSION_NAME_EXTRAS environment variable value
     * or defaultConfig.versionName value + VERSION_NAME_EXTRAS environment variable value
     * if project hasn't VERSION_NAME gradle property and it's Android project
     * @throws InvalidUserDataException If version name can't be return
     */
    String getPomVersionName() throws InvalidUserDataException {
        final String versionNameExtras = (System.getenv().containsKey('VERSION_NAME_EXTRAS')) ? System.getenv('VERSION_NAME_EXTRAS') : ''
        if (project.hasProperty('VERSION_NAME')) {
            return project.VERSION_NAME + versionNameExtras
        } else if (isAndroid() && (project.android.defaultConfig.versionName != null)) {
            return project.android.defaultConfig.versionName + versionNameExtras
        } else {
            throw new InvalidUserDataException('You must set VERSION_NAME in gradle.properties file.')
        }
    }

    /**
     * Returns packaging.
     *
     * @return POM_PACKAGING gradle property value or "aar" for Android project and
     * "jar" for non-Android project if project hasn't POM_PACKAGING gradle property
     */
    String getPomPackaging() {
        if (project.hasProperty('POM_PACKAGING')) {
            return project.POM_PACKAGING
        } else {
            return isAndroid() ? 'aar' : 'jar'
        }
    }

    /**
     * Returns library name.
     *
     * @return POM_NAME gradle property value
     * @throws InvalidUserDataException If library name can't be return
     */
    String getPomName() throws InvalidUserDataException {
        if (project.hasProperty('POM_NAME')) {
            return project.POM_NAME
        } else {
            throw new InvalidUserDataException('You must set POM_NAME in gradle.properties file.')
        }
    }

    /**
     * Returns library description.
     *
     * @return POM_DESCRIPTION gradle property value
     * @throws InvalidUserDataException If library description can't be return
     */
    String getPomDescription() throws InvalidUserDataException {
        if (project.hasProperty('POM_DESCRIPTION')) {
            return project.POM_DESCRIPTION
        } else {
            throw new InvalidUserDataException('You must set POM_DESCRIPTION in gradle.properties file.')
        }
    }

    /**
     * Checks if unique snapshots must be enable.
     *
     * @return true if POM_GENERATE_UNIQUE_SNAPSHOTS gradle property value is "true" or
     * if project hasn't POM_GENERATE_UNIQUE_SNAPSHOTS gradle property, false otherwise
     */
    boolean getPomUniqueVersion() {
        return project.hasProperty('POM_GENERATE_UNIQUE_SNAPSHOTS') ? 'true'.equalsIgnoreCase(project.POM_GENERATE_UNIQUE_SNAPSHOTS) : true
    }

    /**
     * Returns library url.
     *
     * @return POM_URL gradle property value
     * @throws InvalidUserDataException If library url can't be return
     */
    String getPomUrl() throws InvalidUserDataException {
        if (project.hasProperty('POM_URL')) {
            return project.POM_URL
        } else {
            throw new InvalidUserDataException('You must set POM_URL in gradle.properties file.')
        }
    }

    /**
     * Returns inception year.
     *
     * @return POM_INCEPTION_YEAR gradle property value or "" if project hasn't
     * POM_INCEPTION_YEAR gradle property
     */
    String getPomInceptionYear() {
        return project.hasProperty('POM_INCEPTION_YEAR') ? project.POM_INCEPTION_YEAR : ''
    }

    /**
     * Returns library publicly browsable repository url.
     *
     * @return POM_SCM_URL gradle property value or POM_URL gradle property value if project hasn't
     * POM_SCM_URL gradle property
     * @throws InvalidUserDataException If library publicly browsable repository url can't be return
     */
    String getPomScmUrl() throws InvalidUserDataException {
        return project.hasProperty('POM_SCM_URL') ? project.POM_SCM_URL : getPomUrl()
    }

    /**
     * Returns connection element convey to how one is to connect to the version control system through Maven.
     *
     * @return POM_SCM_CONNECTION gradle property value
     * @throws InvalidUserDataException If connection element can't be return
     */
    String getPomScmConnection() throws InvalidUserDataException {
        if (project.hasProperty('POM_SCM_CONNECTION')) {
            return project.POM_SCM_CONNECTION
        } else {
            throw new InvalidUserDataException('You must set POM_SCM_CONNECTION in gradle.properties file.')
        }
    }

    /**
     * Returns connection element convey to how one is to connect to the version control system through Maven.
     *
     * @return POM_SCM_DEV_CONNECTION gradle property value or POM_SCM_CONNECTION gradle property value if project hasn't
     * POM_SCM_DEV_CONNECTION gradle property
     * @throws InvalidUserDataException If connection element can't be return
     */
    String getPomScmDevConnection() throws InvalidUserDataException {
        return project.hasProperty('POM_SCM_DEV_CONNECTION') ? project.POM_SCM_DEV_CONNECTION : getPomScmConnection()
    }

    /**
     * Returns the tag that your project lives under.
     *
     * @return POM_SCM_TAG gradle property value or "HEAD" (meaning, the Software Configuration Management (SCM) root)
     * if project hasn't POM_SCM_TAG gradle property
     */
    String getPomScmTag() {
        return project.hasProperty('POM_SCM_TAG') ? project.POM_SCM_TAG : 'HEAD'
    }

    /**
     * Returns licence name.
     *
     * @return POM_LICENCE_NAME gradle property value
     * @throws InvalidUserDataException If licence name can't be return
     */
    String getPomLicenseName() throws InvalidUserDataException {
        if (project.hasProperty('POM_LICENCE_NAME')) {
            return project.POM_LICENCE_NAME
        } else {
            throw new InvalidUserDataException('You must set POM_LICENCE_NAME in gradle.properties file.')
        }
    }

    /**
     * Returns licence url.
     *
     * @return POM_LICENCE_URL gradle property value
     * @throws InvalidUserDataException If licence url can't be return
     */
    String getPomLicenseUrl() throws InvalidUserDataException {
        if (project.hasProperty('POM_LICENCE_URL')) {
            return project.POM_LICENCE_URL
        } else {
            throw new InvalidUserDataException('You must set POM_LICENCE_URL in gradle.properties file.')
        }
    }

    /**
     * Returns licence dist.
     *
     * @return POM_LICENCE_DIST gradle property value or "repo"
     * if project hasn't POM_LICENCE_DIST gradle property
     */
    String getPomLicenseDist() {
        return project.hasProperty('POM_LICENCE_DIST') ? project.POM_LICENCE_DIST : 'repo'
    }

    /**
     * Returns licence comments.
     *
     * @return POM_LICENCE_COMMENTS gradle property value or ""
     * if project hasn't POM_LICENCE_COMMENTS gradle property
     */
    String getPomLicenseComments() {
        return project.hasProperty('POM_LICENCE_COMMENTS') ? project.POM_LICENCE_COMMENTS : ''
    }

    /**
     * Returns organization.
     *
     * @return POM_ORG gradle property value or ""
     * if project hasn't POM_ORG gradle property
     */
    String getOrg() {
        return project.hasProperty('POM_ORG') ? project.POM_ORG : ''
    }

    /**
     * Returns organization url.
     *
     * @return POM_ORG_URL gradle property value or ""
     * if project hasn't POM_ORG_URL gradle property
     */
    String getOrgUrl() {
        return project.hasProperty('POM_ORG_URL') ? project.POM_ORG_URL : ''
    }

    /**
     * Returns developer ID.
     *
     * @return POM_DEVELOPER_ID gradle property value
     * @throws InvalidUserDataException If developer ID can't be return
     */
    String getDeveloperId() throws InvalidUserDataException {
        if (project.hasProperty('POM_DEVELOPER_ID')) {
            return project.POM_DEVELOPER_ID
        } else {
            throw new InvalidUserDataException('You must set POM_DEVELOPER_ID in gradle.properties file.')
        }
    }

    /**
     * Returns developer name.
     *
     * @return POM_DEVELOPER_NAME gradle property value
     * @throws InvalidUserDataException If developer name can't be return
     */
    String getDeveloperName() throws InvalidUserDataException {
        if (project.hasProperty('POM_DEVELOPER_NAME')) {
            return project.POM_DEVELOPER_NAME
        } else {
            throw new InvalidUserDataException('You must set POM_DEVELOPER_NAME in gradle.properties file.')
        }
    }

    /**
     * Returns developer email.
     *
     * @return POM_DEVELOPER_EMAIL gradle property value or "lazy-developer-who-does-not-read-readme@example.com"
     * if project hasn't POM_DEVELOPER_EMAIL gradle property
     */
    String getDeveloperEmail() {
        return project.hasProperty('POM_DEVELOPER_EMAIL') ? project.POM_DEVELOPER_EMAIL : 'lazy-developer-who-does-not-read-readme@example.com'
    }

    /**
     * Returns developer url.
     *
     * @return POM_DEVELOPER_URL gradle property value or ""
     * if project hasn't POM_DEVELOPER_URL gradle property
     */
    String getDeveloperUrl() {
        return project.hasProperty('POM_DEVELOPER_URL') ? project.POM_DEVELOPER_URL : ''
    }

    /**
     * Returns developer organization.
     *
     * @return POM_DEVELOPER_ORG gradle property value or ""
     * if project hasn't POM_DEVELOPER_ORG gradle property
     */
    String getDeveloperOrg() {
        return project.hasProperty('POM_DEVELOPER_ORG') ? project.POM_DEVELOPER_ORG : getOrg()
    }

    /**
     * Returns developer organization url.
     *
     * @return POM_DEVELOPER_ORG_URL gradle property value or POM_ORG_URL gradle property value if project hasn't
     * POM_DEVELOPER_ORG_URL gradle property or "" if project hasn't POM_DEVELOPER_ORG_URL gradle property and
     * POM_ORG_URL gradle property
     */
    String getDeveloperOrgUrl() {
        return project.hasProperty('POM_DEVELOPER_ORG_URL') ? project.POM_DEVELOPER_ORG_URL : getOrgUrl()
    }

    /**
     * Returns array of developer roles.
     *
     * @return developer roles from POM_DEVELOPER_ROLE gradle property and POM_DEVELOPER_ROLES gradle property
     * or ['Software Developer'] if project hasn't POM_DEVELOPER_ROLE gradle property and
     * POM_DEVELOPER_ROLES gradle property
     */
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

    /**
     * Returns developer timezone.
     *
     * @return POM_DEVELOPER_TIMEZONE gradle property value or ""
     * if project hasn't POM_DEVELOPER_TIMEZONE gradle property
     */
    String getDeveloperTimezone() {
        return project.hasProperty('POM_DEVELOPER_TIMEZONE') ? project.POM_DEVELOPER_TIMEZONE : ''
    }

    /**
     * Returns array of developers (id, name and email for each developer). Example:
     * {@code [ ' B i l l G ' , 'Bill Gates' , 'bill@example.com' , 'SteveJ' , 'Steve Jobs' , 'steve@example.com']}
     *
     * @return developers from POM_DEVELOPERS gradle property or ['']
     * if project hasn't POM_DEVELOPERS gradle property
     */
    String[] getDevelopers() {
        return project.hasProperty('POM_DEVELOPERS') ? project.getProperty('POM_DEVELOPERS').split(',') : ['']
    }

    /**
     * Returns array of contributors (name and email for each contributor). Example:
     * {@code ['Bill Gates' , 'bill@example.com' , 'Steve Jobs' , 'steve@example.com']}
     *
     * @return contributors from POM_CONTRIBUTORS gradle property or ['']
     * if project hasn't POM_CONTRIBUTORS gradle property
     */
    String[] getContributors() {
        return project.hasProperty('POM_CONTRIBUTORS') ? project.getProperty('POM_CONTRIBUTORS').split(',') : ['']
    }

    /**
     * Returns issue tracking system.
     *
     * @return POM_ISSUE_SYSTEM gradle property value or ""
     * if project hasn't POM_ISSUE_SYSTEM gradle property
     */
    String getIssueSystem() {
        return project.hasProperty('POM_ISSUE_SYSTEM') ? project.POM_ISSUE_SYSTEM : ''
    }

    /**
     * Returns issue tracking system url.
     *
     * @return POM_ISSUE_SYSTEM_URL gradle property value or ""
     * if project hasn't POM_ISSUE_SYSTEM_URL gradle property
     */
    String getIssueSystemUrl() {
        return project.hasProperty('POM_ISSUE_SYSTEM_URL') ? project.POM_ISSUE_SYSTEM_URL : ''
    }

    /**
     * Returns continuous integration system.
     *
     * @return POM_CI_SYSTEM gradle property value or ""
     * if project hasn't POM_CI_SYSTEM gradle property
     */
    String getCiSystem() {
        return project.hasProperty('POM_CI_SYSTEM') ? project.POM_CI_SYSTEM : ''
    }

    /**
     * Returns continuous integration system url.
     *
     * @return POM_CI_SYSTEM_URL gradle property value or ""
     * if project hasn't POM_CI_SYSTEM_URL gradle property
     */
    String getCiSystemUrl() {
        return project.hasProperty('POM_CI_SYSTEM_URL') ? project.POM_CI_SYSTEM_URL : ''
    }

    /**
     * Returns array of mailing lists (name, subscribe email and unsubscribe email for each mailing list). Example:
     * {@code ['Main' , 's@example.com' , 'u@example.com' , 'Support' , 'ss@example.com' , 'us@example.com']}
     *
     * @return mailing lists from POM_MAILING_LISTS gradle property or ['']
     * if project hasn't POM_MAILING_LISTS gradle property
     */
    String[] getMailingLists() {
        return project.hasProperty('POM_MAILING_LISTS') ? project.getProperty('POM_MAILING_LISTS').split(',') : ['']
    }

    /**
     * Returns array of repositories (id and url for each repository). Example:
     * {@code ['mavenCentral' , 'https://repo1.maven.org/maven2/' , 'jCenter' , 'https://jcenter.bintray.com/']}
     *
     * @return repositories from POM_REPOSITORIES gradle property or ['']
     * if project hasn't POM_REPOSITORIES gradle property if release build;
     * repositories from POM_SNAPSHOT_REPOSITORIES gradle property or
     * repositories from POM_REPOSITORIES gradle property
     * if project hasn't POM_SNAPSHOT_REPOSITORIES gradle property or ['']
     * if project hasn't POM_SNAPSHOT_REPOSITORIES and POM_REPOSITORIES gradle properties if non-release build
     */
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

    /**
     * Returns the url of the repository from whence another POM
     * may point to in order to grab this POM's artifact.
     *
     * @return POM_DIST_DOWNLOAD_URL gradle property value or ""
     * if project hasn't POM_DIST_DOWNLOAD_URL gradle property
     */
    String getDistDownloadUrl() {
        return project.hasProperty('POM_DIST_DOWNLOAD_URL') ? project.POM_DIST_DOWNLOAD_URL : ''
    }
}