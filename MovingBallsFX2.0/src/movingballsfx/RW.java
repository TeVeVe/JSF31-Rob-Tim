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

    private int readersActive,writersActive, readersWaiting, writersWaiting;
    private Lock monLock;
    private Condition okToRead;
    private Condition okToWrite;

    public RW() {
        monLock = new ReentrantLock();
        okToRead = monLock.newCondition();
        okToWrite = monLock.newCondition();
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
                writersWaiting++;
                okToWrite.await();
                writersWaiting--;
            }

            writersActive++;
        } finally {
            monLock.unlock();
        }
    }

    public void exitWriter() {
        monLock.lock();

        try {
            writersActive--;
            if (writersWaiting > 0 && writersActive == 0) {
                okToWrite.signal();
            } else {
                okToRead.signalAll();
            }
        } finally {
            monLock.unlock();
        }
    }

//    public void exitInterruptedWriter() {
//        monLock.lock();
//        System.out.println("Begin EW: " + writersWaiting + " - " + writersActive);
//        try {
//            writersActive--;
//            if (writersWaiting > 0 && writersActive == 0) {
//                okToWrite.signal();
//            } else {
//                okToRead.signalAll();
//            }
//        }
//        finally {
//            System.out.println("After EW: " + writersWaiting + " - " + writersActive);
//            monLock.unlock();
//        }
//    }

}
