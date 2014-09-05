package net.yangziwen.moviestore.test;

import java.util.Collections;
import java.util.Map;

import javax.sql.DataSource;

import net.yangziwen.moviestore.util.Profiles;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@Ignore("the abstract test class")
@ActiveProfiles(Profiles.UNIT_TEST)
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "jpaTxManager")
@Transactional
@ContextConfiguration(locations = {
	"/applicationContext-jpa.xml", 
	"/applicationContext-test.xml"
})
public class SpringJpaPersistenceTests {
	
	protected static final Map<String, Object> EMPTY_PARAM = Collections.emptyMap();

	protected DataSource dataSource;
	
	protected NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
}
