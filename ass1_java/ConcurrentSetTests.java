package ass1_java;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ConcurrentSetTests {
  @Test
  public void TestInsert() {

    int N = 10;

    ConcurrentSet array = new ConcurrentSet(N);
    Thread[] threads = new Thread[N];
    for (int i = 0; i < N; i++) {
      threads[i] = new Thread(new Inserter(array, i));
    }
    for (int i = 0; i < N; i++) {
      threads[i].start();
    }
    for (int i = 0; i < N; i++) {
      try {
        threads[i].join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    array.printSorted();
  }

  @Test
  public void TestMember() {
    int N = 1000;

    ConcurrentSet array = new ConcurrentSet(N);

    Thread[] threads = new Thread[N];

    for (int i = 0; i < N; i += 2) {
      threads[i] = new Thread(new Inserter(array, i));
    }
    for (int i = 0; i < N; i += 2) {
      threads[i].start();
    }
    for (int i = 0; i < N; i += 2) {
      try {
        threads[i].join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    Thread sortedPrinter = new Thread(new SortedPrinter(array));
    sortedPrinter.start();
    try {
      sortedPrinter.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    for (int i = 0; i < N; i++) {
      threads[i] = new Thread(new Reader(array, i));
    }

    for (int i = 0; i < N; i++) {
      threads[i].start();
    }

    for (int i = 0; i < N; i++) {
      try {
        threads[i].join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void TestInsertAndMember() {
    int N = 1000;

    ConcurrentSet array = new ConcurrentSet(N);

    List<Thread> inserterThreads = new ArrayList<Thread>(N);
    List<Thread> memberThreads = new ArrayList<Thread>(N);

    for (int i = 0; i < N; i++) {
      inserterThreads.add(new Thread(new Inserter(array, i)));
    }

    for (int i = 0; i < N; i++) {
      memberThreads.add(new Thread(new Reader(array, i)));
    }

    // interlace the threads so they run concurrently

    // array.printSorted();

    for (int i = 0; i < N; i++) {
      inserterThreads.get(i).start();
      memberThreads.get(i).start();
    }

    for (int i = 0; i < N; i++) {
      try {
        inserterThreads.get(i).join();
        memberThreads.get(i).join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    array.printSorted();

    assert(array.printSorted().size() == N);

  }

  @Test
  public void TestInsertThenDelete() {

    int N = 1000;

    ConcurrentSet array = new ConcurrentSet(N);

    List<Thread> inserterThreads = new ArrayList<Thread>(N);
    List<Thread> deleterThreads = new ArrayList<Thread>(N);

    for (int i = 0; i < N; i++) {
      inserterThreads.add(new Thread(new Inserter(array, i)));
      deleterThreads.add(new Thread(new Deleter(array, i)));
    }

    for (int i = 0; i < N; i++) {
      inserterThreads.get(i).start();
    }

    for (int i = 0; i < N; i++) {
      try {
        inserterThreads.get(i).join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    array.printSorted();

    for (int i = 0; i < N; i++) {
      deleterThreads.get(i).start();
    }

    for (int i = 0; i < N; i++) {
      try {
        deleterThreads.get(i).join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    array.printSorted();

    assert(array.printSorted().size() == 0);

  }

  @Test
  public void TestInsertAndDelete() {
    int N = 100;

    ConcurrentSet array = new ConcurrentSet(N);

    List<Thread> inserterThreads = new ArrayList<Thread>(N);
    List<Thread> deleterThreads = new ArrayList<Thread>(N);

    for (int i = 0; i < N; i++) {
      inserterThreads.add(new Thread(new Inserter(array, i)));
    }

    for (int i = 0; i < N; i++) {
      deleterThreads.add(new Thread(new Deleter(array, i)));
    }

    // interlace the threads so they run concurrently

    // array.printSorted();

    for (int i = 0; i < N; i++) {
      inserterThreads.get(i).start();
      deleterThreads.get(i).start();
    }

    for (int i = 0; i < N; i++) {
      try {
        inserterThreads.get(i).join();
        deleterThreads.get(i).join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    array.printSorted();

  }

  @Test
  public void TestEverything() {
    int N = 100;

    ConcurrentSet array = new ConcurrentSet(N);

    List<Thread> inserterThreads = new ArrayList<Thread>(N);
    List<Thread> deleterThreads = new ArrayList<Thread>(N);
    List<Thread> deleterThreads2 = new ArrayList<Thread>(N);
    List<Thread> memberThreads = new ArrayList<Thread>(N);
    List<Thread> sortedPrinterThreads = new ArrayList<Thread>(N);

    for (int i = 0; i < N; i++) {
      inserterThreads.add(new Thread(new Inserter(array, i)));
      deleterThreads.add(new Thread(new Deleter(array, i)));
      deleterThreads2.add(new Thread(new Deleter(array, N - 1 - i)));
      memberThreads.add(new Thread(new Reader(array, i)));
      sortedPrinterThreads.add(new Thread(new SortedPrinter(array)));
    }

    for (int i = 0; i < N; i++) {
      inserterThreads.get(i).start();
      deleterThreads.get(i).start();
      deleterThreads2.get(i).start();
      memberThreads.get(i).start();
      // sortedPrinterThreads.get(i).start();
    }

    try {
      for (int i = 0; i < N; i++) { 
        inserterThreads.get(i).join();
        deleterThreads.get(i).join();
        deleterThreads2.get(i).join();
        memberThreads.get(i).join();
        // sortedPrinterThreads.get(i).join();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    array.printSorted();

  }
    
}