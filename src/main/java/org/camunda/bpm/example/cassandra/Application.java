package org.camunda.bpm.example.cassandra;

import static org.camunda.bpm.engine.variable.Variables.createVariables;

import java.util.List;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.cassandra.cfg.CassandraProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.engine.runtime.ProcessInstance;

public class Application
{

  public static void main(String[] args)
  {

    final CassandraProcessEngineConfiguration processEngineConfiguration = new CassandraProcessEngineConfiguration();
    processEngineConfiguration.setCassandraContactPoint("127.0.0.1");
    processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
    processEngineConfiguration.setMetricsEnabled(false);

    processEngineConfiguration.setHistoryEventHandler(new HistoryEventHandler()
    {

      public void handleEvents(List<HistoryEvent> historyEvents)
      {
        for (HistoryEvent historyEvent : historyEvents)
        {
          handleEvent(historyEvent);
        }
      }

      public void handleEvent(HistoryEvent historyEvent)
      {
        System.out.println(historyEvent);
      }

    });

    final ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
    final RepositoryService repositoryService = processEngine.getRepositoryService();
    final RuntimeService runtimeService = processEngine.getRuntimeService();

    repositoryService
        .createDeployment()
        .enableDuplicateFiltering(true)
        .addClasspathResource("example1.bpmn")
        .deploy();

    final ProcessInstance processInstance = runtimeService
        .startProcessInstanceByKey("example1",
            createVariables().putValue("variableName", "variableValue"));

    runtimeService.createMessageCorrelation("hello")
      .processInstanceId(processInstance.getId())
      .correlate();

    runtimeService.createMessageCorrelation("goodbye")
      .processInstanceId(processInstance.getId())
      .correlate();

    processEngine.close();
  }

}
