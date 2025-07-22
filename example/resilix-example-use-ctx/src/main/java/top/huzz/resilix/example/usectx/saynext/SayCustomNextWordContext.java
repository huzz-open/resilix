package top.huzz.resilix.example.usectx.saynext;

import lombok.Getter;
import lombok.Setter;
import top.huzz.resilix.core.SimpleRunContext;

/**
 * @author chenji
 * @since 1.0.0
 */
@Getter
@Setter
public class SayCustomNextWordContext extends SimpleRunContext {
    private String nextWord;
}
