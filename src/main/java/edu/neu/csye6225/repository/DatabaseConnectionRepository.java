package edu.neu.csye6225.repository;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DatabaseConnectionRepository{

    @Autowired
    private JdbcTemplate template;

    public Integer checkConnection(){
        return template.queryForObject("SELECT 1", Integer.class);
    };

}
