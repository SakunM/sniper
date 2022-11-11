package main.ui;

import main.Snapshot;

public interface ForGUI {
  void addListener(SniperListener listener);
  Snapshot getSnapshot();
}