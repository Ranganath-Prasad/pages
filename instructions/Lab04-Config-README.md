<<<<<<< HEAD:instructions/Lab04-Config-README.md
# Follow Instructions to Externalize the Welcome Message

## Code Change
- Verify the *HomeControllerTests.java* file to check the change in test. The *HomeController.java* needs to be changed to pass the test case.
- In *HomeController.java* create a String variable **pageContent** and intialize it through constructor using @Value annotation. Use the same variable in getPage() method to return welcome message. 
```java
package org.dell.kube.pages;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    private String pageContent;

    public HomeController(@Value("${page.content}") String pageContent){
        this.pageContent=pageContent;

    }
    @GetMapping
    public String getPage(){
        return "Hello from page : "+pageContent+" ";
    }


}
```
- Initialize the variable PAGE_CONTENT in build.gradle for bootRun and test closures by adding below statements
```groovy
bootRun.environment([
		"PAGE_CONTENT": "YellowPages",
])

test.environment([
		"PAGE_CONTENT": "YellowPages",
])
```
This would supply the value for pageContent during test and bootRun gradle tasks.
- Build and Test the application using
```shell script
./gradlew clean build
./gradlew bootRun
```
### Dockerization
- Build the application using gradle
- run the following commands to generate the Docker image
```shell script
docker build -t <docker_username>/pages:config .
``` 
- run the following command to run the image
```shell script
docker run --env PAGE_CONTENT="Green-Pages coming from Yellow-world!" -p 8080:8080 -t <docker_username>/pages:config
```
Then open the application at http://localhost:8080 to test it.

- Push the image to docker
```shell script
docker push <docker_username>/pages:config
```

### Running the image in Kubernetes
- Create a new file **pages-config.yaml* in deployment folder to initialize the variable in kubernetes. Use below content
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: pages-config-map
  namespace: <your-name>
data:
  PAGE_CONTENT: "Green-Pages coming from Yellow-world!"
```
- In *pages-deployment.yaml* replace the image tag name with *config*
- In *pages-deployment.yaml* make changes to set the PAGE_CONTENT value reading from the ConfigMap. Add the below content inside container section with proper alignment.
```yaml
env:
  - name: PAGE_CONTENT
    valueFrom:
      configMapKeyRef:
        name: pages-config-map
        key: PAGE_CONTENT
```
- Run the following commands for minikube and pks to run the application in kubernetes cluster. Follow the previous lab to check the instructions to run the application in *minikube* and *PKS*. In the file **pages-service.yaml** value for *type* would be *NodePort* and *LoadBalancer* for minikube and pks respectively. 
```shell script
kubectl apply -f deployment/pages-namespace.yaml
kubectl apply -f deployment/pages-config.yaml
kubectl apply -f deployment/pages-service.yaml
kubectl delete -f deployment/pages-deployment.yaml
kubectl apply -f deployment/pages-deployment.yaml
```
We are deleting the pages deployment first as we are adding ConfigMapRef in it.
- Verify the deployment, configMap and service are created by using the following command
```shell script
kubectl get deployment pages --namespace pages-<your-name>
kubectl get cm pages-config-map  --namespace pages-<your-name>
kubectl get service pages  --namespace pages-<your-name>
```
- Identify the url of the **pages** service from the output above command. Refer to the previous lab for instructions to know the url of the service for both minikube and pks
- Open the pages url in browser to test the application.  
- Push the code to github repository using below commands
```shell script
git add .
git commit -m "MESSAGE"
git push origin config-work:master
```
- Use the below command to checkout the required tag for next lab
```shell script
git checkout pipeline-start -n pipeline-work
```
=======
## Instruction to start the *Hello Application* from scratch
- Download the project zip file from [here](https://dell-edu-lab-store.s3.ap-south-1.amazonaws.com/repository/pages.zip) and extract it inside workspace folder
- Create a repository in git with the name **pages**. Keep everything default, while creating the repository, don't change anything other than default.
- Copy the *git remote add origin <repo address>* command from the landing page and execute it in the directory 
- Create a build.gradle file with following content

```groovy
plugins {
	id 'org.springframework.boot' version '2.3.1.RELEASE'
	id 'io.spring.dependency-management' version '1.0.9.RELEASE'
	id 'java'
}

