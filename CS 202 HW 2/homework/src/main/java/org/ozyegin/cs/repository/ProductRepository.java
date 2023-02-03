package org.ozyegin.cs.repository;

import java.util.*;
import javax.sql.DataSource;
import org.ozyegin.cs.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository extends JdbcDaoSupport {
  final int batchSize = 10;

  final String createP = "INSERT INTO Product (name, description, brandName) VALUES(?,?,?)";
  final String updateP = "UPDATE Product SET name=?, description=?, brandName=? WHERE id=?";
  final String getP = "SELECT * FROM Product WHERE id IN (:ids)";
  final String getIdsP = "SELECT id FROM Product";
  final String getAllP = "SELECT * FROM Product";
  final String getBrandNameP ="SELECT * FROM Product WHERE brandName=?";
  final String getSingleP = "SELECT * FROM Product WHERE id=?";
  final String deleteAllP = "DELETE FROM Product";
  final String deleteP = "DELETE FROM Product WHERE id=?";
  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  private final RowMapper<Product> productRowMapper = (resultSet, i) -> new Product()
          .id(resultSet.getInt("id"))
          .name(resultSet.getString("name"))
          .description(resultSet.getString("description"))
          .brandName(resultSet.getString("brandName"));
  private final RowMapper<Integer> integerRowMapper=((resultSet, i) -> resultSet.getInt("id"));


  public Product find(int id) {
    Product product;

      product = Objects.requireNonNull(getJdbcTemplate()).queryForObject(getSingleP,
              new Object[] {id},
              productRowMapper);
      if(product!=null){
        return product;

      }
      else{
        return null;

      }


  }

  public List<Product> findMultiple(List<Integer> ids) {

    if (ids == null || ids.isEmpty()) {
      return new ArrayList<>();
    } else {
      Map<String, List<Integer>> params = new HashMap<>() {
        {
          this.put("ids", new ArrayList<>(ids));
        }
      };
      var template = new NamedParameterJdbcTemplate(Objects.requireNonNull(getJdbcTemplate()));
      return template.query(getP, params, productRowMapper);
    }


  }

  public List<Product> findByBrandName(String brandName){
    List<Product> products;

    //products=Objects.requireNonNull(getJdbcTemplate()).queryForList(getBrandNameP,Product.class,brandName);
    products=Objects.requireNonNull(getJdbcTemplate()).query(getBrandNameP,new Object[]{brandName},productRowMapper);


    return products;
  }

  public List<Integer> create(List<Product> products) {
    List<Integer> idListFirst=Objects.requireNonNull(getJdbcTemplate()).query(getIdsP,integerRowMapper);

    Objects.requireNonNull(getJdbcTemplate()).batchUpdate(createP, products,
            batchSize,
            (ps, product) -> {

              ps.setString(1, product.getName());
              ps.setString(2, product.getDescription());
              ps.setString(3, product.getBrandName());

              //NOTE: There is probably something wrong here.
              //idList.add(product.getId());


            });

    List<Integer> idListLast=Objects.requireNonNull(getJdbcTemplate()).query("SELECT id FROM Product",integerRowMapper);
     idListLast.removeAll(idListFirst);
    return idListLast;
  }

  public void update(List<Product> products){

    for (Product product:products) {
      if (product.getName() == null) {
        System.out.println("Name is Null");

      }
    }
    Objects.requireNonNull(getJdbcTemplate()).batchUpdate(updateP, products,
            batchSize,
            (ps, product) -> {
              ps.setString(2, product.getDescription());
              ps.setString(1, product.getName());
              ps.setString(3, product.getBrandName());
              ps.setInt(4, product.getId());

            });
  }

  public void delete(List<Integer> ids) {
    for(int id :ids){
      Objects.requireNonNull(getJdbcTemplate()).update(deleteP, id);
    }
  }


  public void deleteAll() {
    Objects.requireNonNull(getJdbcTemplate()).batchUpdate(deleteAllP);
  }
}
