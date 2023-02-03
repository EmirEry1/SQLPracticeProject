package org.ozyegin.cs.repository;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class ProduceRepository extends JdbcDaoSupport {

  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }
    private int produceId=0;
  List<Integer> produceIDs = new ArrayList<Integer>();
  public Integer produce(String company, int product, int capacity) {
    produceId++;
    produceIDs.add(produceId);

    Objects.requireNonNull(getJdbcTemplate()).update("INSERT INTO Produce (cname,pid,capacity) VALUES(?,?,?)",
            company, product,capacity
    );

    return produceId;
  }

  public void delete(int produceId) throws Exception {

    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM Produce WHERE produceId=?"
            ,produceId);

    if(produceIDs.contains(produceId)){
     // produceIDs.removeIf(n -> (n == produceId));
      for(int i=0;i<produceIDs.size();i++){
        if(produceIDs.get(i)==produceId){
          produceIDs.remove(i);
        }
      }
    }
    else{
      throw new Exception ("Invalid produceId");
    }
  }

  public void deleteAll() {

    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM Produce"
    );
    produceIDs.removeAll(produceIDs);
  }
}
