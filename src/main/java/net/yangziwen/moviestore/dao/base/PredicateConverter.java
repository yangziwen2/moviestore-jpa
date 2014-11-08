package net.yangziwen.moviestore.dao.base;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface PredicateConverter {

	public <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder);
}
