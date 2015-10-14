/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movingballsfx;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Rob
 */
public class RW {

    private int readersActive;
    private int writersActive;
    private Lock monLock;

    public RW() {
        monLock = new ReentrantLock();
    }

    public void enterReader() throws InterruptedException {
        monLock.lock();
        try {
            while (writersActive > 0) {
                okToRead.await();
            }
            readersActive++;
        } finally {
            monLock.unlock();
        }
    }

    public void exitReader() {
        monLock.lock();
        try {
            readersActive--;
            if (readersActive == 0) {
                okToWrite.signal();
            }
        } finally {
            monLock.unlock();
        }
    }

    public void enterWriter() throws InterruptedException {
        monLock.lock();
        try {
            while (writersActive > 0 || readersActive > 0) {
                
            }
            okToWrite.await();
            writersActive++;
        }finally {
            monLock.unlock();
        }
    }

    public void exitWriter() {
    }
}
