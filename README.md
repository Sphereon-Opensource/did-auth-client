# did-auth-client
## Dependencies
The did-auth-client doesn't seem to resolve dependencies when imported into another project without adding the following to the `build.gradle` of that project.
```groovy
repositories {
    ...
    maven { url "https://jitpack.io" }
    ...
}
``` 