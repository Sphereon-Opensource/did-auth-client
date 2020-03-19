# DID Auth Client Library
The DID Auth Client combines useful tools for implementing DID authentication into your own login solution. 
## Dependencies
When importing the DID Auth Client into your project, add the jitpack repository in your `build.gradle` to be able to resolve all uPort Kotlin dependencies.
```groovy
repositories {
    ...
    maven { url "https://jitpack.io" }
    ...
}
```

To be able publish you must setup your Maven repository URLs in your ~/gradle/gradle.properties file.
```bash
did-auth.publish.url.snapshot=https://my.nexus.com/nexus/content/repositories/snapshots
did-auth.publish.url.releases=https://my.nexus.com/nexus/content/repositories/releases
``` 
To set the credentials for the repository, execute the following:
```bash
gradle addCredentials --key nexusUser --value <insert-your-nexus-username>
gradle addCredentials --key nexusPassword --value <insert-your-nexus-password>
``` 

