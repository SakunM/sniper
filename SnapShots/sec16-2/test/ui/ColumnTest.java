package test.ui;

import static org.junit.Assert.assertThat;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

import main.ui.Column;
import main.Snapshot;
import main.State;

public class ColumnTest {
  @Test public void 返り値をチェック () {
    assertThat(Column.at(0), is(Column.ITEM_IDENTIFER));
    assertThat(Column.at(1), is(Column.LAST_PRICE));
    assertThat(Column.at(2), is(Column.LAST_BID));
    assertThat(Column.at(3), is(Column.STATE));
  }
  @Test public void バリューインもチェックだ(){
    Snapshot ss = Snapshot.joining("item id");
    assertThat((String)Column.at(0).valueIn(ss), is("item id"));
    assertThat((int)Column.at(1).valueIn(ss), is(0));
    assertThat((int)Column.at(2).valueIn(ss), is(0));
    assertThat((String)Column.at(3).valueIn(ss), is("参加要請中"));
  }
}