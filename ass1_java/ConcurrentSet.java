package ass1_java;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A concurrent array data structure that supports concurrent searches, insertions, deletes, and sorted printing.
 * 
 * It is 
 */
public class ConcurrentSet {

  private Integer N;
  private List<Integer> arr;
  private Semaphore capacity;
  private List<ReadWriteLock> locks;
  private List<Semaphore> insertion_mutex;

  public ConcurrentSet(Integer N) {
    this.N = N;
    arr = new ArrayList<>(N);
    capacity = new Semaphore(N, true);
    locks = new ArrayList<>(N);
    insertion_mutex = new ArrayList<>(N);

    for (int i = 0; i < N; i++) {
      arr.add(-1);
      locks.add(new ReentrantReadWriteLock(true));
      insertion_mutex.add(new Semaphore(1, true));
    }
  }

  private void shiftLeft(Integer index) {

    locks.get(index).writeLock().lock();
    locks.get(index - 1).writeLock().lock();

    arr.set(index - 1, arr.get(index));
    arr.set(index, -1);

    locks.get(index).writeLock().unlock();
    locks.get(index - 1).writeLock().unlock();
  }

  private void shiftRight(Integer index) {

    locks.get(index).writeLock().lock();
    locks.get(index + 1).writeLock().lock();

    arr.set(index + 1, arr.get(index));
    arr.set(index, -1);

    locks.get(index).writeLock().unlock();
    locks.get(index + 1).writeLock().unlock();
  }

  private void insertIndex(Integer index, Integer value) {

    locks.get(index).writeLock().lock();

    arr.set(index, value);

    locks.get(index).writeLock().unlock();
  }

  private boolean deleteIndex(Integer index) {

    locks.get(index).writeLock().lock();

    boolean successfulDeletion = arr.get(index) != -1;
    arr.set(index, -1);

    locks.get(index).writeLock().unlock();

    return successfulDeletion;
  }

  private Integer readIndex(Integer index) {

    locks.get(index).readLock().lock();

    Integer value = arr.get(index);

    locks.get(index).readLock().unlock();

    return value;
  }

  /**
   * Implements binary search on the array to search for a number 
   * @param x - the number to search for
   * @return true if the number is found, false otherwise
   */
  public boolean search(Integer x) {

    // Perform the initial setup for the binary search

    Integer L = 0;
    Integer R = N - 1;

    locks.get(L).readLock().lock();
    locks.get(R).readLock().lock();

    if (arr.get(L) == x || arr.get(R) == x) {
      locks.get(L).readLock().unlock();
      locks.get(R).readLock().unlock();
      return true;
    }

    while (true) {

      // lock the middle element
      Integer MGuessLeft = (L + R) / 2;
      Integer MGuessRight = MGuessLeft + 1;
      Integer M;

      locks.get(MGuessLeft).readLock().lock();
      locks.get(MGuessRight).readLock().lock();

      if (arr.get(MGuessLeft) != -1) {
        M = MGuessLeft;
      } else if (arr.get(MGuessRight) != -1) {
        M = MGuessRight;
      } else {
        // search both directions until the number is found or the search space is exhausted
        while (true) {

          boolean canProgressLeft = MGuessLeft > L + 1;
          boolean canProgressRight = MGuessRight < R - 1;

          if (!canProgressLeft && !canProgressRight) {
            // the number is not in [L, R]
            locks.get(MGuessLeft).readLock().unlock();
            locks.get(MGuessRight).readLock().unlock();

            locks.get(L).readLock().unlock();
            locks.get(R).readLock().unlock();
            return false;
          }

          if (canProgressLeft) {
            locks.get(MGuessLeft - 1).readLock().lock();
            locks.get(MGuessLeft).readLock().unlock();
            MGuessLeft--;

            if (arr.get(MGuessLeft) != -1) {
              M = MGuessLeft;
              break;
            }
          }

          if (canProgressRight) {
            locks.get(MGuessRight).readLock().lock();
            locks.get(MGuessRight + 1).readLock().unlock();
            MGuessRight++;

            if (arr.get(MGuessRight) != -1) {
              M = MGuessRight;
              break;
            }
          }
        }
      }

      // We have identified a non-empty element at position M
      Integer MValue = arr.get(M);

      assert(MValue != -1);

      if (MValue == x) {
        // The number is found
        locks.get(M).readLock().unlock();
        locks.get(L).readLock().unlock();
        locks.get(R).readLock().unlock();
        return true;
      } else if (MValue < x) {
        // The number must be further to the right
        locks.get(L).readLock().unlock();
        L = M;
      } else {
        // The number must be further to the left
        locks.get(R).readLock().unlock();
        R = M;
      }
    }
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

      Integer nextValue;

      if (R == N - 1) {
        assert(L != -1);
        nextValue = Integer.MAX_VALUE; // forces left insertion
      } else {
        insertion_mutex.get(R + 1).acquireUninterruptibly();
        nextValue = arr.get(R + 1);
      }

      if (R != -1 && R != L) {
        insertion_mutex.get(R).release();
      }

      if (nextValue == -1) {

        // A new empty slot is found, release the mutexes and update it to the new space
        if (L != -1) {
          insertion_mutex.get(L).release();
        }
        
        R = R + 1;
        L = R;

      } else if (nextValue < x) {

        // The insertion point must further to the right
        R = R + 1;

      } else if (nextValue == x) {

        // The number is already in the array, no insertion occurs
        capacity.release();

        // release the mutexes
        insertion_mutex.get(R + 1).release();
        if (L != -1) {
          insertion_mutex.get(L).release();
        }

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
          insertIndex(R, x);

          // release the mutexes
          if (R != N - 1) {
            insertion_mutex.get(R + 1).release();
          }
          insertion_mutex.get(L).release();

          return true;

        } else {

          // We don't have an empty slot reserved, search to the right for an empty slot
          Integer k = 2;

          while (true) {

            assert(R + k < N);
            insertion_mutex.get(R + k).acquireUninterruptibly();
            if (k != 2) {
              insertion_mutex.get(R + k - 1).release();
            }
            
            if (readIndex(R + k) == -1) {

              // An empty slot is found at R + k
              // shift all the numbers between R + 1 and R + k - 1 to the right in descending order
              for (int i = R + k - 1; i >= R + 1; i--) {
                shiftRight(i);
              }

              // insert the number at R + 1
              insertIndex(R + 1, x);

              // release the mutexes

              insertion_mutex.get(R + k).release();
              insertion_mutex.get(R + 1).release();

              return true;
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
  public void printSorted() {

    List<Integer> sorted = new ArrayList<Integer>();

    for (int i = 0; i < N; i++) {

      // acquire the next lock, then release the previous lock
      locks.get(i).readLock().lock();
      if (i != 0) {
        locks.get(i - 1).readLock().unlock();
      }

      // read the next value and add it to the sorted list
      Integer nextValue = arr.get(i);

      if (nextValue != -1) {
        sorted.add(nextValue);
      }

    }

    // release the last lock
    locks.get(N - 1).readLock().unlock();

    System.out.println(sorted);

  }
}
