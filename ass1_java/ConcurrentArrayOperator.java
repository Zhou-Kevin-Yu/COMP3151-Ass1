package ass1_java;

public abstract class ConcurrentArrayOperator implements Runnable {
  private ConcurrentArray array;
  
  public ConcurrentArrayOperator(ConcurrentArray array) {
    this.array = array;
  }
}
