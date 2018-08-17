# CHANGELOG

## 1.6.0 Yokohama (released 17.08.2018)

- LIBff60817 maven-push.gradle detectable by GitHub Linguist library
- LIBff50817 .gitattributes file for GitHub Linguist library added
- LIBff40817 Groovydocs added
- LIBff30817 Ant Build file for groovydoc added
- LIBcc20817 isAndroid() check added to MavenPush Class methods
- LIBff20817 "var" Gradle Android Artifact support
- LIBff10817 *.cpp and *.groovy files support
- LIBcc10817 Cleaning
- LIBff10816 Pull request from [EasyDokkaPlugin](https://github.com/Vorlonsoft/EasyDokkaPlugin)
- LIBcc30816 Inspection
- LIBcc20816 Documenting
- LIBcc10815 Documenting
- LIBcc50814 README.md update
- LIBcc40814 downloadLib(...) method update
- LIBcc10814 downloadLib(...) method update
- LIBff10814 Process AAR dependencies
- LIBbb10814 Sets the artifacts base file name to match the artifact ID in any case
- LIBff10808 dokka tasks merged
- LIBff90807 sources tasks merged
- LIBff80807 apklib tasks merged
- LIBff60807 MavenPushInitializer Class added
- LIBcc20807 isAndroid() added
- LIBbb10807 Can't find isAndroid()
- LIBff50807 MavenPushUtils Class added
- LIBff40807 InvalidUserDataException class added
- LIBff30807 Move getJavaAPISpecificationLink(...) to MavenPush Class
- LIBff20807 Move MavenPush Class out
- LIBff10807 Refactoring
- LIBff40806 compileOnly excluded from fatjar
- LIBbb30806 `GROUP` and `VERSION_NAME` default values updated
- LIBcc30806 static pomFinalizer(...) added
- LIBff30806 `ANDROID_JAR_MAIN_CLASS` added to add "Main-Class" attribute to Android's "var", "jar" and "fatjar" `MANIFEST.MF` files.
- LIBff20806 non-Android fatjar manifest update
- LIBcc10805 "Our other pligins" section added
- LIBbb10805 isAndroid() checks added

## 1.5.0 Tokyo (released 03.08.2018)

- LIBcc30803 "Already in use in following libraries" section update
- LIBcc20803 .gradle added to .gitignore
- LIBff20803 Optimization and refactoring
- LIBcc10803 .gitignore changed
- LIBff10803 Add MavenPush.groovy Groovy Class
- LIBff40731 Add "6.2 Dokka configuration (optional)" section to README.md
- LIBcc30731 Add description about POM_PACKAGING property.
- LIBff30731 Added Gradle Android/Java/Kotlin Artifact fatjar option
- LIBff20731 Build and Deploy/Install gradle commands update
- LIBcc20731 README.md update
- LIBcc10731
- LIBff10731 Added Gradle Android Artifact jar option
- LIBbb20728 isKotlin() updated
- LIBcc20728 Kotlin source paths updated
- LIBcc10728 Inspection
- LIBff20728 Tasks for dokka-android and dokka plugins added
- LIBbb10728 Kotlin sources packaging update
- LIBff10728 dokkaInitializer added
- LIBff10726 Dokka support added. Documentation engine for Kotlin, supports Java/Kotlin projects.
- LIBff20724 JAVADOC_HTML_VERSION property. Javadoc HTML4/HTML5 options for Java 9+.
- LIBff10724 sources.jar for Android Kotlin projects
- LIBcc30724 Set the archives configuration.
- LIBcc20724 Sort tags in pom files
- LIBcc10724 API Key for JCenter, not password
- LIBcc30723 Move dependencies section at the bottom of pom files
- LIBbb10723 Add Javadoc boolean option ('html4', true) for Java 9+
- LIBcc20723 "Already in use in following libraries" section of README.md update
- LIBcc10723 "Other Properties" section of README.md update
- LIBff30723 Add Distribution Management section to pom file
- LIBff20723 Add POM_SNAPSHOT_REPOSITORIES property for Repositories section of pom file
- LIBff10723 Add Repositories section to pom file
- LIBcc40722
- LIBbb10722 Pom file generation update
- LIBcc30722 POM_DEVELOPER_ROLE and POM_DEVELOPER_ROLES code update
- LIBff20722 POM_SCM_TAG (Default is "HEAD") added. Specifies the tag that this project lives under. HEAD (meaning, the SCM root) should be the default.
- LIBff10722 Inception Year, Additional Developers, Contributors, CI Management, Mailing Lists, LicenseComments, DeveloperUrl, DeveloperTimezone added to pom
- LIBff10721 Javadoc docEncoding can be set
- LIBcc20721
- LIBbb20721 Javadoc generation update
- LIBcc10721 Default pomConfig for pom files
- LIBbb10721 Fix javadoc for Gradle Java Artifacts
- LIBff20720 Ability to generate Gradle Android Artifact apklib
- LIBbb10720 Set POM_PACKAGING default to "jar" for Gradle Java Artifacts
- LIBcc10720 Remove unnecessary requests to properties
- LIBff10720 Option to add Issue tracking system to pom file
- LIBbb10719 Remove empty pom tags

## 1.1.0 (released 19.07.2018)

- LIBcc10719 Default developer role update
- LIBff20719 Inter-module dependency
- LIBff10719 Add JCenter option
- LIBff30718 Configuration for java plugin
- LIBff20718 Allow for multiple developer roles
- LIBbb20718
- LIBcc10718 README.md update
- LIBff10718 Support both android and java projects
- LIBbb10718 Set failOnError to "false" for task androidJavadocs
- LIBbb10717 Fix for "No signature of method: java.lang.Object.uniqueVersion() is applicable for argument types: (java.util.Collections$EmptyMap, java.lang.Boolean) values: [[:], true]"
- LIBcc10717
- LIBff10717 Add installArchives task which installs jars/aars in local repo
- LIBff30715 Add lookup to set uniqueVersion
- LIBff20715 POM_ORG, POM_ORG_URL and POM_DEVELOPER_ROLE added
- LIBbb10715 Workaround for "Given organization in generated POM is evaluated to org.apache.maven.model.Organization.toString()"
- LIBff10715 Add properties getters to use gradle.build values
- LIBff50714 Adding developer metadata to pom
- LIBcc70714
- LIBff40714 Javadoc encoding and charSet can be set
- LIBff30714 Disable doclint in JDK 8 Javadoc
- LIBff20714 Add javadoc charset

## 1.0.0 (released 14.07.2018)

- Initial Public Version