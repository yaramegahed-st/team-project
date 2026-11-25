# Running the tests

Tests use JUnit 5 (Jupiter) and hand-written fakes (no Mockito needed).
Drop `src/test/java/**` into your project and add JUnit 5 to your build.

Maven:  org.junit.jupiter:junit-jupiter:5.10.x (scope test)
Gradle: testImplementation 'org.junit.jupiter:junit-jupiter:5.10.1'
        test { useJUnitPlatform() }
