package top.huzz.resilix.exception;

import lombok.Getter;

/**
 * 执行单元停止异常
 *
 * @author chenji
 * @since 1.0.0
 */
@Getter
public class TaskReportStoppedException extends PhaseStoppedException {
    private final String reportId;

    public TaskReportStoppedException(String reportId) {
        this.reportId = reportId;
    }
}
