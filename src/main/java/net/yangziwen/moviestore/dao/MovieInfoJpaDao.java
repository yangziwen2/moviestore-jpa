package net.yangziwen.moviestore.dao;

import java.util.List;

import net.yangziwen.moviestore.dao.base.BaseRepository;
import net.yangziwen.moviestore.dao.custom.MovieInfoJpaCustomRepository;
import net.yangziwen.moviestore.pojo.MovieInfo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieInfoJpaDao extends BaseRepository<MovieInfo, Long>, MovieInfoJpaCustomRepository {

	public List<MovieInfo> findByWebsiteIdOrderByMovieIdDesc(Long websiteId, Pageable pageable);
	
	@Query("select distinct year "
			+ "from MovieInfo "
			+ "where websiteId = :websiteId and year is not null "
			+ "order by year desc")
	public List<String> getYearListByWebsiteId(@Param("websiteId")Long websiteId);
	
	@Query("select distinct area "
			+ "from MovieInfo "
			+ "where websiteId = :websiteId and area is not null "
			+ "group by area "
			+ "order by count(*) desc")
	public List<String> getAreaListByWebsiteId(@Param("websiteId")Long websiteId);
	
	@Query("select distinct category "
			+ "from MovieInfo "
			+ "where websiteId = :websiteId and category is not null "
			+ "group by category "
			+ "order by count(*) desc")
	public List<String> getCategoryListByWebsiteId(@Param("websiteId")Long websiteId);
	
	@Query("select distinct subcategory "
			+ "from MovieInfo "
			+ "where websiteId = :websiteId and category = :category and subcategory is not null "
			+ "order by subcategory desc")
	public List<String> getSubcategoryListByWebsiteIdAndCategory(@Param("websiteId")Long websiteId, @Param("category")String category);
	
}
