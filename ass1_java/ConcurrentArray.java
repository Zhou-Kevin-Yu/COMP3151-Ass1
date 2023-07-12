package ass1_java;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * A concurrent array data structure that supports concurrent searches, insertions, deletes, and sorted printing.
 * 
 * It is 
 */
public class ConcurrentArray {

  private Integer N;
  private List<Integer> arr;
  private Semaphore capacity;
  private List<Semaphore> left;
  private List<Semaphore> right;

  public ConcurrentArray(Integer N) {
    this.N = N;
    arr = new ArrayList<>(N);
    left = new ArrayList<Semaphore>(N);
    right = new ArrayList<Semaphore>(N);
  }

  private void shiftLeft(Integer index) {
    arr.set(index - 1, arr.get(index));
    arr.set(index, -1);
  }

  private void shiftRight(Integer index) {
    arr.set(index + 1, arr.get(index));
    arr.set(index, -1);
  }

  private void insertIndex(Integer index, Integer value) {
    arr.set(index, value);
  }

  private boolean deleteIndex(Integer index) {

    Integer value = arr.get(index);

    if (value == -1) {
      return false;
    }

    arr.set(-1, index);
    return true;
  }

  /**
   * Implements binary search on the array to search for a number 
   * @param x - the number to search for
   * @return true if the number is found, false otherwise
   */
  public boolean search(Integer x) {
    return false;
  }

  /**
   * Inserts a number into the array
   * @param x - the number to insert
   * @return true if the number is inserted, false otherwise
   */
  public boolean insert(Integer x) {

    capacity.acquireUninterruptibly();

    Integer L = -1;
    Integer R = -1;

    while (true) {
      // get R + 1

      Integer nextValue = arr.get(R + 1);

      if (nextValue == -1) {
        
        // A new empty slot is found, release the mutex on L and update it to R
        L = R;

      } else if (nextValue < x) {

        // The insertion point must further to the right
        R = R + 1;

      } else if (nextValue == x) {

        // The number is already in the array, no insertion occurs
        capacity.release();
        return false;

      } else if (nextValue > x) {
        
        // The insertion point is found, insert the number between R and R + 1

        if (L != -1) {
          
          // We have an empty slot reserved at L
          // shift all the numbers between L + 1 and R to the left in ascending order
          for (int i = L + 1; i <= R; i++) {
            shiftLeft(i);
          }

          // insert the number at R
          arr.set(R, x);

        } else {

          // We don't have an empty slot reserved, search to the right for an empty slot
          Integer k = 2;

          while (true) {
            Integer nextnextValue = arr.get(R + k);

            if (nextnextValue == -1) {

              // An empty slot is found at R + k
              // shift all the numbers between R + 1 and R + k - 1 to the right in descending order
              for (int i = R + k - 1; i >= R + 1; i--) {
                shiftRight(i);
              }

              // insert the number at R + 1
              arr.set(R + 1, x);
            } 
          }
        }
      }
    }
  }

  /**
   * Deletes a number from the array
   * @param x - the number to delete
   * @return true if the number is deleted, false otherwise
   */
  public boolean delete(Integer x) {
    return false;
  }

  /**
   * Prints the array in sorted order
   * @return true
   */
  public void print_sorted() {

    List<Integer> sorted = new ArrayList<Integer>();

    for (int i = 0; i < N; i++) {
      Integer nextValue = arr.get(i);
      if (nextValue != -1) {
        sorted.add(nextValue);
      }
    }

    System.out.println(sorted);

  }
}
