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
import static org.mockito.Mockito.any;
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

  @Test public void スナイパーが増えたとき報告するよ(){
    Snapshot joining = Snapshot.joining("item123");
    assertEquals(0, model.getRowCount());
    model.addSniper(joining);
    verify(listener).tableChanged(any());
    assertEquals(1, model.getRowCount());
    assertRowMatchesSnapshot(0, joining);
  }
  @Test public void カラムも数は４つだよ(){ assertThat(model.getColumnCount(), equalTo(Column.values().length));}
  @Test public void 入札の詳細を表示するよ() throws Exception {
    Snapshot joining = Snapshot.joining("item id");
    Snapshot bidding = joining.bidding(555, 666);
    model.addSniper(joining); model.stateChanged(bidding);
    verify(listener).tableChanged(argThat(is(aRowChangeInRow(0))));
    assertRowMatchesSnapshot(0,bidding);
  }
  @Test public void カラムヘッダはどうかな(){
    for (Column c: Column.values()){ assertEquals(c.name, model.getColumnName(c.ordinal()));}
  }
  private void assertColumnEquals(Column column, Object exp){
    final int row = 0, col = column.ordinal();
    assertEquals(exp, model.getValueAt(row, col));
  }
  private Matcher<TableModelEvent> aRowChangeInRow(final int row){
    return new SamePropertyValuesAs<TableModelEvent>( new TableModelEvent(model, row));
  }
  private Matcher<TableModelEvent> aRowChangedEvent(){
    return new SamePropertyValuesAs(new TableModelEvent(model, 0));
  }
  private void assertRowMatchesSnapshot(final int row, final Snapshot ss){
    for(final Column col: Column.values()){
      assertThat(model.getValueAt(row, col.ordinal()), is(col.valueIn(ss)));
    }
  }
}