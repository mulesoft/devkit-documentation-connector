
# Devkit Documentation Connector

This project is a Cloud Connector made for the purpose of showing all the new features availables in devkit.

It makes use of the Devkit Documentation Service.

## Installation Requirements
1. AnyPoint Studio to use the connector
2. Devkit Documentation Service, it might use the hosted in the cloud version, or can be downloaded and runned locally.

## Installation
> For the tests to run, the credential.properties must be pointing to a running instance of the _Devkit Documentation Service_

	> mvm clean install

or if you want to skip the tests

	> mvn clean install -DskipTests

1. Open AnyPoint Studio and go to _help > Install New Software_.
2. Add a new site pointing to _{connector_path}/target/update-site/_.
3. Install the connector plugin
4. Restart Studio and it should be visible now in the _Connectors_ section in the pallete.





