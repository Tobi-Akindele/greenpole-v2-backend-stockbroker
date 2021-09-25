package com.ap.greenpole.stockbroker.dto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

public class SearchSpecification<T> implements Specification<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<SearchCriteria> searchCriteriaList;
	private boolean hasShareholderSubQuery = false;
	private boolean hasBondholderSubQuery = false;
	private List<SubQueryAttributes<?>> subQueryAttributesList;
	private String superQueryJoinAttribute;

	public SearchSpecification(List<SearchCriteria> searchCriteriaList, boolean hasShareholderSubQuery,
			 boolean hasBondholderSubQuery, List<SubQueryAttributes<?>> subQueryAttributesList, String superQueryJoinAttribute) {
		this.searchCriteriaList = searchCriteriaList;
		this.hasShareholderSubQuery = hasShareholderSubQuery;
		this.hasBondholderSubQuery = hasBondholderSubQuery;
		this.subQueryAttributesList = subQueryAttributesList;
		this.superQueryJoinAttribute = superQueryJoinAttribute;
	}

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

		// create a new predicate list
		List<Predicate> predicates = new ArrayList<>();

		// add add criteria to predicates
		for (SearchCriteria criteria : searchCriteriaList) {
			if (criteria.getOperation().equals(SearchOperation.GREATER_THAN)) {
				predicates
						.add(criteriaBuilder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString()));
			} else if (criteria.getOperation().equals(SearchOperation.LESS_THAN)) {
				predicates.add(criteriaBuilder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString()));
			} else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(criteria.getKey()),
						criteria.getValue().toString()));
			} else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
				predicates.add(
						criteriaBuilder.lessThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString()));
			} else if (criteria.getOperation().equals(SearchOperation.NOT_EQUAL)) {
				predicates.add(criteriaBuilder.notEqual(root.get(criteria.getKey()), criteria.getValue()));
			} else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
				predicates.add(criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue()));
			} else if (criteria.getOperation().equals(SearchOperation.MATCH)) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(criteria.getKey())),
						"%" + criteria.getValue().toString().toLowerCase() + "%"));
			} else if (criteria.getOperation().equals(SearchOperation.MATCH_END)) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(criteria.getKey())),
						criteria.getValue().toString().toLowerCase() + "%"));
			} else if (criteria.getOperation().equals(SearchOperation.MATCH_START)) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(criteria.getKey())),
						"%" + criteria.getValue().toString().toLowerCase()));
			} else if (criteria.getOperation().equals(SearchOperation.IN)) {
				predicates.add(criteriaBuilder.in(root.get(criteria.getKey())).value(criteria.getValue()));
			} else if (criteria.getOperation().equals(SearchOperation.NOT_IN)) {
				predicates.add(criteriaBuilder.not(root.get(criteria.getKey())).in(criteria.getValue()));
			}
		}
		if (hasShareholderSubQuery) {
			for (SearchCriteria criteria : subQueryAttributesList.get(0).getSubQuerySearchCriteriaList()) {
				Subquery<Long> sub = query.subquery(Long.class);
				Root<?> subRoot = sub.from(subQueryAttributesList.get(0).getSubQueryEntityClass().getClass());
				sub.select(criteriaBuilder.count(subRoot.get(subQueryAttributesList.get(0).getSubQueryIdentityColumn())));
				sub.where(criteriaBuilder.equal(root.get(superQueryJoinAttribute),
						subRoot.get(subQueryAttributesList.get(0).getSubQueryJoinColumn())));
				if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
					predicates.add(criteriaBuilder.lessThanOrEqualTo(sub, (long) criteria.getValue()));
				}
				if(criteria.getOperation().equals(SearchOperation.BETWEEN)) {
					String[] values = criteria.getValue().toString().split(":");
					predicates.add(criteriaBuilder.between(sub, Long.valueOf(values[0]), Long.valueOf(values[1])));
				}
			}
		}
		if (hasBondholderSubQuery) {
			for (SearchCriteria criteria : subQueryAttributesList.get(1).getSubQuerySearchCriteriaList()) {
				Subquery<Long> sub = query.subquery(Long.class);
				Root<?> subRoot = sub.from(subQueryAttributesList.get(1).getSubQueryEntityClass().getClass());
				sub.select(criteriaBuilder.count(subRoot.get(subQueryAttributesList.get(1).getSubQueryIdentityColumn())));
				sub.where(criteriaBuilder.equal(root.get(superQueryJoinAttribute),
						subRoot.get(subQueryAttributesList.get(1).getSubQueryJoinColumn())));
				if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
					predicates.add(criteriaBuilder.lessThanOrEqualTo(sub, (long) criteria.getValue()));
				}
				if(criteria.getOperation().equals(SearchOperation.BETWEEN)) {
					String[] values = criteria.getValue().toString().split(":");
					predicates.add(criteriaBuilder.between(sub, Long.valueOf(values[0]), Long.valueOf(values[1])));
				}
			}
		}

		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

	}

}
