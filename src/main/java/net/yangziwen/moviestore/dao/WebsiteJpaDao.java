package net.yangziwen.moviestore.dao;

import net.yangziwen.moviestore.dao.base.BaseRepository;
import net.yangziwen.moviestore.pojo.Website;

public interface WebsiteJpaDao extends BaseRepository<Website, Long> {
	
	public Website getByName(String name);

}
