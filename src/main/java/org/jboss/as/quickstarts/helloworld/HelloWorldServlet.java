/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.helloworld;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
/**
 * <p>
 * A simple servlet taking advantage of features added in 3.0.
 * </p>
 *
 * <p>
 * The servlet is registered and mapped to /HelloServlet using the {@linkplain WebServlet
 * @HttpServlet}. The {@link HelloService} is injected by CDI.
 * </p>
 *
 * @author Pete Muir
 *
 */
@SuppressWarnings("serial")
@WebServlet("/HelloWorld")
public class HelloWorldServlet extends HttpServlet {

    static String PAGE_HEADER = "<html><head><title>helloworld</title></head><body>";

    static String PAGE_FOOTER = "</body></html>";

    @Inject
    HelloService helloService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	String msg = getMessageFromDB();
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        writer.println(PAGE_HEADER);
        writer.println("<h1>" + helloService.createHelloMessage("World") + "</h1>");
        writer.print("from DB:" + msg);
        writer.print("<br>HostName : " + InetAddress.getLocalHost().getHostName());
        writer.print("<br>SessionStatus : " + checkSession(req));
        writer.println(PAGE_FOOTER);
        writer.close();
    }

    private String checkSession(HttpServletRequest req) throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      session.setMaxInactiveInterval(10 * 60);

      if (session.isNew()) {
          return "NEW SESSION";
      }
      else {
          return "OLD SESSION";
      }
      
    }
    
    private String getMessageFromDB() {
    	String msg = null;
    	try {
    		DBUtil dbUtil = DBUtil.getInstance();
    		Connection conn = dbUtil.getConnection();
    		PreparedStatement stmt = conn.prepareStatement("select msg from hello");
    		ResultSet rs = stmt.executeQuery();
    		
    		while (rs.next()) {
    			msg = rs.getString("msg");
    		}
    	}
    	catch (Exception ex) {
    		msg = "Get message failed.";
    		ex.printStackTrace();
    	}
    	return msg;
    }

}
