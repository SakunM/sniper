package test.ui;

import javax.swing.table.JTableHeader;
import javax.swing.JButton;
import javax.swing.JTextField;

import static org.hamcrest.Matchers.equalTo;

import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.driver.JButtonDriver;
import com.objogate.wl.swing.driver.JTextFieldDriver;
import com.objogate.wl.swing.driver.JTableHeaderDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;
import com.objogate.wl.swing.matcher.IterableComponentsMatcher;

@SuppressWarnings("unchecked") public class SwingTestDriver extends JFrameDriver {
  public SwingTestDriver(final int timeout){
    super( new GesturePerformer(),
      JFrameDriver.topLevelFrame(named("MainWindow"), showingOnScreen()), 
      new AWTEventQueueProber(timeout, 100));
  }
  public void 状態(String status){ new JTableDriver(this).hasCell(withLabelText(equalTo(status)));}
  public void 状態(String itemId, int lastPrice, int lastBid, String status){
    final JTableDriver table = new JTableDriver(this);
    table.hasRow(matching(
      withLabelText(itemId), withLabelText(String.valueOf(lastPrice)),
      withLabelText(String.valueOf(lastBid)), withLabelText(status)
    ));
  }
  public void カラムのタイトルもチェックするよ(){
    JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
    headers.hasHeaders(
      matching( withLabelText("商品名"), withLabelText("最新価格"), withLabelText("当社入札額"), withLabelText("入札状態"))
    );
  }
  private JTextFieldDriver textField(String name){
    JTextFieldDriver field = new JTextFieldDriver(this, JTextField.class, named(name));
    field.focusWithMouse(); return field;
  }
  private JButtonDriver bidButton(){ return new JButtonDriver(this, JButton.class, named("button"));}
  public void 入札開始(String itemId, int stopPrice){
    textField("text").replaceAllText(itemId); 
    textField("price").replaceAllText(String.valueOf(stopPrice)); 
    bidButton().click();
  }
}