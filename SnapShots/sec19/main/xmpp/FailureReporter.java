package main.xmpp;

public interface FailureReporter {
  void cannotTranslateMessage(String sniperId, String failMsg, Exception e);
}