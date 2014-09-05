package net.yangziwen.moviestore.dao.impl.jpa;

import java.util.List;

import net.yangziwen.moviestore.pojo.MovieInfo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface MovieInfoJpaDao extends JpaRepository<MovieInfo, Long>, JpaSpecificationExecutor<MovieInfo>, MovieInfoRepositoryCustom {

	@Query("from MovieInfo "
			+ "where websiteId = ?1 "
			+ "order by movieId desc")
	public List<MovieInfo> findByWebsiteIdOrderByMovieId(Long websiteId, Pageable pageable);
	
	@Query("select distinct year "
			+ "from MovieInfo "
			+ "where websiteId = ?1 and year is not null "
			+ "order by year desc")
	public List<String> getYearListByWebsiteId(Long websiteId);
	
	@Query("select distinct area "
			+ "from MovieInfo "
			+ "where websiteId = ?1 and area is not null "
			+ "group by area "
			+ "order by count(*) desc")
	public List<String> getAreaListByWebsiteId(Long websiteId);
	
	@Query("select distinct category "
			+ "from MovieInfo "
			+ "where websiteId = ?1 and category is not null "
			+ "group by category "
			+ "order by count(*) desc")
	public List<String> getCategoryListByWebsiteId(Long websiteId);
	
	@Query("select distinct subcategory "
			+ "from MovieInfo "
			+ "where websiteId = ?1 and category = ?2 and subcategory is not null "
			+ "order by subcategory desc")
	public List<String> getSubcategoryListByWebsiteIdAndCategory(Long websiteId, String category);
	
}
