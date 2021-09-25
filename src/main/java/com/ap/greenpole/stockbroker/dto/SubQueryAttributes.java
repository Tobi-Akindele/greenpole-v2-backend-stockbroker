package com.ap.greenpole.stockbroker.dto;

import java.util.List;

public class SubQueryAttributes<T> {

	private List<SearchCriteria> subQuerySearchCriteriaList;
	private T subQueryEntityClass;
	private String subQueryJoinColumn;
	private String subQueryIdentityColumn;

	public SubQueryAttributes(List<SearchCriteria> subQuerySearchCriteriaList, T subQueryEntityClass,
			String queryAttributeName, String subQueryIdentityColumn) {
		this.subQuerySearchCriteriaList = subQuerySearchCriteriaList;
		this.subQueryEntityClass = subQueryEntityClass;
		this.subQueryJoinColumn = queryAttributeName;
		this.subQueryIdentityColumn = subQueryIdentityColumn;
	}

	public List<SearchCriteria> getSubQuerySearchCriteriaList() {
		return subQuerySearchCriteriaList;
	}

	public void setSubQuerySearchCriteriaList(List<SearchCriteria> subQuerySearchCriteriaList) {
		this.subQuerySearchCriteriaList = subQuerySearchCriteriaList;
	}

	public T getSubQueryEntityClass() {
		return subQueryEntityClass;
	}

	public void setSubQueryEntityClass(T subQueryEntityClass) {
		this.subQueryEntityClass = subQueryEntityClass;
	}

	public String getSubQueryJoinColumn() {
		return subQueryJoinColumn;
	}

	public void setSubQueryJoinColumn(String subQueryJoinColumn) {
		this.subQueryJoinColumn = subQueryJoinColumn;
	}

	public String getSubQueryIdentityColumn() {
		return subQueryIdentityColumn;
	}

	public void setSubQueryIdentityColumn(String subQueryIdentityColumn) {
		this.subQueryIdentityColumn = subQueryIdentityColumn;
	}
}
