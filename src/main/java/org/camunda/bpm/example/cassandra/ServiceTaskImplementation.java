package org.camunda.bpm.example.cassandra;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ServiceTaskImplementation implements JavaDelegate
{

  public void execute(DelegateExecution execution) throws Exception
  {

    for (String variableName : execution.getVariableNames())
    {
      System.out.println(execution.getVariable(variableName));
    }

  }

}
