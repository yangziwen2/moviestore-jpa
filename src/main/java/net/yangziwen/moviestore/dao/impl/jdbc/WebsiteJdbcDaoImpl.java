package net.yangziwen.moviestore.dao.impl.jdbc;

import org.springframework.stereotype.Repository;

import net.yangziwen.moviestore.dao.IWebsiteDao;
import net.yangziwen.moviestore.dao.base.AbstractJdbcDaoImpl;
import net.yangziwen.moviestore.pojo.Website;

@Repository
public class WebsiteJdbcDaoImpl extends AbstractJdbcDaoImpl<Website> implements IWebsiteDao {

}
