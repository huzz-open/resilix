package org.huzz.resilix.example.quickstart.testbyrunner;

import org.huzz.resilix.api.run.DefaultRunHandlerManager;
import org.huzz.resilix.api.run.RunHandlerManager;
import org.huzz.resilix.example.quickstart.sayhello.SayPhase;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author chenji
 * @since 1.0.0
 */
@Component
public class SayRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        RunHandlerManager manager = DefaultRunHandlerManager.build(SayPhase.class);
        manager.start();
    }
}
