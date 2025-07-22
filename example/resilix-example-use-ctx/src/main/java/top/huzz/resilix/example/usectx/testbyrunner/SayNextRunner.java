package top.huzz.resilix.example.usectx.testbyrunner;

import jakarta.annotation.Resource;
import top.huzz.resilix.example.usectx.saynext.SayCustomPhase;
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
public class SayNextRunner implements ApplicationRunner {
    @Resource
    private RunHandlerManagerFactory runHandlerManagerFactory;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        RunHandlerManager manager = runHandlerManagerFactory.build(SayCustomPhase.class);
        manager.start();
    }
}
