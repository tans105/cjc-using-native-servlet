package com.tanmay.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/JsonAppend")
public class JsonAppend extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String JSON_PATH_APPEND = "/WEB-INF/classes/dummy.json";

	public JsonAppend() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jsonPath = request.getServletContext().getRealPath(JSON_PATH_APPEND);
		response.setContentType("application/json");
		StringBuilder sb = new StringBuilder();
		PrintWriter writer = new PrintWriter(jsonPath);
		writer.print("");
		String s;
		while ((s = request.getReader().readLine()) != null) {
			sb.append(s);
		}
		writer.println(sb);
		writer.close();

	}

}
