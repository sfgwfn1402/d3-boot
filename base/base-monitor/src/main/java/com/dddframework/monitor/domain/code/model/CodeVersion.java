package com.dddframework.monitor.domain.code.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CodeVersion {
    private String branch;
    private String buildTime;
    private String buildVersion;
    private String commitId;
    private String commitMessage;
    private String commitUser;
    private String commitTime;
}
