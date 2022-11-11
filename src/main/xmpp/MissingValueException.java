package main.xmpp;

public class MissingValueException extends RuntimeException {
  private static final long serialVersionUID = 3L;
  public MissingValueException(final String name){
    super("Missing value for field: " + name);
  }
}