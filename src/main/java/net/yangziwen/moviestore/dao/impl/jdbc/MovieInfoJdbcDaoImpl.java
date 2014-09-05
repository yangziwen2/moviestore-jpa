package net.yangziwen.moviestore.dao.impl.jdbc;

import java.util.List;

import net.yangziwen.moviestore.dao.IMovieInfoDao;
import net.yangziwen.moviestore.dao.base.AbstractJdbcDaoImpl;
import net.yangziwen.moviestore.dao.base.DaoConstant;
import net.yangziwen.moviestore.pojo.MovieInfo;
import net.yangziwen.moviestore.pojo.Website;

import org.springframework.stereotype.Repository;
import org.springframework.ui.ModelMap;

@Repository
public class MovieInfoJdbcDaoImpl extends AbstractJdbcDaoImpl<MovieInfo> implements IMovieInfoDao {

	@Override
	public List<String> getYearListByWebsite(Website website) {
		ModelMap param = new ModelMap()
			.addAttribute("websiteId", website.getId())
			.addAttribute("year__is_not_null", null)
			.addAttribute(DaoConstant.ORDER_BY, new ModelMap()
				.addAttribute("year", DaoConstant.ORDER_DESC)
		);
		String sql = generateSqlByParam("select distinct year", param);
		return jdbcTemplate.queryForList(sql, param, String.class);
	}

	@Override
	public List<String> getAreaListByWebsite(Website website) {
		ModelMap param = new ModelMap()
			.addAttribute("websiteId", website.getId())
			.addAttribute("area__is_not_null", null)
			.addAttribute(DaoConstant.GROUP_BY, "area")
			.addAttribute(DaoConstant.ORDER_BY, new ModelMap()
				.addAttribute("cnt", DaoConstant.ORDER_DESC)
			);
		// 遇到了测试时内存数据库不支持order by count(*)的情形
		String sql = "select tmp.area from (" + generateSqlByParam("select distinct area, count(*) as cnt", param) + ") as tmp";
		return jdbcTemplate.queryForList(sql, param, String.class);
	}

	@Override
	public List<String> getCategoryListByWebsite(Website website) {
		ModelMap param = new ModelMap()
			.addAttribute("websiteId", website.getId())
			.addAttribute("category__is_not_null", null)
			.addAttribute(DaoConstant.GROUP_BY, "category")
			.addAttribute(DaoConstant.ORDER_BY, "count(*) desc");
		String sql = generateSqlByParam("select distinct category", param);
		return jdbcTemplate.queryForList(sql, param, String.class);
	}

	@Override
	public List<String> getSubcategoryListByWebsiteAndCategory(Website website, String category) {
		ModelMap param = new ModelMap()
			.addAttribute("websiteId", website.getId())
			.addAttribute("category", category)
			.addAttribute("subcategory__is_not_null", null)
			.addAttribute(DaoConstant.ORDER_BY, "subcategory desc");
		String sql = generateSqlByParam("select distinct subcategory", param);
		return jdbcTemplate.queryForList(sql, param, String.class);
	}
	
	@Override
	public int batchSave(MovieInfo[] infos, int batchSize) {
		return super.batchSave(infos, batchSize);
	}

}
