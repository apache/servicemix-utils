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
package org.apache.servicemix.timers.impl;

import java.util.Date;
import java.util.TimerTask;

import org.apache.servicemix.timers.Timer;
import org.apache.servicemix.timers.TimerListener;
import org.apache.servicemix.timers.TimerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerManagerImpl implements TimerManager {

    private static final Logger LOG = LoggerFactory.getLogger(TimerManagerImpl.class);

    private java.util.Timer timer;

    public synchronized Timer schedule(TimerListener listener, long delay) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Schedule timer " + listener + " for " + delay);
        }
        TimerImpl tt = new TimerImpl(listener);
        if (timer == null) {
            timer = new java.util.Timer();
        }
        timer.schedule(tt, delay);
        return tt;
    }

    public synchronized Timer schedule(TimerListener listener, Date date) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Schedule timer " + listener + " at " + date);
        }
        TimerImpl tt = new TimerImpl(listener);
        if (timer == null) {
            timer = new java.util.Timer();
        }
        timer.schedule(tt, date);
        return tt;
    }

    public void start() {
        // for later usage
    }

    public synchronized void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    protected static class TimerImpl extends TimerTask implements Timer {

        private TimerListener timerListener;

        public TimerImpl(TimerListener timerListener) {
            this.timerListener = timerListener;
        }

        public boolean cancel() {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Timer " + timerListener + " cancelled");
            }
            return super.cancel();
        }

        public TimerListener getTimerListener() {
            return this.timerListener;
        }

        public void run() {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Timer " + timerListener + " expired");
            }
            this.timerListener.timerExpired(this);
        }

    }

}
