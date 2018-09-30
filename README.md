# GradleMavenPush [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-GradleMavenPush-brightgreen.svg?style=flat)](#) [![Latest Version](https://api.bintray.com/packages/vorlonsoft/VorlonsoftCentral/GradleMavenPush/images/download.svg)](https://github.com/Vorlonsoft/GradleMavenPush/releases)

Helper to upload Gradle Android Artifacts, Gradle Java Artifacts and Gradle Kotlin Artifacts to Maven repositories (JCenter, Maven Central, Corporate staging/snapshot servers and local Maven repositories).

## Contents

* [Usage](#usage)
  * [1. Have a working Gradle build](#1-have-a-working-gradle-build)
  * [2. Update your home gradle.properties](#2-update-your-home-gradleproperties)
  * [3. Create project root gradle.properties](#3-create-project-root-gradleproperties)
  * [4. Create gradle.properties in each module](#4-create-gradleproperties-in-each-module)
  * [5. Call the script from each module's build.gradle](#5-call-the-script-from-each-modules-buildgradle)
  * [6. Build and Deploy/Install](#6-build-and-deployinstall)
* [Other properties](#other-properties-optional)
* [Groovydoc documentation](#groovydoc-documentation)
* [Already in use](#already-in-use-in-following-libraries)
* [Other plugins](#our-other-plugins)
* [Contribute](#contribute)
* [License](#license)

## Usage

### 1. Have a working Gradle build

It is up to you.

### 2. Update your home gradle.properties

This will include flag `IS_JCENTER` (default is "false" - Maven Central, "true" - JCenter), username and password (API Key for JCenter) to upload to the Maven server and so that they are kept local on your machine. The location defaults to `USER_HOME/.gradle/gradle.properties`.

It may also include your signing key id, password, and secret key ring file (for signed uploads).  Signing is only necessary if you're putting release builds of your project on Maven Central or JCenter.

```properties
IS_JCENTER                = false
NEXUS_USERNAME            = vorlonsoft
NEXUS_PASSWORD            = $tr0ngP@55w0rd

signing.keyId             = ABCDEF12
signing.password          = P@55w0rd
signing.secretKeyRingFile = ./secring.gpg
```

#### 2.1. Alternative, use environment variables (optional)

You can modify username and password (API Key for JCenter) from environment variables (useful for CI). To use those environment variables on CI just export them:

```properties
export NEXUS_USERNAME      = vorlonsoft
export NEXUS_PASSWORD      = $tr0ngP@55w0rd
```

#### 2.2. Other home gradle.properties (optional)

This will include `JCENTER_USERNAME` (default is `NEXUS_USERNAME` value) and `JCENTER_API_KEY` (default is `NEXUS_PASSWORD` value) to upload to the JCentor. Also you can modify `JCENTER_USERNAME` and `JCENTER_API_KEY` from environment variables (useful for CI).

```properties
JCENTER_USERNAME          = vorlonsoft
JCENTER_API_KEY           = $tr0ngJCenter@P!Key
```

### 3. Create project root gradle.properties

You may already have this file, in which case just edit the original. This file should contain the properties values which are common to all of your sub-projects (if you have any). For instance, here's [AndroidRate's](https://github.com/Vorlonsoft/AndroidRate):

```properties
# GROUP (default is packageName for Android projects, "" for non-Android)
GROUP                  = com.vorlonsoft
# VERSION_NAME (default is build.gradle versionName for Android projects, "" for non-Android)
VERSION_NAME           = 1.2.0-SNAPSHOT

POM_DESCRIPTION        = Library for Android applications, which provides rating dialog.
POM_URL                = https\://github.com/Vorlonsoft/AndroidRate
POM_LICENCE_NAME       = The MIT License (MIT)
POM_LICENCE_URL        = https\://opensource.org/licenses/MIT
POM_DEVELOPER_ID       = AlexanderLS
POM_DEVELOPER_NAME     = Alexander Savin
POM_DEVELOPER_EMAIL    = info@vorlonsoft.com
POM_SCM_CONNECTION     = scm\:git@github.com\:Vorlonsoft/AndroidRate.git
```

The `VERSION_NAME` value is important. If it contains the keyword `SNAPSHOT` then the build will upload to the snapshot server, if not then to the release server.

#### 3.1. Modify the version name from environment variable (optional)

If there's an environment variable called `VERSION_NAME_EXTRAS`, its value will get appended at the end of `VERSION_NAME`.
This can be very powerful when running from CI. For example, to have one SNAPSHOT per branch, you could

```properties
export VERSION_NAME_EXTRAS = -master-SNAPSHOT
```
in this case it will be uploaded to the snapshot server and indicates it's from the master branch.

### 4. Create gradle.properties in each module

The values in this file are specific to the sub-project (and override those in the root `gradle.properties`). In this example, this is just the name, artifactId and `JAVADOC_BY_DOKKA` (default is "false"):

```properties
POM_ARTIFACT_ID  = androidrate
POM_NAME         = AndroidRate Library
JAVADOC_BY_DOKKA = false
```

Set `JAVADOC_BY_DOKKA` to "true" to generate documentation by Dokka. Dokka is a documentation engine for Kotlin, it fully supports mixed-language Java/Kotlin projects.

#### 4.1 Other gradle.properties in each module (optional)

You can add `POM_PACKAGING` (default is "aar" for Gradle Android Artifacts and "jar" for Gradle Java Artifacts and Gradle Kotlin Artifacts) and change it's value. Depends on Gradle/Plugins versions this option: 1. Changes `<packaging>` tag in the generated pom file only; 2. Changes main artifact file extension and `<packaging>` tag in the generated pom file; 3. Changes main artifact and it's asc file extensions and change `<packaging>` tag in the generated pom file;

Add `VAR_ARTIFACT` (default is "true") and set it to "true" to generate Gradle Android Artifact var. You'll get both `POM_PACKAGING` value (default is "aar" for Gradle Android Artifacts) and "var" artifacts in your Android library project.

Add `ANDROID_JAR_ARTIFACT` (default is "false") and set it to "true" to generate Gradle Android Artifact jar. You'll get both `POM_PACKAGING` value (default is "aar" for Gradle Android Artifacts) and "jar" artifacts in your Android library project.

Add `ANDROID_JAR_MAIN_CLASS` (example "com.vorlonsoft.android.rate.AppRate", default is "") and set it to `${package}.${main-class-name}` to add "Main-Class" attribute to Android's "var", "jar" and "fatjar" `MANIFEST.MF` files.

Add `FATJAR_ARTIFACT` (default is "false") and set it to "true" to generate fatjar. You'll get both `POM_PACKAGING` value (default is "aar" for Gradle Android Artifacts and "jar" for Gradle Java Artifacts and Gradle Kotlin Artifacts) and "fatjar" artifacts.

Add `APKLIB_ARTIFACT` (default is "false") and set it to "true" to generate Gradle Android Artifact apklib. You'll get both `POM_PACKAGING` value (default is "aar" for Gradle Android Artifacts) and "apklib" artifacts in your Android library project. apklib is a way to bundle an Android library project.

Also you can set `POM_ARTIFACT_URL` (default is `POM_ARTIFACT_ID` value), this is makes to easier to have an artifact with one artifactId but the name on JCenter something else.

### 5. Call the script from each module's build.gradle

Add the following at the end of each `build.gradle` that you wish to upload:

```groovy
apply from: 'https://raw.github.com/Vorlonsoft/GradleMavenPush/master/maven-push.gradle'
```

### 6. Build and Deploy/Install

You can now build and *deploy* on JCenter, Maven Central or Corporate staging/snapshot servers:

```bash
$ gradle deployOnServerRepository
```

Build and *install* on local Maven (~/.m2/repository/):

```bash
$ gradle installOnLocalRepository
```

Build and *deploy* on local Maven (~/.m2/repository/):

```bash
$ gradle deployOnLocalRepository
```

#### 6.1 Inter-module dependency (optional)

If your modules have dependencies on each other (e.g. implementation project(':other_module')), then you should do one of the following for proper POM generation

- **option A**: add to top level `build.gradle`:

```groovy
allprojects {
    group = GROUP
    version = VERSION_NAME
}
```

- **option B**: add to top level `gradle.properties`:

```properties
group   = com.vorlonsoft
version = 1.2.0
```

### Other properties (optional)

There are other properties which can be set:

#### Repositories urls

```properties
RELEASE_REPOSITORY_URL (defaults to Maven Central's or JCenter's staging server (depends on IS_JCENTER))
SNAPSHOT_REPOSITORY_URL (defaults to Maven Central's or JCenter's snapshot server (depends on IS_JCENTER))
```

#### Javadoc generation

```properties
DOCLINT_CHECK (default is "false")
JAVADOC_ENCODING (default is "UTF-8")
JAVADOC_DOC_ENCODING (default is "UTF-8")
JAVADOC_CHARSET (default is "UTF-8")
```

Java 9+. This option assume that the HTML in the document comments is of the same version (4 or 5). It doesn't convert the HTML in the user documentation comments to the specified output version.

```properties
JAVADOC_HTML_VERSION (default is "4")
```

#### Dokka documentation engine

Dokka fatjar version. Latest version is [![Dokka fatjar latest version](https://api.bintray.com/packages/kotlin/dokka/dokka/images/download.svg)](#)

```properties
DOKKA_FATJAR_VERSION (default is "0.9.17")
```

Dokka output format. Options are:

  * `html` - minimalistic html format used by default
  * `javadoc` - Dokka mimic to javadoc
  * `html-as-java` - as `html` but using java syntax
  * `markdown` - Markdown structured as `html`
    * `gfm` - GitHub flavored markdown
    * `jekyll` - Jekyll compatible markdown
  * `kotlin-website` - internal format used for documentation on *kotlinlang.org*

```properties
DOKKA_OUTPUT_FORMAT (default is "javadoc")
```

#### Snapshots names

```properties
POM_GENERATE_UNIQUE_SNAPSHOTS (default is "true")
```

#### Project Information

```properties
POM_INCEPTION_YEAR (default is "")
```

#### Organization

```properties
POM_ORG (default is "")
POM_ORG_URL (default is "")
```

#### Licenses

```properties
POM_LICENCE_DIST (default is "repo")
POM_LICENCE_COMMENTS (default is "")
```

#### Developers

```properties
POM_DEVELOPER_URL (default is "")
POM_DEVELOPER_ORG (default is POM_ORG value)
POM_DEVELOPER_ORG_URL (default is POM_ORG_URL value)
POM_DEVELOPER_ROLE (default is "Software Developer")
POM_DEVELOPER_ROLES (example "Software Architect,Software Developer", default is POM_DEVELOPER_ROLE value)
POM_DEVELOPER_TIMEZONE (default is "")
```

2nd, 3rd, etc developers, only id, name and email separated by comma.

```properties
POM_DEVELOPERS (example "BillG,Bill Gates,bill@example.com,SteveJ,Steve Jobs,steve@example.com", default is "")
```

#### Contributors

Contributors, only name and email separated by comma.

```properties
POM_CONTRIBUTORS (example "Bill Gates,bill@example.com,Steve Jobs,steve@example.com", default is "")
```

#### Issue Management

```properties
POM_ISSUE_SYSTEM (default is "")
POM_ISSUE_SYSTEM_URL (default is "")
```

#### Continuous Integration Management

```properties
POM_CI_SYSTEM (default is "")
POM_CI_SYSTEM_URL (default is "")
```

#### Mailing Lists

Mailing Lists, only name, subscribe email and unsubscribe email separated by comma.

```properties
POM_MAILING_LISTS (example "Main,s@example.com,u@example.com,Support,ss@example.com,us@example.com", default is "")
```

#### Software Configuration Management

Connection element convey to how one is to connect to the version control system through Maven.

```properties
POM_SCM_DEV_CONNECTION (default is POM_SCM_CONNECTION value)
```

Specifies the tag that this project lives under. HEAD (meaning, the SCM root).

```properties
POM_SCM_TAG (default is "HEAD")
```

A publicly browsable repository.

```properties
POM_SCM_URL (default is POM_URL value)
```

#### Repositories

Repositories in the Release pom file, only id and url separated by comma.

```properties
POM_REPOSITORIES (example "mavenCentral,https\://repo1.maven.org/maven2/,jCenter,https\://jcenter.bintray.com/", default is "")
```

Repositories in the Snapshot pom file, only id and url separated by comma.

```properties
POM_SNAPSHOT_REPOSITORIES (example "mavenCentral,https\://oss.sonatype.org/content/repositories/snapshots/,jCenter,https\://oss.jfrog.org/artifactory/oss-snapshot-local/", default is POM_REPOSITORIES value)
```

#### Distribution Management

This is the url of the repository from whence another POM may point to in order to grab this POM's artifact.

```properties
POM_DIST_DOWNLOAD_URL (default is "")
```

## Groovydoc documentation

See [GradleMavenPush documentation](https://vorlonsoft.github.io/GradleMavenPush/groovydoc/)

## Already in use in following libraries

* [AndroidRate library](https://github.com/Vorlonsoft/AndroidRate)

* [ExpandableSelector library](https://github.com/Karumi/ExpandableSelector)

* ...

## Our other plugins

[EasyDokkaPlugin](https://github.com/Vorlonsoft/EasyDokkaPlugin) - Gradle Script plugin to generate documentation by Dokka documentation engine in Javadoc or other formats for Java, Kotlin, Android and non-Android projects. It's very easy, you don't need to add to `dependencies` section additional `classpath` or think about compatibility issues, you don't need additional repositories also.

## Contribute

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Added some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

## License

    Copyright 2018 Vorlonsoft LLC

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
