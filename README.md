GradleMavenPush
===============

Helper to upload Gradle Android Artifacts to Maven repositories

## Usage

### 1. Have a working Gradle build
It is up to you.

### 2. Update your home gradle.properties

This will include the username and password to upload to the Maven server and so that they are kept local on your machine. The location defaults to `USER_HOME/.gradle/gradle.properties`.

It may also include your signing key id, password, and secret key ring file (for signed uploads).  Signing is only necessary if you're putting release builds of your project on maven central.

```properties
NEXUS_USERNAME            = vorlonsoft
NEXUS_PASSWORD            = $tr0ngP@55w0rd

signing.keyId             = ABCDEF12
signing.password          = P@55w0rd
signing.secretKeyRingFile = ./secring.gpg
```

### 3. Create project root gradle.properties
You may already have this file, in which case just edit the original. This file should contain the POM values which are common to all of your sub-project (if you have any). For instance, here's [AndroidRate's](https://github.com/Vorlonsoft/AndroidRate):

```properties
VERSION_NAME           = 1.2.0-SNAPSHOT
VERSION_CODE           = 43
GROUP                  = com.vorlonsoft

POM_DESCRIPTION        = Library for Android applications, which provides rating dialog.
POM_URL                = https://github.com/Vorlonsoft/AndroidRate
POM_SCM_URL            = https://github.com/Vorlonsoft/AndroidRate
POM_SCM_CONNECTION     = scm:git@github.com:Vorlonsoft/AndroidRate.git
POM_SCM_DEV_CONNECTION = scm:git@github.com:Vorlonsoft/AndroidRate.git
POM_LICENCE_NAME       = The MIT License (MIT)
POM_LICENCE_URL        = https://opensource.org/licenses/MIT
POM_LICENCE_DIST       = repo
POM_DEVELOPER_ID       = AlexanderLS
POM_DEVELOPER_NAME     = Alexander Savin
```

The `VERSION_NAME` value is important. If it contains the keyword `SNAPSHOT` then the build will upload to the snapshot server, if not then to the release server.

### 4. Create gradle.properties in each sub-project
The values in this file are specific to the sub-project (and override those in the root `gradle.properties`). In this example, this is just the name, artifactId and packaging type:

```properties
POM_NAME        = AndroidRate Library
POM_ARTIFACT_ID = androidrate
POM_PACKAGING   = aar
```

### 5. Call the script from each sub-modules build.gradle

Add the following at the end of each `build.gradle` that you wish to upload:

```groovy
apply from: 'https://raw.github.com/Vorlonsoft/GradleMavenPush/master/gradle-mvn-push.gradle'
```

### 6. Build and Push

You can now build and push:

```bash
$ gradle clean build uploadArchives
```
	
### Other Properties

There are other properties which can be set:

```
RELEASE_REPOSITORY_URL (defaults to Maven Central's staging server)
SNAPSHOT_REPOSITORY_URL (defaults to Maven Central's snapshot server)
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