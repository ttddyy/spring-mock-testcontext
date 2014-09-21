# About

This is my **proof of concept** project to implement `Auto Mock Feature` in Spring Test Context Framework.


# Feature Concept

## Preface

While writing tests with mock-frameworks, I have to explicitly prepare mock objects: such as instantiating mocks or annotating mocked variables, as well as resetting or re-assigning mocks between tests.
I think these code can be considered as infrastructure code for using mock-frameworks.
The `Auto Mock Feature` is to automate these mocking infrastructure logic.  
In other word, when this feature is enabled, automatically inject mock beans if dependency injection cannot resolve the target beans from user specified application configurations.


**Sample:**

```java
@EnableMock    // something like this to enable feature (TestBootstrapper)
@ContextConfiguration(...)
public class MyTest {

  // test target bean, declared in bean config 
  @Autowired
  MyController myController;   // actual bean will be injected

  // MyController has dependency to this bean but NOT declared in bean config for this test
  @Autowired
  MyService myService;  // automatically mock will be injected

  @Test
  public void myTest(){
     given(myService.getSomething).willReturn(...);   // prepare mock

     myController.doSomething();   // call test target method
     
     ... // verify logic
  }
}

```


# Implementation

## Impl Concept

In order to create mock beans, I used hierarchical structure of BeanFactory.
I created a MockBeanFactory which manages mocking beans, and place it to the root ApplicationContext's root BeanFactory in TestContext.

Since spring's DI mechanism searches candidate beans from child to parent context(bean factory), when child context(TestContext) cannot resolve the bean, the MockBeanFactory at last generates a mock bean and inject to the target variable.


## Bean resolution logic in BeanFactory

This is how BeanFactory(DefaultListableBeanFactory) finds target bean:
- the child BeanFactory looks up candidate beannames at once including ancestors(parents)
- iterate candidate beannames to check whether each candidate qualifies for the injection target (such as qualifier name, etc)
- retrieve actual bean



## MockBeanFactory Actual Impl

In actual implementation, it differs a bit from my impl concept.  
MockBeanFactory delays actual mock creation until bean retrieval time in order to gather all necessary information to decide whether to create a new mock or to return existing mock bean.


## Classes

*[MyController](src/main/java/net/ttddyy/mocktestcontext/controller/MyController.java), [FooService](src/main/java/net/ttddyy/mocktestcontext/service/FooService.java), [BarService](src/main/java/net/ttddyy/mocktestcontext/service/BarService.java)* : Sample controller and service

*[MyTest](src/test/java/net/ttddyy/mocktestcontext/MyTest.java)* : Sample controller unit test (not in web environment)

*[MyWebTest](src/test/java/net/ttddyy/mocktestcontext/MyWebTest.java)* : Sample controller unit test (with web environment)

*[MockBeanFactory](src/test/java/net/ttddyy/mocktestcontext/MockBeanFactory.java)* : BeanFactory implementation to manage mock beans

*[MockManager](src/test/java/net/ttddyy/mocktestcontext/MockInfoManager.java), [MockInfo](src/test/java/net/ttddyy/mocktestcontext/MockInfo.java)*: Manage mock information

*[MockAnnotationConfigContextLoader](src/test/java/net/ttddyy/mocktestcontext/MockAnnotationConfigContextLoader.java), [MockAnnotationConfigWebContextLoader](src/test/java/net/ttddyy/mocktestcontext/MockAnnotationConfigWebContextLoader.java)*: ContextLoader for this feature. (Will be implemented as TestBootstrapper)



# TODOs

- make pluggable strategy to create/reset/(destroy) mocks
- improve MockBeanFactory impl
- expose mocking info to BeanFactory, so that user can inject the info to tests
- make feature TestBootstrapper
- automatically reset mocks in each test
- etc.

