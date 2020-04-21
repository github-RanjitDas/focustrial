
# focus-mobile-android-v3

## Run mutation tests and analyze results on Sonarqube
For running mutation tests in the project, you just need to execute:

    gradlew pitest

#### Local results
This will run unit tests and mutation analysis on each of the app layers (presentation, data and domain) and generate one HTML report for each of the layers. This report can be found at:

    <layer>/build/reports/pitest/debug/index.html
	<layer>/build/reports/pitest/release/index.html
#### Sonarqube
In order to check results on Sonarqube, we need to execute first a script located on the project root folder:

    sh merge_mutation_reports.sh
This script combines results of each of the project layers into a single file located in **build/reports/pitest/mutations.xml** which will be used by sonarqube to get the mutation results. After this, we just need to run:

    gradlew sonarqube
And results will be uploaded to sonarqube.

***Note:** In order to be able to analyze mutation results, the **Mutation Analysis** plugin needs to be installed on SonarQube and the mutation analysis rules need to be added to the project **quality profile***
