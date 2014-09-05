package net.yangziwen.moviestore.dao;

import java.util.List;

import net.yangziwen.moviestore.dao.base.IAbstractDao;
import net.yangziwen.moviestore.pojo.MovieInfo;
import net.yangziwen.moviestore.pojo.Website;

public interface IMovieInfoDao extends IAbstractDao<MovieInfo> {

	List<String> getYearListByWebsite(Website website);

	List<String> getAreaListByWebsite(Website website);

	List<String> getCategoryListByWebsite(Website website);

	int batchSave(MovieInfo[] infos, int batchSize);

	List<String> getSubcategoryListByWebsiteAndCategory(Website website, String category);

}