group = 'com.example'
sourceCompatibility = '11'

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-web'
	testImplementation('org.springframework.boot:spring-boot-starter-test') {
		exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
	}
}

test {
	useJUnitPlatform()
}
```
- Create the gradle ecosystem by using the following commands
```shell script
    gradle wrapper --gradle-version 6.4.1 --distribution-type all
``` 
- Create a .gitignore file with following content
```text
HELP.md
.gradle
build/
!gradle/wrapper/gradle-wrapper.jar
!**/src/main/**
!**/src/test/**

### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr
out/

### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/

### VS Code ###
.vscode/
```
- Open the project in Intellij Idea, select the import gradle project option in bottom right corner and  set project SDK to JDK 11
- Create two folders **src/main/java** and **src/test/java** under project root directory. Mark them as sources root and test root respectively.
- Create two packages **org.dell.kube.pages** and **org.dell.kube.pagesapi** under *src/test/java*
- Create a Test class called **PagesApplicationTests.java** under package **org.dell.kube.pages** with below content
```java
package org.dell.kube.pages;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PagesApplicationTests {

	@Test
	void contextLoads() {
	}

}
```
- Create a Test class called **HomeControllerTests.java** under package **org.dell.kube.pages** with below content
```java
package org.dell.kube.pages;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HomeControllerTests {
    private final String message = "YellowPages";

    @Test
    public void itSaysYellowPagesHello() throws Exception {
        HomeController controller = new HomeController();

        assertThat(controller.getPage()).contains(message);
    }


}
```
- Create a Test class called **HomeApiTest** under the package **org.dell.kube.pagesapi** with below content
```java
package org.dell.kube.pagesapi;

import org.dell.kube.pages.PageApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = PageApplication.class, webEnvironment = RANDOM_PORT)
public class HomeApiTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Test
    public void readTest() {
        String body = this.restTemplate.getForObject("/", String.class);
        assertThat(body).contains("YellowPages");
    }

    @Test
    public void healthTest(){
        String body = this.restTemplate.getForObject("/actuator/health", String.class);
        assertThat(body).contains("UP");
    }
}
```
- Create a *settings.gradle* file with below content in the root project
```groovy
rootProject.name = 'pages'
```
- Push the content to the local git repository
```shell
git add .
git commit -m "MESSAGE"
```
- For the current monolith app there are 8 labs. The lab could be started by checking out a tag. All the tag names are provided in sequence. The tag name would be [lab-name]-start. For each lab there would be a file containing instructions to carry out  under *instructions* directory. The instruction files follow a naming convention as *Lab[number]-[Lab Description]-README.md*. For example for docker lab which is lab number 2 the instruction file name would be instructions/*Lab02-Docker-README.md*. The instruction file would be added to the local repository once we check out the tag.
For each lab we have to checkout the tag into a new branch. The branch name would be [tag-name-minus-start]-work. For example for  tag *docker-start* branch-name would be **docker-work**. The tags needs to be checked out in following sequence.

  - *hello-start* 
  - *docker-start*
  - *kubernetes-demo* 
  - *kubernetes-start*
  - *config-start*
  - *pipeline-start*
  - *log-start*
  - *inmemory-start*
  - *persistence-start*
For example if we have to check out *docker-start* tag we need to use the below command.
```sh
git checkout docker-start -b docker-work
```
This checkout would pick  a new file instructions/*Lab02-Docker-README.md* which contains instructions for the lab. Once the lab is completed we need to commit and push the code using below command.
```sh
git add .
git commit -m "MESSAGE"
git push origin docker-work:master -f
```
The same needs to be repeated for each lab. Now to start with, checkout **hello-start** tag so that you will get instructions/Lab01-Hello-README.md file. The command to checkout the tag into the branch would be as below.
```sh
git checkout hello-start -b hello-work
```



>>>>>>> e21528addd90d01298bb7a103d2b49e008baec87:README.md
