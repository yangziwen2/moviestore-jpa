package net.yangziwen.moviestore.dao.impl.hibernate;

import java.util.List;

import net.yangziwen.moviestore.dao.IMovieInfoDao;
import net.yangziwen.moviestore.dao.base.AbstractHibernateDaoImpl;
import net.yangziwen.moviestore.pojo.MovieInfo;
import net.yangziwen.moviestore.pojo.Website;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.ui.ModelMap;

@Repository
public class MovieInfoHibernateDaoImpl extends AbstractHibernateDaoImpl<MovieInfo> implements IMovieInfoDao {
	
	@Override
	public int batchSave(MovieInfo[] infos, int batchSize) {
		if(ArrayUtils.isEmpty(infos)) {
			return 0;
		}
		for(MovieInfo info: infos) {
			if(info.getId() != null) {
				throw new IllegalArgumentException("The id of the entity should be null before insersion!");
			}
		}
		String insertPrefix = new StringBuilder()
			.append(" insert into movie_info ")
			.append(" ( website_id, movie_id, title, actors, area, year, photo_url, category, subcategory )")
			.append(" values ")
			.toString();
		
		int insertRows = 0;
		for(int i = 0, l = infos.length; i < l; i += batchSize) {
			int bound = Math.min(l - i, batchSize);
			StringBuilder sqlBuff = new StringBuilder(insertPrefix);
			sqlBuff.append(transformToInsertValue(infos[i]));
			for(int j = 1; j < bound; j++) {
				sqlBuff.append(", ").append(transformToInsertValue(infos[i+j]));
			}
			insertRows += baseHibernateDao.executeSql(getSession(), sqlBuff.toString()); 
		}
		return insertRows;
	}
	
	private String transformToInsertValue(MovieInfo info) {
		return new StringBuilder("(")
			.append(info.getWebsiteId())
			.append(",")
			.append(info.getMovieId())
			.append(",")
			.append(escapeStringForInsert(info.getTitle()))
			.append(",")
			.append(escapeStringForInsert(info.getActors()))
			.append(",")
			.append(escapeStringForInsert(info.getArea()))
			.append(",")
			.append(escapeStringForInsert(info.getYear()))
			.append(",")
			.append(escapeStringForInsert(info.getPhotoUrl()))
			.append(",")
			.append(escapeStringForInsert(info.getCategory()))
			.append(",")
			.append(escapeStringForInsert(info.getSubcategory()))
			.append(")")
			.toString();
	}
	
	private String escapeStringForInsert(String value) {
		if(value == null) {
			return null;
		}
		value = StringUtils.replace(value, "'", "''");
		return "'" + value + "'";
	}

	@Override
	public List<String> getYearListByWebsite(Website website) {
		StringBuilder hqlBuff = new StringBuilder()
			.append(" select distinct year ")
			.append(" from MovieInfo ")
			.append(" where 1 = 1")
			.append("	and websiteId = :websiteId ")
			.append("	and year is not null ")
			.append(" order by year desc");
		return baseHibernateDao.<String>list(getSession(), 0, 0, hqlBuff.toString(), new ModelMap()
			.addAttribute("websiteId", website.getId())
		);
	}
	
	@Override
	public List<String> getAreaListByWebsite(Website website) {
		StringBuilder hqlBuff = new StringBuilder()
			.append(" select distinct area") 
			.append(" from MovieInfo ")
			.append(" where 1 = 1 ")
			.append("	and websiteId = :websiteId ")
			.append("	and area is not null ")
			.append(" group by area ")
			.append(" order by count(*) desc ");
		return baseHibernateDao.<String>list(getSession(), 0, 0, hqlBuff.toString(), new ModelMap()
			.addAttribute("websiteId", website.getId())
		);
	}
	
	@Override
	public List<String> getCategoryListByWebsite(Website website) {
		StringBuilder hqlBuff = new StringBuilder()
			.append(" select distinct category ")
			.append(" from MovieInfo ")
			.append(" where 1 = 1 ")
			.append("	and websiteId = :websiteId ")
			.append("	and category is not null ")
			.append(" group by category ")
			.append(" order by count(*) desc ");
		return baseHibernateDao.<String>list(getSession(), 0, 0, hqlBuff.toString(), new ModelMap()
			.addAttribute("websiteId", website.getId())
		);
	}
	
	@Override
	public List<String> getSubcategoryListByWebsiteAndCategory(Website website, String category) {
		StringBuilder hqlBuff = new StringBuilder()
			.append(" select distinct subcategory ")
			.append(" from MovieInfo ")
			.append(" where 1 = 1 ")
			.append("	and websiteId = :websiteId ")
			.append("	and category = :category ")
			.append("	and subcategory is not null ")
			.append(" order by subcategory desc ");
		return baseHibernateDao.<String>list(getSession(), 0, 0, hqlBuff.toString(), new ModelMap()
			.addAttribute("websiteId", website.getId())
			.addAttribute("category", category)
		);
	}
	
}
