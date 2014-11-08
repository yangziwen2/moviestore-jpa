package net.yangziwen.moviestore.dao.base.clause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.yangziwen.moviestore.dao.base.PredicateConverter;
import net.yangziwen.moviestore.dao.base.filter.AndFilter;
import net.yangziwen.moviestore.dao.base.filter.OrFilter;

/**
 * 包括where和having应该都可以用ConditionClause
 */
public class ConditionClause<T> implements Clause<T> {

	private List<AndFilter> andList = new ArrayList<AndFilter>();
	private List<OrFilter> orList = new ArrayList<OrFilter>();
	
	//----- and ------//
	
	public ConditionClause<T> and(AndFilter andFilter) {
		andList.add(andFilter);
		return this;
	}
	
	public AndFilter getAndFilter(int index) {
		if(index < 0 || index >= sizeOfAndFilters()) {
			throw new IndexOutOfBoundsException(String.format("index[%d] of andFilter doesn't exist!", index));
		}
		return andList.get(index);
	}
	
	public int sizeOfAndFilters() {
		return andList.size();
	}
	
	//----- or ------//
	
	public ConditionClause<T> or(OrFilter orFilter) {
		orList.add(orFilter);
		return this;
	}
	
	public OrFilter getOrFilter(int index) {
		if(index < 0 || index >= sizeOfOrFilters()) {
			throw new IndexOutOfBoundsException(String.format("index[%d] of orFilter doesn't exist!", index));
		}
		return orList.get(index);
	}
	
	public int sizeOfOrFilters() {
		return orList.size();
	}
	
	public static <T> ConditionClause<T> and(AndFilter... andFilters) {
		ConditionClause<T> clause = new ConditionClause<T>();
		if(andFilters == null) {
			return clause;
		}
		clause.andList.addAll(Arrays.asList(andFilters));
		return clause;
	}
	
	public static <T> ConditionClause<T> or(OrFilter... orFilters) {
		ConditionClause<T> clause = new ConditionClause<T>();
		if(orFilters == null) {
			return clause;
		}
		clause.orList.addAll(Arrays.asList(orFilters));
		return clause;
	}

	@Override
	public void fillToQuery(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		Predicate predicate = null;
		for(PredicateConverter or: orList) {
			if(or == null) {
				continue;
			}
			predicate = or.toPredicate(root, builder);
			if(predicate != null) {
				predicates.add(predicate);
			}
		}
		for(PredicateConverter and: andList) {
			if(and == null) {
				continue;
			}
			predicate = and.toPredicate(root, builder);
			if(predicate != null) {
				predicates.add(predicate);
			}
		}
		query.where(builder.and(predicates.toArray(new Predicate[]{})));
	}
	
}
