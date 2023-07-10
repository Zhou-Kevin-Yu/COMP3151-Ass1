package ass1_java;

public class Reader extends ConcurrentArrayOperator implements Runnable {

  private Integer x;

  public Reader(ConcurrentArray array, Integer x) {
    super(array);
    this.x = x;
  }

  @Override
  public void run() {
    System.out.println("Search: " + x);
    boolean result = array.insert(x);
    System.out.println("Search: " + x + (result ? " found" : " not found"));
  }
  
}
