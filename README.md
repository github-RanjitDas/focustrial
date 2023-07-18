# Android-FOCUS-Mobile-APP-By-Safe-Fleet
Contains new features such as SSO, Nexus Support and BLE.
# New Features:
1) Nexus Support
2) BLE Support for Validation and reading configuration
3) SSO for Nexus


## Pre-commit format verification
To enable the ktlint check pre-commit make sure to run build before commiting any changes to install it automatically.
If there are any format issues your commit will fail and show what files have errors in formatting.
After all files format is correct the commit should succeed.

### Auto format files
To make ktlint do the format for you, use the Android Studio "Gradle" tab, go to project "Tasks" folder, then go to "formatting" and run the task "ktlintFormat".
Or write and run the following command in the Android Studio Terminal tab:

    gradlew ktlintFormat

And it will format all the files, but there are some exceptions that cannot be auto-formatted and you have to do it manually.

## Create mutation and coverage reports for SonarQube
To ensure the quality of the project you must run the following gradle tasks before create a Pull Request:

### Run unit tests
In order to run unit test use the Android Studio "Gradle" tab, go to project "Tasks" folder, then go to "verification" folder and double click on "testDebugUnitTest".
Or write and run the following command in the Android Studio Terminal tab:

    gradlew testDebugUnitTest

### Create unit test coverage report
In order to create unit test coverage report use the Android Studio "Gradle" tab, go to project "Tasks" folder, then go to "reporting" folder and double click on "testDebugUnitTestCoverage".
Or write and run the following command in the Android Studio "Terminal" tab:

    gradlew testDebugUnitTestCoverage

#### Local results

This task will run "testDebugUnitTest" in case you have not run it, but it also will create an xml and html report that can be found in each module build folder:

    <module>\build\reports\jacoco\testDebugUnitTestCoverage.xml     //this xml report file is the one that will be uploaded to the SonarQube server
    <module>\build\reports\jacoco\html\index.html

### Run mutation tests and analyze results on SonarQube
In order to run mutation tests use the Android Studio "Gradle" tab, go to project "Tasks" folder, then go to "verification" folder and double click on "pitestDebug".
Or write and run the following command in the Android Studio "Terminal" tab:

    gradlew pitestDebug

#### Local results

This task will run unit tests and mutation analysis on each of the app modules (presentation, data and domain) and generate one HTML report for each module. This report can be found at:

    <module>/build/reports/pitest/debug/index.html
	<module>/build/reports/pitest/release/index.html

### upload reports to SonarQube
Before upload all the reports to SonarQube, we need to execute a script located on the project root folder:

    sh merge_mutation_reports.sh

This script combines results of each of the project modules into a single file located in **build/reports/pitest/mutations.xml** which will be used by SonarQube to get the mutation results.

Then we just need to run:

    gradlew sonarqube

And results will be uploaded to SonarQube so you can check the quality gates before merge your branch.

***Note:** In order to be able to analyze mutation results, the **Mutation Analysis** plugin needs to be installed on SonarQube and the mutation analysis rules need to be added to the project **quality profile***