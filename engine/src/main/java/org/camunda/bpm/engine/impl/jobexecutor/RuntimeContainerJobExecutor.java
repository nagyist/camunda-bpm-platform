/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.impl.jobexecutor;

import java.util.List;

import org.camunda.bpm.container.ExecutorService;
import org.camunda.bpm.container.RuntimeContainerDelegate;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.ProcessEngineImpl;

/**
 * <p>JobExecutor implementation that delegates the execution of jobs 
 * to the {@link RuntimeContainerDelegate RuntimeContainer}</p>
 * 
 * @author Daniel Meyer
 *
 */
public class RuntimeContainerJobExecutor extends JobExecutor {
  
  protected void startExecutingJobs() {
    
    final RuntimeContainerDelegate runtimeContainerDelegate = getRuntimeContainerDelegate();
    
    // schedule job acquisition
    if(!runtimeContainerDelegate.getExecutorService().schedule(acquireJobsRunnable, true)) {
      throw new ProcessEngineException("Could not schedule AcquireJobsRunnable for execution.");
    }
   
  }

  protected void stopExecutingJobs() {
    // nothing to do
  }

  public void executeJobs(List<String> jobIds, ProcessEngineImpl processEngine) {
    
    final RuntimeContainerDelegate runtimeContainerDelegate = getRuntimeContainerDelegate();
    final ExecutorService executorService = runtimeContainerDelegate.getExecutorService();
    
    Runnable executeJobsRunnable = executorService.getExecuteJobsRunnable(jobIds, processEngine);
    
    // delegate job execution to runtime container
    if(!executorService.schedule(executeJobsRunnable, false)) {
      
      // TODO: if the execution is rejected by the runtime container, execute the rejected jobs handler

      // run in caller thread
      executeJobsRunnable.run();
    }
           
  }

  protected RuntimeContainerDelegate getRuntimeContainerDelegate() {
    return RuntimeContainerDelegate.INSTANCE.get();
  }
  
}
