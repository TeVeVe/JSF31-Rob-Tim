/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movingballsfx;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Rob
 */
public class RW {

    private int readersActive;
    private int writersActive;
    private int readersWaiting;
    private Lock monLock;
    private Condition okToRead;
    private Condition okToWrite;

    public RW() {
        monLock = new ReentrantLock();
        readersWaiting = 0;
        writersActive = 0;
        readersActive = 0;
    }

    public void enterReader() throws InterruptedException {
        monLock.lock();
        try {
            while (writersActive != 0) {
                readersWaiting++;
                okToRead.await();
                readersWaiting--;
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
        } finally {
            monLock.unlock();
        }
    }

    public void exitWriter() {
        monLock.lock();
        try {
            writersActive--;
            if (readersWaiting > 0) {
                okToRead.signal();
            } else {
                okToWrite.signal();
            }
        } finally {
            monLock.unlock();
        }
    }

}
