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

/**
 * An enhanced runnable interface that can be implemented
 * by tasks submitted to the Executor.
 */
public interface ExecutorAwareRunnable extends Runnable {

    /**
     * This method can be used by the executor to have a hint whether the internal thread
     * pool can be bypassed for the execution of this runnable.
     * If the Executor supports such things and the method returns <code>true</code>,
     * this task will be executed in the caller thread.
     *
     * @return <code>true</code> if the executor thread pool can be bypassed for this task
     */
    boolean shouldRunSynchronously();

}
