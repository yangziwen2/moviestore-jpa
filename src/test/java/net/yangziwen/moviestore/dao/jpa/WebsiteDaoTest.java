package net.yangziwen.moviestore.dao.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.yangziwen.moviestore.dao.WebsiteJpaDao;
import net.yangziwen.moviestore.pojo.Website;
import net.yangziwen.moviestore.test.SpringJpaPersistenceTests;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

public class WebsiteDaoTest extends SpringJpaPersistenceTests {

	@Autowired
	private WebsiteJpaDao websiteJpaDao;
	
	@Test
	public void count() {
		int expectedTotal = 4;
		assertEquals(expectedTotal, websiteJpaDao.count());
		assertEquals(1, websiteJpaDao.count(new Specification<Website>() {
			@Override
			public Predicate toPredicate(Root<Website> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return query.where(cb.equal(root.get("id").as(Long.class), 1L)).getRestriction();
			}
		}));
		assertEquals(expectedTotal - 1, websiteJpaDao.count(new Specification<Website>() {
			@Override
			public Predicate toPredicate(Root<Website> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return query.where(cb.greaterThan(root.get("id").as(Long.class), 1L)).getRestriction();
			}
		}));
	}
	
	@Test
	public void getById() {
		assertEquals(Long.valueOf(1L), websiteJpaDao.findOne(1L).getId());
		assertNull(websiteJpaDao.findOne(1000L));
	}
	
	@Test
	public void delete() {
		Long idToDelete = 3L;
		Website website = websiteJpaDao.findOne(idToDelete);
		assertNotNull(website);
		websiteJpaDao.delete(website);
		assertNull(websiteJpaDao.findOne(idToDelete));
	}
	
	@Test
	public void deleteById() {
		Long idToDelete = 3L;
		Website website = websiteJpaDao.findOne(idToDelete);
		assertNotNull(website);
		websiteJpaDao.delete(website);
		assertNull(websiteJpaDao.findOne(idToDelete));
	}
	
	@Test
	public void save() {
		long cntBeforeSave = websiteJpaDao.count();
		Website website = new Website();
		website.setName("test");
		website.setDisplayName("测试");
		websiteJpaDao.save(website);
		assertEquals(cntBeforeSave + 1, websiteJpaDao.count());
	}
	
	@Test
	public void update() {
		Website website = websiteJpaDao.findOne(4L);
		String modifiedDisplayName = website.getDisplayName() + "-modified";
		website.setDisplayName(modifiedDisplayName);
		websiteJpaDao.save(website);
		assertEquals(modifiedDisplayName, websiteJpaDao.findOne(4L).getDisplayName());
	}
}
