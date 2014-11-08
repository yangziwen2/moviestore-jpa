package net.yangziwen.moviestore.dao.base.clause;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public interface Clause<T> {

	public void fillToQuery(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder);
	
}
