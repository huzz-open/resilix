package org.huzz.resilix.api.run.cache;


import org.huzz.resilix.api.run.RunContext;

/**
 * @author chenji
 * @since 1.0.0
 */
public interface Cleanable {
    void clean(RunContext context);
}
