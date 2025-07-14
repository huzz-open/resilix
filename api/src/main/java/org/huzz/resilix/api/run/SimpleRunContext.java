package org.huzz.resilix.api.run;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import org.huzz.resilix.api.constants.EnvType;

/**
 * @author chenji
 * @since 1.0.0
 */
@Getter
@Setter
public class SimpleRunContext extends AbstractRunContext {

    @JsonIgnore
    private Object extra;

    @Override
    @JsonDeserialize(as = EmptyPhase.class)
    public void setCurrentPhase(Phase phase) {
        this.currentPhase = phase;
    }

    @Override
    public EnvType getEnvType() {
        return null;
    }

    @Override
    public RunContext duplicate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cover(RunContext useToCovered) {
        throw new UnsupportedOperationException();
    }

}
