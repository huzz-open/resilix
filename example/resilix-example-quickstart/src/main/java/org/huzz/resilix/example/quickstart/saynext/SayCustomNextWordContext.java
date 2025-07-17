package org.huzz.resilix.example.quickstart.saynext;

import lombok.Getter;
import lombok.Setter;
import org.huzz.resilix.api.run.SimpleRunContext;

/**
 * @author chenji
 * @since 1.0.0
 */
@Getter
@Setter
public class SayCustomNextWordContext extends SimpleRunContext {
    private String nextWord;
}
