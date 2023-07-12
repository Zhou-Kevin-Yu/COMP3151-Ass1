package ass1_java;

import org.junit.Test;

public class ConcurrentSetTests {
  @Test
  public void TestInsert() {
    ConcurrentSet array = new ConcurrentSet(10);
    Thread[] threads = new Thread[10];
    for (int i = 0; i < 10; i++) {
      threads[i] = new Thread(new Inserter(array, i));
    }
    for (int i = 0; i < 10; i++) {
      threads[i].start();
    }
    for (int i = 0; i < 10; i++) {
      try {
        threads[i].join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    array.printSorted();
  }
}