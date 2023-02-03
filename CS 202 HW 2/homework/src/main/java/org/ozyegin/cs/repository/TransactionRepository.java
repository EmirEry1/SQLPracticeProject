package org.ozyegin.cs.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepository extends JdbcDaoSupport {
  private int transaction=0;
  List<Integer> transactionIDs = new ArrayList<Integer>();
  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  public Integer order(String company, int product, int amount, Date createdDate) {

    transaction++;
    transactionIDs.add(transaction);

    Objects.requireNonNull(getJdbcTemplate()).update("INSERT INTO OrderTransaction (cname,pid,amount,orderDate) VALUES(?,?,?,?)",
          company, product,amount,createdDate
            );

    return transaction;
  }

  public void delete(int transactionId) throws Exception {
    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM OrderTransaction WHERE transactionId=?"
    ,transactionId);


    if(transactionIDs.contains(transaction)){

      for(int i=0;i<transactionIDs.size();i++){
        if(transactionIDs.get(i)==transactionId){
          transactionIDs.remove(i);
        }
      }

    }else{
      throw new Exception ("Invalid transaction");
    }
  }

  public void deleteAll() {
    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM OrderTransaction");

    transactionIDs.removeAll(transactionIDs);
  }
}
