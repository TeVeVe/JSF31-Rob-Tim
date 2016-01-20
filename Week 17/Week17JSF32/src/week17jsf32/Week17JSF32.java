/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package week17jsf32;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import timeutil.TimeStamp;

/**
 *
 * @author Rob
 */
public class Week17JSF32 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        ILibrary lib = (ILibrary) Native.loadLibrary("Kernel32", ILibrary.class);
        ILibrary.SYSTEMTIME time = new ILibrary.SYSTEMTIME();
        TimeStamp ts = new TimeStamp();
        TimeStamp ts2 = new TimeStamp();
        
        ts.setBegin();
        lib.GetSystemTime(time);
        ts.setEnd();
        System.out.println(ts.toString());
        
        ts2.setBegin();
        System.nanoTime();
        ts2.setEnd();
        System.out.println(ts2.toString());
        

        IntByReference IntR1 = new IntByReference(), 
        IntR2 = new IntByReference(), 
        IntR3 = new IntByReference(), 
        IntR4 = new IntByReference();
        
        if (lib.GetDiskFreeSpaceA("C:\\", IntR1, IntR2, IntR3, IntR4)) {
            long multiplier = IntR1.getValue() * IntR2.getValue();
            long free = (IntR3.getValue() * multiplier) / 1073741824;
            long total = (IntR4.getValue() * multiplier) / 1073741824;
            System.out.println("'C:\\': free: " + free + ", total: " + total);
        } else {
            System.out.println("GetDiskFreeSpaceEx() returned false");
        }
        
        char[] charss = {'C', ':', '\\'};
        if (lib.GetDiskFreeSpaceW(charss, IntR1, IntR2, IntR3, IntR4)) {
            long multiplier = IntR1.getValue() * IntR2.getValue();
            long free = (IntR3.getValue() * multiplier) / 1073741824;
            long total = (IntR4.getValue() * multiplier) / 1073741824;
            System.out.println("'C:\\': free: " + free + ", total: " + total);
        } else {
            System.out.println("GetDiskFreeSpaceEx() returned false");
        }

    }
}
