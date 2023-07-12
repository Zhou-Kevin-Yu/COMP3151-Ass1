package ass1_java;

public abstract class ConcurrentSetOperator implements Runnable {
  protected ConcurrentSet array;
  
  public ConcurrentSetOperator(ConcurrentSet array) {
    this.array = array;
  }
}
