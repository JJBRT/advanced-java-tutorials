# Advanced Java tuorials

<img src="https://github.com/JJBRT/advanced-java-tutorials/blob/master/Java-logo.png" alt="Java-logo.png" height="180px" align="right"/>

A collection of examples about **advanced Java programming**. Here you will find tutorials about:

* [how to create your own dependency injection framework](https://dev.to/bw_software/how-to-create-your-own-dependency-injection-framework-o2l)
* [how to make applications created with old Java versions work on Java 9 and later versions](https://dev.to/bw_software/making-applications-created-with-old-java-versions-work-on-java-9-and-later-versions-19ld)

## Instructions
For the tutorial tutorial related to "[how to make applications created with old Java versions work on Java 9 and later versions](https://dev.to/bw_software/making-applications-created-with-old-java-versions-work-on-java-9-and-later-versions-19ld)" in the folder "[spring-boot-application-adapter-tutorial]"(https://github.com/JJBRT/advanced-java-tutorials/tree/master/spring-boot-application-adapter-tutorial) notice that to make it works you need:

* to set the property '**paths.jdk-home**' of [**burningwave.properties**](https://github.com/JJBRT/advanced-java-tutorials/blob/master/spring-boot-application-adapter-tutorial/src/test/resources/burningwave.properties#L1) file with a jdk 8 home

* to run with a jdk 9 or later the [**DependenciesAdapter**](https://github.com/JJBRT/advanced-java-tutorials/blob/master/spring-boot-application-adapter-tutorial/src/test/java/it/springbootappadapter/DependenciesAdapter.java)
