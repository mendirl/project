[![Project Build Status][github-actions-image]][github-actions-url]
 
[github-actions-url]: https://github.com/mendirl/project/actions

[github-actions-image]: https://github.com/mendirl/project/workflows/Java%20CI/badge.svg

#### DEPLOY & RELEASE

+ https://github.com/marketplace/actions/setup-java-jdk
+ https://docs.github.com/en/free-pro-team@latest/actions/guides/publishing-java-packages-with-maven

#### COMMAND

    ./mvnw verify
    ./mvnw verify -DskipTests
    ./mvnw verify -Pdocker
    ./mvnw verify -Pdocker,native
    ./mvnw verify -Pnative

#### SOME ISSUES

###### maven ci friendly

+ https://maven.apache.org/maven-ci-friendly.html
+ https://issues.apache.org/jira/browse/MRELEASE-935
+ https://issues.apache.org/jira/browse/MNG-6656

