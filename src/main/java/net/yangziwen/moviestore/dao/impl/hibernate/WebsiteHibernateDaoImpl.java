package net.yangziwen.moviestore.dao.impl.hibernate;

import org.springframework.stereotype.Repository;

import net.yangziwen.moviestore.dao.IWebsiteDao;
import net.yangziwen.moviestore.dao.base.AbstractHibernateDaoImpl;
import net.yangziwen.moviestore.pojo.Website;

@Repository
public class WebsiteHibernateDaoImpl extends AbstractHibernateDaoImpl<Website> implements IWebsiteDao {

}
