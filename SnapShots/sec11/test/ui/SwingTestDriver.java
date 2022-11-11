package test.ui;

import static org.hamcrest.Matchers.equalTo;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;
import com.objogate.wl.swing.matcher.IterableComponentsMatcher;

@SuppressWarnings("unchecked") public class SwingTestDriver extends JFrameDriver {
  public SwingTestDriver(final int timeout){
    super( new GesturePerformer(),
      JFrameDriver.topLevelFrame(named("MainWindow"), showingOnScreen()), 
      new AWTEventQueueProber(timeout, 100));
  }
  public void 状態(String status){ new JLabelDriver(this, named("label")).hasText(equalTo(status));}
}