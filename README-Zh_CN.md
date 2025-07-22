> [English](README.md) | 中文

# Resilix

这是一款用于编排复杂业务的框架。在您熟悉业务的情况下，可以将业务拆分成若干个最小的执行阶段，随后使用该框架将它们（被拆分的阶段）组合起来，形成一个完整的业务流。

## 快速开始

### 1. 添加依赖

> 目前仅支持使用 spring-boot-starter 的方式引入

```xml

<dependency>
    <groupId>top.huzz</groupId>
    <artifactId>spring-boot-starter-resilix</artifactId>
    <version>${resilix.version}</version>
</dependency>
```

### 2. 创建业务流

业务流需要实现 `Phase` 接口，一般情况下，可以直接使用枚举类来实现。下面是一个简单的业务流示例：

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

在该示例中，包含2个阶段 `SAY_HELLO`、`SAY_WORLD`

### 3. 创建业务流处理器

业务流处理器需要实现 `RunHandler` 接口。下面是实现上述业务流的处理器示例：

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

### 4. 执行业务流

业务流程可以在应用启动时执行，也可以在任何需要的地方执行，执行的时候需要使用 `RunHandlerManagerFactory`
来创建一个 `RunHandlerManager` 实例，并调用 `start()` 方法来执行业务流。下面是一个在应用启动时执行的示例：

```java

@Component
public class SayRunner implements ApplicationRunner {
    @Resource
    private RunHandlerManagerFactory runHandlerManagerFactory;

    @Override
    public void run(ApplicationArguments args) {
        // 您可以在任何地方执行业务流，这里只是作为示例在应用启动时执行一次
        RunHandlerManager manager = runHandlerManagerFactory.build(SayPhase.class);
        manager.start();
    }
}
```

如果一切顺利，您应该可以在控制台看到以下输出：

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

### 更多示例

| 示例项目                                                             | 说明        |
|------------------------------------------------------------------|-----------|
| [resilix-example-quickstart](example/resilix-example-quickstart) | 快速开始      |
| [resilix-example-advanced](example/resilix-example-use-ctx)      | 使用上下文传递变量 |

## 支持

如需支持，请发送电子邮件至 [huzz-resilix@gmail.com](https://mail.google.com/) 或
提交 [issue](https://github.com/huzz-open/resilix/issues/new)

