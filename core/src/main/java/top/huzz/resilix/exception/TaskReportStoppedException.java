package top.huzz.resilix.exception;

import lombok.Getter;

/**
 * Task execution unit stopped exception
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
