package org.ozyegin.cs.repository;

import org.ozyegin.cs.entity.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

@Repository
public class CompanyRepository extends JdbcDaoSupport {

  final int batchSize=10;

  final String createC = "INSERT INTO Company (name, phoneNumber, country, zip, streetInfo) VALUES(?,?,?,?,?)";
  //TODO:  Email implement
  final String updateC = "UPDATE Company SET phoneNumber=?, country=?, zip=?, streetInfo=? WHERE name=?";
  final String getC = "SELECT * FROM Company,ZipNumbers WHERE name IN (:names) AND ZipNumbers.zip=Company.zip";
  final String getAllC = "SELECT * FROM Company,ZipNumbers WHERE ZipNumbers.zip=Company.zip";
  final String getSingleC = "SELECT * FROM Company,ZipNumbers WHERE name=? AND ZipNumbers.zip=Company.zip";
  final String getByCountryC =  "SELECT * FROM Company,ZipNumbers WHERE country=? AND ZipNumbers.zip=Company.zip";
  final String deleteAllC = "DELETE FROM Company";
  final String deleteC = "DELETE FROM Company WHERE name=?";

  @Autowired
  public void setDatasource(DataSource dataSource) {

    super.setDataSource(dataSource);
  }
  private final RowMapper<Company> companyRowMapper = (resultSet, i) -> new Company()
          .name(resultSet.getString("name"))
          .phoneNumber(resultSet.getString("phoneNumber"))
          .streetInfo(resultSet.getString("streetInfo"))
          .country(resultSet.getString("country"))
          .zip(resultSet.getInt("zip"))
          .city(resultSet.getString("city"));



  private final RowMapper<String> stringRowMapper = (resultSet, i) -> resultSet.getString(1);
  private final RowMapper<Integer> intRowMapper = (resultSet, i) -> resultSet.getInt(1);


          //TODO:  Email implement

  public Company find(String name) {

    Company company;

      company = Objects.requireNonNull(getJdbcTemplate()).queryForObject(getSingleC,
              new Object[] {name},
              companyRowMapper);
        List<String> e_mails = Objects.requireNonNull(getJdbcTemplate().query("SELECT e_mail FROM Has_Email WHERE cName=?", new Object[] {company.getName()},stringRowMapper));

        company.setE_mails(e_mails);

    return company;
  }

  public List<Company> findByCountry(String country) {

    List<Company> companies;

    companies=Objects.requireNonNull(getJdbcTemplate()).query(getByCountryC,new Object[]{country},companyRowMapper);
      for(Company company: companies){
          List<String> e_mails = Objects.requireNonNull(getJdbcTemplate().query("SELECT e_mail FROM Has_Email WHERE cName=?", new Object[] {company.getName()},stringRowMapper));

          company.setE_mails(e_mails);
      }
    return companies;

  }

  public String create(Company company) throws Exception {
    List<Integer> allZips = Objects.requireNonNull(getJdbcTemplate()).query("SELECT zip From ZipNumbers", intRowMapper);
    if(!allZips.contains(company.getZip())){
      Objects.requireNonNull(getJdbcTemplate()).update("INSERT INTO ZipNumbers  (zip, city) VALUES(?,?)"
              ,company.getZip(), company.getCity());
    }
      String city1 = Objects.requireNonNull(getJdbcTemplate()).queryForObject("SELECT city FROM ZipNumbers WHERE zip=?",new Object[] {company.getZip()}, stringRowMapper);
      if(!city1.equals(company.getCity())){
          throw new Exception("There is a diffrent city coresponding to this zip value not in ZipNumbers table");
      }


    Objects.requireNonNull(getJdbcTemplate()).update(createC,
            company.getName(),
            company.getPhoneNumber(),
            company.getCountry(),
            company.getZip(),
            company.getStreetInfo()
    );
    String city = Objects.requireNonNull(getJdbcTemplate()).queryForObject("SELECT city FROM ZipNumbers WHERE zip=?",new Object[] {company.getZip()}, stringRowMapper);
    company.setCity(city);

      for(String e_mail : company.getE_mails()){
          Objects.requireNonNull(getJdbcTemplate()).update("INSERT INTO Has_Email (cName, e_mail) VALUES (?,?)", company.getName(), e_mail);
      }

      List<String> e_mails = Objects.requireNonNull(getJdbcTemplate().query("SELECT e_mail FROM Has_Email WHERE cName=?", new Object[] {company.getName()},stringRowMapper));

    return company.getName();

  }

  public String delete(String name) {
      Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM Has_Email WHERE cName=?",name);
    Objects.requireNonNull(getJdbcTemplate()).update(deleteC,name);


    return name;
  }

  public void deleteAll() {
      Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM Has_Email");
    Objects.requireNonNull(getJdbcTemplate()).update(deleteAllC);
  }
}
