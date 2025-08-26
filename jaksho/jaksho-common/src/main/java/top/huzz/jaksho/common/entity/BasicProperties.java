package top.huzz.jaksho.common.entity;

import top.huzz.jaksho.common.able.DomainDescription;

/**
 * @author chenji
 * @since 1.0.2
 */
public abstract class BasicProperties implements DomainDescription {

    public Integer getId() {
        return null;
    }

    public abstract Integer getWorkspaceId();


    public abstract BasicProperties setWorkspaceId(Integer workspaceId);
}
