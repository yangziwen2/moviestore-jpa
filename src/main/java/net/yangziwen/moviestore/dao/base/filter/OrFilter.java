package net.yangziwen.moviestore.dao.base.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.yangziwen.moviestore.dao.base.PredicateConverter;

import org.springframework.util.CollectionUtils;

public class OrFilter implements PredicateConverter {

	private List<AndFilter> andList = new ArrayList<AndFilter>();
	
	private OrFilter() {}
	
	public OrFilter and(AndFilter andFilter) {
		andList.add(andFilter);
		return this;
	}
	
	public static OrFilter and(AndFilter... andFilters) {
		OrFilter filter = new OrFilter();
		filter.andList.addAll(Arrays.asList(andFilters));
		return filter;
	}


	@Override
	public <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder) {
		if(CollectionUtils.isEmpty(andList)) {
			return null;
		}
		List<Predicate> predicates = new ArrayList<Predicate>(andList.size());
		for(AndFilter filter: andList) {
			predicates.add(filter.toPredicate(root, builder));
		}
		return builder.or(predicates.toArray(new Predicate[]{}));
	}
}
