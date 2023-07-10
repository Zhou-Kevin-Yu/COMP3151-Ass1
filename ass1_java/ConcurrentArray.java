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

  private List<Integer> array;
  private List<Semaphore> left;
  private List<Semaphore> right;

  public ConcurrentArray(Integer N) {
    array = new ArrayList<>(N);
    left = new ArrayList<Semaphore>(N);
    right = new ArrayList<Semaphore>(N);
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
    return false;
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

    System.out.println(sorted);

    return;
  }
}
