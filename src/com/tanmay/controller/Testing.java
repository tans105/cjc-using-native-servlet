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
 * /**
 * Servlet implementation class Testing
 */
@WebServlet("/welcome")
public class Testing extends HttpServlet {

	private static final String JSON_PATH_APPEND = "/WEB-INF/classes/request_mapping.json";

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jsonPath = request.getServletContext().getRealPath(JSON_PATH_APPEND);
		response.setContentType("application/json");
		StringBuilder sb = new StringBuilder();
		String s;
		Gson gson = new Gson();
		while ((s = request.getReader().readLine()) != null) {
			sb.append(s);
		}
		Bundle bundle = (Bundle) gson.fromJson(sb.toString(), Bundle.class);
		JSONObject obj = fetchCSVPath(bundle.getId(), jsonPath);
		/*
		 * obj will be of below format
		 * {
		 * "id": "url1",
		 * "url": "/home/tanmay/my_projects/GIT/cjc/src/main/resources/test.csv",
		 * "group": "A"
		 * }
		 * 
		 * to get say "url" -> obj.get("url") or "group" -> obj.get("group")
		 * 
		 * make sure to cast the same to string obj.get("url").toString()
		 */
		if (obj != null) {
			CSVReader reader = null;
			List<Map<String, String>> list = new LinkedList<Map<String, String>>();
			try {
				reader = new CSVReader(new FileReader(obj.get("url").toString()));
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
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
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

	private JSONObject fetchCSVPath(String id, String jsonPath) throws IOException {
		Object obj;
		JSONParser parser = new JSONParser();
		try {
			obj = parser.parse(new FileReader(jsonPath));
			JSONArray array = (JSONArray) obj;
			for (int i = 0; i < array.size(); i++) {
				JSONObject jsonObject = (JSONObject) array.get(i);
				if (id.equals(jsonObject.get("id"))) {
					return jsonObject;
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
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