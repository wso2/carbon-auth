# Carbon Auth
---

|  Branch | Build Status |
| :------------ |:-------------
| master      | [![Build Status](https://wso2.org/jenkins/job/platform-builds/job/carbon-auth/badge/icon)](https://wso2.org/jenkins/job/platform-builds/job/carbon-auth/) |

---

Carbon Auth is a common authentication platform for Carbon 5 based products.

#### Carbon Auth contains several components such as:

* OAuth2 Client Registration
* OAuth2 Token Endpoint
* OAuth2 Token Introspection
* OAuth2 Scope Registration
* SCIM
* User Info
* User Stores

## How to build from the source
### Prerequisites
* Java 8 or above
* [Apache Maven](https://maven.apache.org/download.cgi#) 3.x.x
### Steps
1. Install above prerequisites if they have not been already installed
2. Get a clone from [this](https://github.com/wso2/carbon-auth.git) repository
3. Run one of the following maven commands from carbon-auth directory
   * To build with the tests
        ```bash
         mvn clean install 
        ```
   * To build without running any unit/integration test
        ```bash
         mvn clean install -Dmaven.test.skip=true
        ```
## How to Contribute
* Please report issues at [Carbon Auth Github Issues](https://github.com/wso2/carbon-auth/issues)
* Send your bug fixes pull requests to the [master branch](https://github.com/wso2/carbon-auth/tree/master)

## Contact us
WSO2 Carbon developers can be contacted via the mailing lists:

* Carbon Developers List : dev@wso2.org
* Carbon Architecture List : architecture@wso2.org

