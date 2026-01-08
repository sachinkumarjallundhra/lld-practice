package lld.machinecoding.hitcounter.multithreaded;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class Solution {
    private AtomicIntegerArray visitCounts;

    public Solution(){}

    public void init(int totalPages){
        this.visitCounts = new AtomicIntegerArray(totalPages);
        // helper.println("restaurant rating module initialized");
    }

    // increment visit count for pageIndex by 1
    public void incrementVisitCount(int pageIndex) {
        visitCounts.incrementAndGet(pageIndex);

    }

    // return total visit count for a given page
    public int getVisitCount(int pageIndex) {
        return visitCounts.get(pageIndex);
    }
}

// uncomment below code in case you are using your local ide like intellij, eclipse etc and
// comment it back again back when you are pasting completed solution in the online CodeZym editor.
// if you don't comment it back, you will get "java.lang.AssertionError: java.lang.LinkageError"
// This will help avoid unwanted compilation errors and get method autocomplete in your local code editor.
/**
 interface Q06WebpageVisitCounterInterface {
 void init(int totalPages, Helper06 helper);
 void incrementVisitCount(int pageIndex);
 int getVisitCount(int pageIndex);
 }

 class Helper06 {
 void print(String s){System.out.print(s);}
 void println(String s){System.out.println(s);}
 }
 */