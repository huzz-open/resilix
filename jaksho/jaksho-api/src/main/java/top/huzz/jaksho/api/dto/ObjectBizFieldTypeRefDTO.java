package top.huzz.jaksho.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @author huzz
 * @since 1.0.2
 */
@Getter
@Setter
public class ObjectBizFieldTypeRefDTO {
    @NotNull
    private Integer bizFieldTypeId;
    @NotNull
    private Integer refId;
    @NotNull
    private Integer parentId;
    @NotNull
    private Integer sortOrder;
}
