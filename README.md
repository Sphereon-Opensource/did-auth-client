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

To add publish you can add publish.url.snapshot=https://.... and publish.url.releases=https://.... to your gradle.properties
To set its credentials execute the following:
```bash
gradle addCredentials --key nexusUser --value <insert-your-nexus-username>
gradle addCredentials --key nexusPassword --value <insert-your-nexus-password>
``` 

