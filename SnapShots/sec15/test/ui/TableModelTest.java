package test.ui;

import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import org.hamcrest.Matcher;
import org.hamcrest.beans.SamePropertyValuesAs;

import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.argThat;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import main.State;
import main.Snapshot;
import main.ui.TableModel;
import main.ui.Column;


@SuppressWarnings("unchecked") @RunWith(MockitoJUnitRunner.class) public class TableModelTest {
  @Mock TableModelListener listener;
  @InjectMocks TableModel model;
  @Before public void addListener(){ model = new TableModel(); model.addTableModelListener(listener);}
  @Test public void カラムも数は４つだよ(){ assertThat(model.getColumnCount(), equalTo(Column.values().length));}
  @Test public void 入札の詳細を表示するよ() throws Exception {
    model.stateChanged( new Snapshot("item id", 555, 666, State.BIDDING));
    verify(listener).tableChanged(argThat(is(aRowChangedEvent())));
    assertColumnEwurals(Column.ITEM_IDENTIFER, "item id");
    assertColumnEwurals(Column.LAST_PRICE, 555);
    assertColumnEwurals(Column.LAST_BID, 666);
    assertColumnEwurals(Column.STATE, "入札参加中");
  }
  @Test public void カラムヘッダはどうかな(){
    for (Column c: Column.values()){ assertEquals(c.name, model.getColumnName(c.ordinal()));}
  }
  private void assertColumnEwurals(Column column, Object exp){
    final int row = 0, col = column.ordinal();
    assertEquals(exp, model.getValueAt(row, col));
  }
  private Matcher<TableModelEvent> aRowChangedEvent(){
    return new SamePropertyValuesAs(new TableModelEvent(model, 0));
  }
}