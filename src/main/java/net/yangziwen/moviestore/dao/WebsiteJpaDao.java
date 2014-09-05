package net.yangziwen.moviestore.dao;

import net.yangziwen.moviestore.pojo.Website;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WebsiteJpaDao extends JpaRepository<Website, Long>, JpaSpecificationExecutor<Website> {
	
	public Website getByName(String name);

}
