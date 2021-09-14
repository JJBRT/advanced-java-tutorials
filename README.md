# Advanced Java tutorials
<a href="https://jjbrt.github.io/advanced-java-tutorials/">
<img src="https://raw.githubusercontent.com/JJBRT/advanced-java-tutorials/master/Java-logo.png" alt="Java-logo.png" height="180px" align="right"/>
</a>
A collection of examples about **advanced Java programming**. Here you will find tutorials about:

* [how to create your own dependency injection framework](https://dev.to/bw_software/how-to-create-your-own-dependency-injection-framework-o2l)
* [how to make applications created with old Java versions work on Java 9 and later versions](https://dev.to/bw_software/making-applications-created-with-old-java-versions-work-on-java-9-and-later-versions-19ld)
* [how to make reflection fully work on JDK 16 and later](https://dev.to/jjbrt/how-to-make-reflection-fully-work-on-jdk-16-and-later-ihp)


## Instructions
For the tutorial related to "[how to make applications created with old Java versions work on Java 9 and later versions](https://dev.to/bw_software/making-applications-created-with-old-java-versions-work-on-java-9-and-later-versions-19ld)" in the folder "[spring-boot-application-adapter](https://github.com/JJBRT/advanced-java-tutorials/tree/master/spring-boot-application-adapter)" note that to make it works you need:

* to set the property '**paths.jdk-home**' of [**burningwave.properties**](https://github.com/JJBRT/advanced-java-tutorials/blob/master/spring-boot-application-adapter/src/test/resources/burningwave.properties#L1) file with a [JDK 8](https://www.oracle.com/it/java/technologies/javase/javase-jdk8-downloads.html) home

* to run with a [JDK 9](https://www.oracle.com/it/java/technologies/javase-downloads.html) or later the [**DependenciesAdapter**](https://github.com/JJBRT/advanced-java-tutorials/blob/master/spring-boot-application-adapter/src/test/java/org/springbootappadapter/DependenciesAdapter.java)
