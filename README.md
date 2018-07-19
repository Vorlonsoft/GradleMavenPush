GradleMavenPush
===============

Helper to upload Gradle Android Artifacts and Gradle Java Artifacts to Maven repositories (JCenter, Maven Central, Corporate staging/snapshot servers and local Maven repositories)

## Usage

### 1. Have a working Gradle build
It is up to you.

### 2. Update your home gradle.properties

This will include flag IS_JCENTER (default is "false" - Maven Central, "true" - JCenter), username and password to upload to the Maven server and so that they are kept local on your machine. The location defaults to `USER_HOME/.gradle/gradle.properties`.

It may also include your signing key id, password, and secret key ring file (for signed uploads).  Signing is only necessary if you're putting release builds of your project on Maven Central or JCenter.

```properties
IS_JCENTER                = false
NEXUS_USERNAME            = vorlonsoft
NEXUS_PASSWORD            = $tr0ngP@55w0rd

signing.keyId             = ABCDEF12
signing.password          = P@55w0rd
signing.secretKeyRingFile = ./secring.gpg
```

#### 2.1. Alternative, use environment variables

You can modify username and password from environment variables (useful for CI). To use those environment variables on CI just export them:

```properties
export NEXUS_USERNAME      = vorlonsoft
export NEXUS_PASSWORD      = $tr0ngP@55w0rd
```

### 3. Create project root gradle.properties
You may already have this file, in which case just edit the original. This file should contain the POM values which are common to all of your sub-project (if you have any). For instance, here's [AndroidRate's](https://github.com/Vorlonsoft/AndroidRate):

```properties
# VERSION_NAME (default is build.gradle versionName)
VERSION_NAME           = 1.2.0-SNAPSHOT
# GROUP (default is packageName)
GROUP                  = com.vorlonsoft

POM_DESCRIPTION        = Library for Android applications, which provides rating dialog.
POM_URL                = https://github.com/Vorlonsoft/AndroidRate
POM_SCM_CONNECTION     = scm:git@github.com:Vorlonsoft/AndroidRate.git
POM_LICENCE_NAME       = The MIT License (MIT)
POM_LICENCE_URL        = https://opensource.org/licenses/MIT
POM_DEVELOPER_ID       = AlexanderLS
POM_DEVELOPER_NAME     = Alexander Savin
POM_DEVELOPER_EMAIL    = info@vorlonsoft.com
```

The `VERSION_NAME` value is important. If it contains the keyword `SNAPSHOT` then the build will upload to the snapshot server, if not then to the release server.


### 4. Modify the version name from environment variable

If there's an environment variable called `VERSION_NAME_EXTRAS`, its value will get appended at the end of `VERSION_NAME`.
This can be very powerful when running from CI. For example, to have one SNAPSHOT per branch, you could

```properties
export VERSION_NAME_EXTRAS = -master-SNAPSHOT
```
in this case it will be uploaded to the snapshot server and indicates it's from the master branch.

### 5. Create gradle.properties in each sub-project
The values in this file are specific to the sub-project (and override those in the root `gradle.properties`). In this example, this is just the name, artifactId and packaging type:

```properties
POM_NAME        = AndroidRate Library
POM_ARTIFACT_ID = androidrate
```

Also you can set `POM_ARTIFACT_URL`, this is makes to easier to have an artifact with one artifactId but the name on JCenter something else.

### 6. Call the script from each sub-modules build.gradle

Add the following at the end of each `build.gradle` that you wish to upload:

```groovy
apply from: 'https://raw.github.com/Vorlonsoft/GradleMavenPush/master/maven-push.gradle'
```

### 7. Build and Deploy/Install

You can now build and *deploy* on JCenter, Maven Central or Corporate staging/snapshot servers:

```bash
$ gradle clean build uploadArchives
```

Build and *install* on local Maven (~/.m2/repository/):

```bash
$ gradle clean build install
```

Build and *deploy* on local Maven (~/.m2/repository/):

```bash
$ gradle clean build installArchives
```

### Other Properties

There are other properties which can be set:

```properties
RELEASE_REPOSITORY_URL (defaults to Maven Central's or JCenter's staging server (depends on IS_JCENTER))
SNAPSHOT_REPOSITORY_URL (defaults to Maven Central's or JCenter's snapshot server (depends on IS_JCENTER))
POM_GENERATE_UNIQUE_SNAPSHOTS (default is "true")
POM_SCM_URL (default is POM_URL value)
POM_SCM_DEV_CONNECTION (default is POM_SCM_CONNECTION value)
POM_LICENCE_DIST (default is "repo")
POM_ORG (default is "")
POM_ORG_URL (default is "")
POM_DEVELOPER_ORG (default is POM_ORG value)
POM_DEVELOPER_ORG_URL (default is POM_ORG_URL value)
POM_DEVELOPER_ROLE (default is "developer")
POM_DEVELOPER_ROLES (example "architect,developer", default is "")
POM_PACKAGING (default is "aar")
DOCLINT_CHECK (default is "false")
JAVADOC_ENCODING (default is "UTF-8")
JAVADOC_CHARSET (default is "UTF-8")
```

## Already in use in following libraries

* [AndroidRate library](https://github.com/Vorlonsoft/AndroidRate)

* ...

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