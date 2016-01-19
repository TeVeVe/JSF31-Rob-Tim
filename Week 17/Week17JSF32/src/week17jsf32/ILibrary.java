/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package week17jsf32;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME;
import com.sun.jna.ptr.IntByReference;
import java.util.Arrays;
import java.util.List;

/**

 *
 */
public interface ILibrary extends Library {
    
    public static class SYSTEMTIME extends Structure
    {
        public short wYear;
        public short wMonth;
        public short wDayOfWeek;
        public short wDay;
        public short wHour;
        public short wMinute;
        public short wSecond;
        public short wMilliseconds;

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"wYear","wMonth", "wDayOfWeek","wDay", "wHour", "wMinute","wSecond","wMilliseconds"});
        }
    }
    
    public ILibrary INSTANCE = (ILibrary)Native.loadLibrary("Kernel32",ILibrary.class);
    
    void GetSystemTime (SYSTEMTIME result);
    
    boolean GetDiskFreeSpaceA(String resultString, IntByReference IntR1, IntByReference IntR2, IntByReference IntR3, IntByReference IntR4);
    
    boolean GetDiskFreeSpaceW (char[] resultChars, IntByReference IntR1, IntByReference IntR2, IntByReference IntR3, IntByReference IntR4);
}

