package com.tanmay.entity;

import java.util.ArrayList;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author : tanmay
 * @created : 12-Jun-2017
 */
@XmlRootElement
public class Bundle {
	private String id;
	private Map<String, ArrayList<String>> filter;
	private Boolean combination;
	private ArrayList<Integer> columns;  

	public Map<String, ArrayList<String>> getFilter() {
		return filter;
	}

	public void setFilter(Map<String, ArrayList<String>> filter) {
		this.filter = filter;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getCombination() {
		return combination;
	}

	public void setCombination(Boolean combination) {
		this.combination = combination;
	}

	public ArrayList<Integer> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<Integer> columns) {
		this.columns = columns;
	}
}
