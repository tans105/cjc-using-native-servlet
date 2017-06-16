package com.tanmay.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import au.com.bytecode.opencsv.CSVReader;

import com.google.gson.Gson;
import com.tanmay.entity.Bundle;
/**
/**
 * Servlet implementation class Testing
 */
@WebServlet("/welcome")
public class Testing extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		StringBuilder sb = new StringBuilder();
		String s;
		Gson gson = new Gson();
		while ((s = request.getReader().readLine()) != null) {
			sb.append(s);
		}
		Bundle bundle = (Bundle) gson.fromJson(sb.toString(), Bundle.class);
		String csvFilePath = fetchCSVPath(bundle.getId());
		CSVReader reader = null;
		List<Map<String, String>> list = new LinkedList<Map<String, String>>();
		try {
			reader = new CSVReader(new FileReader(csvFilePath));
			String[] header = reader.readNext();
			List<String> headers = new LinkedList<String>();
			for (int i = 0; i < header.length; i++) {
				headers.add(header[i]);
			}
			String[] line;

			while ((line = reader.readNext()) != null) {
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				for (int i = 0; i < headers.size(); i++) {
					map.put(header[i], line[i]);
				}
				if (applyFilters(bundle.getFilter(), map, bundle.getCombination())) {
					if (null != bundle.getColumns()) {
						for (int j = 0; j < header.length; j++) {
							if (bundle.getColumns().contains(j)) {
								continue;
							} else {
								map.remove(header[j]);
							}
						}
					}
					list.add(map);
				} else
					continue;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(createJson(list));

	}

	private Boolean applyFilters(Map<String, ArrayList<String>> filter, LinkedHashMap<String, String> map, Boolean combination) {
		if (null == combination) {
			combination = true;
		}
		Boolean passed = Boolean.FALSE;
		if (!combination) {
			for (Map.Entry<String, ArrayList<String>> e : filter.entrySet()) {
				if (e.getValue().indexOf(map.get(e.getKey())) >= 0)
					passed = Boolean.TRUE;
				else {
					passed = Boolean.FALSE;
					break;
				}
			}
		} else {
			for (Map.Entry<String, ArrayList<String>> e : filter.entrySet()) {
				if (e.getValue().indexOf(map.get(e.getKey())) >= 0)
					passed = Boolean.TRUE;
			}
		}
		return passed;

	}

	private String fetchCSVPath(String id) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream("config.properties");
		Properties properties = new Properties();
		properties.load(input);
		//
		File file = new File(properties.get("jsonPath").toString());
		Object obj;
		JSONParser parser = new JSONParser();
		String path = null;
		try {
			obj = parser.parse(new FileReader(file));
			JSONObject jsonObject = (JSONObject) obj;
			path = jsonObject.get(id).toString();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return path;

	}

	@SuppressWarnings("unchecked")
	private String createJson(List<Map<String, String>> list) {
		JSONArray jsonArray = new JSONArray();
		for (Map<String, String> map : list) {
			JSONObject obj = new JSONObject();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				obj.put(key, value);
			}
			jsonArray.add(obj);
		}
		return jsonArray.toString();

	}

	public void destroy() {
		// do nothing.
	}
}