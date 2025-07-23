> [中文](README-Zh_CN.md) | English

# Resilix

This is a framework designed for orchestrating complex business processes. After decomposing your business into minimal
executable phases, you can use this framework to assemble them into a complete business flow.

## Quick Start

### 1. Add Dependency

> Currently only supports integration via spring-boot-starter

```xml

<dependency>
    <groupId>top.huzz</groupId>
    <artifactId>spring-boot-starter-resilix</artifactId>
    <version>${resilix.version}</version>
</dependency>
```

available versions can be found at [Maven Central](https://search.maven.org/artifact/top.huzz/spring-boot-starter-resilix).

### 2. Create Business Flow

The business flow needs to implement the `Phase` interface. Typically, you can implement it using an enum class. Here's
a simple example:

```java
public enum SayPhase implements Phase {
    SAY_HELLO,
    SAY_WORLD,
    ;

    @Override
    public Phase[] getValues() {
        return values();
    }
}
```

This example contains 2 phases: `SAY_HELLO` and `SAY_WORLD`.

### 3. Create Business Flow Handlers

Business flow handlers need to implement the `RunHandler` interface. Below are handlers for the above business flow:

```java

@Component
public class SayHelloRunHandler extends PredictableRunHandler<SimpleRunContext> {
    protected SayHelloRunHandler(List<HandlerRunPredicate<SimpleRunContext>> handlerRunPredicates) {
        super(handlerRunPredicates);
    }

    @Override
    public void handle(SimpleRunContext context) {
        System.out.println("hello");
    }

    @Nonnull
    @Override
    public Phase phase() {
        return SayPhase.SAY_HELLO;
    }
}

@Component
public class SayWorldRunHandler extends PredictableRunHandler<SimpleRunContext> {
    protected SayWorldRunHandler(List<HandlerRunPredicate<SimpleRunContext>> handlerRunPredicates) {
        super(handlerRunPredicates);
    }

    @Override
    public void handle(SimpleRunContext context) {
        System.out.println("world");
    }

    @Nonnull
    @Override
    public Phase phase() {
        return SayPhase.SAY_WORLD;
    }
}
```

### 4. Execute Business Flow

The business flow can be executed during application startup or anywhere needed. To execute, use
`RunHandlerManagerFactory` to create a `RunHandlerManager` instance and call its start() method. Example during
application
startup:

```java

@Component
public class SayRunner implements ApplicationRunner {
    @Resource
    private RunHandlerManagerFactory runHandlerManagerFactory;

    @Override
    public void run(ApplicationArguments args) {
        // You can execute the flow anywhere - this example runs at startup
        RunHandlerManager manager = runHandlerManagerFactory.build(SayPhase.class);
        manager.start();
    }
}
```

If successful, you should see this console output:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.4.5)

2025-07-22T23:41:20.764+08:00  INFO 27900 --- [           main] t.h.r.e.quickstart.ResilixApplication    : Starting ResilixApplication using Java 17.0.10 with PID 27900 (C:\data\documents\code\huzz-open\resilix\example\resilix-example-quickstart\target\classes started by chenji in C:\data\documents\code\huzz-open\resilix)
2025-07-22T23:41:20.767+08:00  INFO 27900 --- [           main] t.h.r.e.quickstart.ResilixApplication    : No active profile set, falling back to 1 default profile: "default"
2025-07-22T23:41:21.370+08:00  INFO 27900 --- [           main] t.h.r.e.quickstart.ResilixApplication    : Started ResilixApplication in 1.048 seconds (process running for 1.63)
hello
world

Process finished with exit code 0
```

### More Examples

| Example Project                                                  | Description                     |
|------------------------------------------------------------------|---------------------------------|
| [resilix-example-quickstart](example/resilix-example-quickstart) | Quick Start                     |
| [resilix-example-advanced](example/resilix-example-use-ctx)      | Passing Variables Using Context | 

## Support

For support, please email [huzz.resilix@gmail.com](https://mail.google.com/) or submit
an [issue](https://github.com/huzz-open/resilix/issues/new)

