package net.yangziwen.moviestore.dao.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import net.yangziwen.moviestore.dao.MovieInfoJpaDao;
import net.yangziwen.moviestore.dao.base.DynamicSpecifications;
import net.yangziwen.moviestore.pojo.MovieInfo;
import net.yangziwen.moviestore.pojo.Website;
import net.yangziwen.moviestore.test.SpringJpaPersistenceTests;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.ui.ModelMap;

public class MovieInfoDaoTest extends SpringJpaPersistenceTests {

	@Autowired
	private MovieInfoJpaDao movieInfoJpaDao;
	
	@Test
	public void getYearListByWebsite() {
		int expectedSize = jdbcTemplate.getJdbcOperations().queryForObject("select count(*) from (select distinct year from movie_info where website_id = 1) as tmp", Integer.class) ;
		
		Website mockWebsite = new Website();
		mockWebsite.setId(1L);
		List<String> yearList = movieInfoJpaDao.getYearListByWebsiteId(mockWebsite.getId());
		
		assertEquals(expectedSize, yearList.size());
		for(int i=0; i < expectedSize - 1; i++) {
			String year1 = yearList.get(i);
			String year2 = yearList.get(i + 1);
			assertEquals(1, year1.compareTo(year2));	// yearList是倒序的
		}
	}
	
	@Test
	public void getMovieInfoByTitleOrArea() {
		int expectedSize = jdbcTemplate.queryForObject(
				"select count(*) from movie_info where title like :title or area = :area and year < :year", 
				new ModelMap().addAttribute("title", "十月%").addAttribute("area", "日本").addAttribute("year", 2014), 
				Integer.class);
		
		List<MovieInfo> list = movieInfoJpaDao.findAll((DynamicSpecifications.<MovieInfo>bySearchParam(new ModelMap()
			.addAttribute("1__or", new ModelMap()
				.addAttribute("title__start_with", "十月")
				.addAttribute("area", "日本")
			)
			.addAttribute("year__lt", 2014)
		)));
		assertEquals(expectedSize, list.size());
	}
	
	@Test
	@Ignore("h2 can not run the 'order by count(*)' clause due to the difference between it and sqlite/mysql")
	public void getAreaListByWebsite() {
		Long websiteId = 1L;
		int expectedSize = jdbcTemplate.queryForObject("select count(*) from (select distinct area from movie_info where website_id = :websiteId) as tmp",
				new ModelMap().addAttribute("websiteId", websiteId),
				Integer.class);
		
		Website mockWebsite = new Website();
		mockWebsite.setId(websiteId);
		List<String> areaList = movieInfoJpaDao.getAreaListByWebsiteId(mockWebsite.getId());
		
		assertEquals(expectedSize, areaList.size());
		
		// area是按所对应的影片的数量倒排序的
		String validateCntSql = "select count(*) from movie_info where website_id = :websiteId and area = :area";
		
		int areaCnt1 = jdbcTemplate.queryForObject(validateCntSql, new ModelMap()
			.addAttribute("websiteId", websiteId)
			.addAttribute("area", areaList.get(0))
		, Integer.class);
		
		int areaCnt2 = 0;
		
		for(int i=1; i < expectedSize - 1; i++, areaCnt1 = areaCnt2) {
			areaCnt2 = jdbcTemplate.queryForObject(validateCntSql, new ModelMap()
					.addAttribute("websiteId", websiteId)
					.addAttribute("area", areaList.get(i))
				, Integer.class);
			assertTrue(areaCnt1 >= areaCnt2);
		}
	}
	
	@Test
	public void batchSave() {
		List<MovieInfo> list = movieInfoJpaDao.findAll();
		MovieInfo[] infos = list.toArray(new MovieInfo[]{});
		int batchSize = infos.length / 3;
		try {
			movieInfoJpaDao.batchSave(infos, batchSize);
			fail("entities with ids should not be batch saved!");
		} catch (Exception e) {
			assertTrue(e instanceof InvalidDataAccessApiUsageException);
		}
		List<MovieInfo> infosToBatchSave = new ArrayList<MovieInfo>(infos.length);
		for(MovieInfo info: list) {
			MovieInfo newInfo = new MovieInfo();
			BeanUtils.copyProperties(info, newInfo);
			newInfo.setId(null);
			infosToBatchSave.add(newInfo);
		}
		movieInfoJpaDao.batchSave(infosToBatchSave.toArray(new MovieInfo[]{}), batchSize);
		assertEquals(list.size() * 2, movieInfoJpaDao.count());
	}
	
}
