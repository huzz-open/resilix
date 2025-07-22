package top.huzz.resilix.example.quickstart.testbyrunner;

import jakarta.annotation.Resource;
import top.huzz.resilix.example.quickstart.sayhello.SayPhase;
import top.huzz.resilix.core.RunHandlerManager;
import top.huzz.resilix.core.RunHandlerManagerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author chenji
 * @since 1.0.0
 */
@Component
public class SayRunner implements ApplicationRunner {
    @Resource
    private RunHandlerManagerFactory runHandlerManagerFactory;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        RunHandlerManager manager = runHandlerManagerFactory.build(SayPhase.class);
        manager.start();
    }
}
