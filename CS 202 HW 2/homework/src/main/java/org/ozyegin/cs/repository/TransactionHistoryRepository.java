package org.ozyegin.cs.repository;

import java.sql.Date;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import org.ozyegin.cs.entity.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionHistoryRepository extends JdbcDaoSupport {
  private final RowMapper<Pair> pairMapper = (resultSet, i) -> new Pair(
      resultSet.getString(1),
      resultSet.getInt(2)
  );

  private final RowMapper<String> stringMapper = (resultSet, i) -> resultSet.getString(1);

  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  public List<Pair> query1() {

    List <Pair> pairList= Objects.requireNonNull(getJdbcTemplate().query("SELECT T.cname,T.pid FROM Transaction_History T GROUP BY T.cname,T.pid HAVING COUNT(*) >= ALL ( SELECT COUNT(*) FROM Transaction_History T1 WHERE T.cname=T1.cname AND T1.pid<>T.pid )",pairMapper));
    return pairList;
  }

  public List<String> query2(Date start, Date end) {
    List <String> nameList= Objects.requireNonNull(getJdbcTemplate().query("SELECT C.name FROM Company C WHERE NOT EXISTS ( SELECT T1.cname FROM Transaction_History T1 WHERE T1.cname=C.name AND T1.orderDate BETWEEN ? AND ?)",new Object[]{start , end},stringMapper));
    /*SELECT T.cname FROM Transaction_History T WHERE NOT EXISTS ( SELECT * WHERE T1.cname=T.cname AND T1.Date BETWEEN ? AND ?)*/

    return nameList;
  }

  public void deleteAll() {
    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM Transaction_History");
  }
}
