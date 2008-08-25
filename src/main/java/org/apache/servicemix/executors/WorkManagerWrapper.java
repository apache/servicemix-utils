/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.servicemix.executors;

import javax.resource.spi.work.ExecutionContext;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkListener;
import javax.resource.spi.work.WorkManager;

/**
 * This helper class is a simple wrapper around the Executor
 * interface to provide a WorkManager.
 * 
 * Note that the implementation is really simplistic
 * and all calls will delegate to the <code>execute</code>
 * method of the internal <code>Executor</code>.
 * 
 * @author <a href="mailto:gnodet [at] gmail.com">Guillaume Nodet</a>
 */
public class WorkManagerWrapper implements WorkManager {

    private final Executor executor;
    
    public WorkManagerWrapper(Executor executor) {
        this.executor = executor;
    }

    public void doWork(Work work) throws WorkException {
        executor.execute(work);
    }

    public void doWork(Work work, long startTimeout, ExecutionContext execContext, WorkListener workListener) throws WorkException {
        executor.execute(work);
    }

    public void scheduleWork(Work work) throws WorkException {
        executor.execute(work);
    }

    public void scheduleWork(Work work, long startTimeout, ExecutionContext execContext, WorkListener workListener) throws WorkException {
        executor.execute(work);
    }

    public long startWork(Work work) throws WorkException {
        executor.execute(work);
        return 0;
    }

    public long startWork(Work work, long startTimeout, ExecutionContext execContext, WorkListener workListener) throws WorkException {
        executor.execute(work);
        return 0;
    }
    
}
