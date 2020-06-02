package com.asarao.service;

import java.io.InputStream;

public interface ActivityService {

    InputStream tracePhoto(String processDefinitionId, String executionId);
}
