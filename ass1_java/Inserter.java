package ass1_java;

public class Inserter extends ConcurrentArrayOperator implements Runnable {

  private Integer x;

  public Inserter(ConcurrentArray array, Integer x) {
    super(array);
    this.x = x;
  }

  @Override
  public void run() {
    System.out.println("Insertion: " + x);
    boolean result = array.insert(x);
    System.out.println("Insertion: " + x + (result ? " inserted" : " not inserted"));
  }
}
