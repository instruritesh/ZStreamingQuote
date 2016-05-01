package com.ritesh.zstreamingquote.webservice.jetty.handlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;

import com.ritesh.zstreamingquote.config.ZStreamingConfig;
import com.ritesh.zstreamingquote.control.ZStreamingQuoteControl;

public class ProcessStopActionHandler extends ContextHandler {
	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private TimeZone timeZone = TimeZone.getTimeZone("IST");
	
	/**
	 * Constructor
	 */
	public ProcessStopActionHandler(){
		setContextPath(ZStreamingConfig.getJettyServerProcessStopURL());
		dateFormat.setTimeZone(timeZone);
	}
	
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String reqMethod = request.getMethod();

		if(reqMethod.equals("GET")){
			//GET method
			if(ZStreamingConfig.isWebServiceLogsPrintable()){
				System.out.println("ProcessStopActionHandler.doHandle(): ZStreamingQuote Process STOP initiated" + 
					" [" + dateFormat.format(Calendar.getInstance(timeZone).getTime()) + "]");
			}
			
			//Stop the process
			ZStreamingQuoteControl.getInstance().stop();
			
			//write HTTP o/p back to client
			PrintWriter out = response.getWriter();
			out.println("<h1>ZStreamingQuote Process - STOPPED</h1>");
		} else if(reqMethod.equals("POST")){
			//POST method not handled currently
		} else{
			//Default - other handlers not supported
			System.out.println("ProcessStopActionHandler.doHandle(): ERROR: Request method not proper: " + reqMethod);
		}

		response.setContentType("text/html; charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		
		baseRequest.setHandled(true);
	}
}
