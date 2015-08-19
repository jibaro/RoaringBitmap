package org.roaringbitmap.needwork;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.roaringbitmap.RoaringBitmap;
import org.roaringbitmap.ZipRealDataRetriever;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class SlowIterate {

    @Benchmark
    public int iterate_RoaringWithRun(BenchmarkState benchmarkState) {
        int total = 0;
        for (int k = 0; k < benchmarkState.rc.size(); ++k) {
            RoaringBitmap rb = benchmarkState.rc.get(k);
            org.roaringbitmap.IntIterator i = rb.getIntIterator();
            while(i.hasNext())
                total += i.next();
        }
        return total;
    }
    
    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param ({// putting the data sets in alpha. order
             "census-income_srt",
        })
        String dataset;


        ArrayList<RoaringBitmap> rc = new ArrayList<RoaringBitmap>();

        public BenchmarkState() {
        }
                
        @Setup
        public void setup() throws Exception {
            ZipRealDataRetriever dataRetriever = new ZipRealDataRetriever(dataset);
            System.out.println();
            System.out.println("Loading files from " + dataRetriever.getName());

            for (int[] data : dataRetriever.fetchBitPositions()) {
                RoaringBitmap basic = RoaringBitmap.bitmapOf(data);
                basic.runOptimize();
                rc.add(basic);
            }
            System.out.println("loaded "+rc.size()+" bitmaps");
        }

    }
}