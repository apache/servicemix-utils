package org.apache.servicemix.store.memory;

import org.apache.servicemix.executors.Executor;
import org.apache.servicemix.executors.ExecutorFactory;
import org.apache.servicemix.executors.impl.ExecutorFactoryImpl;
import org.apache.servicemix.id.IdGenerator;
import org.apache.servicemix.store.Store;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test case to ensure the {@link TimeoutMemoryStore} behaves properly under multi-threaded l
 */
public class ConcurrentTimeoutMemoryStoreTest {

    private static final int NUMBER_OF_EXECUTORS = 5;
    private static final int NUMBER_OF_OPERATIONS = 1000;
    private static final Random RANDOM = new Random();
    private static final int RANDOMNESS = 5;
    
    private final ExecutorFactory factory = new ExecutorFactoryImpl();
    private final Store store = new TimeoutMemoryStore(new IdGenerator(), 100);
    
    @Test
    public void testConcurrentLoadAndStore() throws Exception {
        final CountDownLatch latch = new CountDownLatch(NUMBER_OF_EXECUTORS * NUMBER_OF_OPERATIONS);
        
        final Executor executor = factory.createExecutor("concurrent.timemout.memory.store");
        for (int i = 0 ; i < 5 ; i++) {
            executor.execute(new RandomAccessExecutable(store, latch));
        }
        
        assertTrue("We should have processed all operations successfully", latch.await(3, TimeUnit.SECONDS));
    }

    /**
     * {@link Runnable} that randomly stores and removes items from a {@link Store} implementation
     */
    private class RandomAccessExecutable implements Runnable {

        private final Store store;
        private final CountDownLatch latch;

        public RandomAccessExecutable(Store store, CountDownLatch latch) {
            super();
            this.store = store;
            this.latch = latch;
        }

        public void run() {
            for (int i = 0 ; i < NUMBER_OF_OPERATIONS ; i++) {
                try {
                    store.store("Item " + RANDOM.nextInt(RANDOMNESS));
                    store.load("Item " + RANDOM.nextInt(RANDOMNESS));
                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("Unexception exception caught: " + e);
                }
            }
        }
    }
}
