# node-version-jenkins-plugin

**A Jenkins plugin to detect the version of the Node.js source code in the current workspace and insert it as a build parameter**

## Building

```
$ mvn install -DskipTests
```

Clean with

```
$ mvn clean
```

## Installing

Copy the resulting `target/node-version-jenkins-plugin.hpi` file into the Jenkins server plugin directory, e.g. `/var/lib/jenkins/plugins` and restart Jenkins.

## Using

This plugin extends the Git plugin and runs after the git checkout phase. Its functionality can be switched on in a Jenkins job config under ***Source Code Management*** → ***Additional Behaviours*** where you can click on the ***Add*** button and select ***Extract Node.js versions as parameters*** and then tick the ***ExportNodejsVersion=Export Node.js version as parameters from nodejs/node git source*** box.

Once enabled for a job, two new job parameters will be made available after the initial git checkout:

* `NODEJS_VERSION` - the three part semver string, excluding any tags (i.e. `-pre` won't be included for non-release builds)
* `NODEJS_MAJOR_VERSION` - the first part of the semver string

These are also inserted as environment variables into the build (ymmv using those).

This plugin couples well with the [Matrix Groovy Execution Strategy Plugin](https://wiki.jenkins.io/display/JENKINS/matrix+groovy+execution+strategy+plugin) which can make use of a `parameters` property within a full Groovy scripting environment to select the matrix options that should be built, and their ordering.

-----------------------------------

Managed under the governance of the Node.js [Build Working Group](https://github.com/nodejs/build).

Copyright (c) 2018 Node.js Foundation. All rights reserved.

Licensed under MIT, see the LICENSE.md file for details
