package ass1_java;

public class Deleter extends ConcurrentArrayOperator implements Runnable {

  private Integer x;

  public Deleter(ConcurrentArray array, Integer x) {
    super(array)
    this.x = x;
  }

  @Override
  public void run() {
    System.out.println("Deletion: " + x);
    boolean result = array.delete(x);
    System.out.println("Deletion: " + x + (result ? " found" : " not found"));
  }
}
