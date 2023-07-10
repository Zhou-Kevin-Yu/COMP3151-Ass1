package ass1_java;

import ass1_java.ConcurrentArray;
import ass1_java.ConcurrentArrayOperator;

public class SortedPrinter extends ConcurrentArrayOperator implements Runnable {

  public SortedPrinter(ConcurrentArray array) {
    super(array);
  }

  @Override
  public void run() {
    System.out.println("Printing Sorted Array");
    System.out.println("Sorted Array: " + array.print_sorted());
  }
  
}
