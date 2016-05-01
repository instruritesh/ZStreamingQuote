package com.ritesh.zstreamingquote.webservice.jetty.handlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;

import com.ritesh.zstreamingquote.config.ZStreamingConfig;
import com.ritesh.zstreamingquote.control.TradingHolidays;
import com.ritesh.zstreamingquote.control.ZStreamingQuoteControl;

public class ProcessStartActionHandler extends ContextHandler {
	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private TimeZone timeZone = TimeZone.getTimeZone("IST");
	
	/**
	 * constructor
	 */
	public ProcessStartActionHandler(){
		setContextPath(ZStreamingConfig.getJettyServerProcessStartURL());
		dateFormat.setTimeZone(timeZone);
	}
	
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String reqMethod = request.getMethod();

		if(reqMethod.equals("GET")){
			//GET method
			if(ZStreamingConfig.isWebServiceLogsPrintable()){
				System.out.println("ProcessStartActionHandler.doHandle(): ZStreamingQuote Process [GET]: START initiated" + 
					" [" + dateFormat.format(Calendar.getInstance(timeZone).getTime()) + "]");
			}
			
			//map request params to values
			Map<String, String> queryValMap = getQueryParameters(request);
			
			//Start the process
			String apiKey = queryValMap.get(ZStreamingConfig.getJettyServerProcessStartApiKeyReqParam());
			String userId = queryValMap.get(ZStreamingConfig.getJettyServerProcessStartUserIdReqParam());
			String publicToken = queryValMap.get(ZStreamingConfig.getJettyServerProcessStartPubTokenReqParam());
			if(ZStreamingConfig.isWebServiceLogsPrintable()){
				System.out.println("ProcessStartActionHandler.doHandle(): DEBUG: apiKey: [" + apiKey + 
						"], userId: [" + userId + "], publicToken: [" + publicToken + "]");
			}
			if(!TradingHolidays.isHoliday()){
				ZStreamingQuoteControl.getInstance().start(apiKey, userId, publicToken);
			} else{
				System.out.println("ProcessStartActionHandler.doHandle(): ERROR: Holiday Today....HALT Process !!!");
			}
			
			//write HTTP o/p back to client
			PrintWriter out = response.getWriter();
			out.println("<h1>ZStreamingQuote Process - STARTED</h1>");
		} else if(reqMethod.equals("POST")){
			//POST method not handled currently
		} else{
			//Default - other handlers not supported
			System.out.println("ProcessStartActionHandler.doHandle(): ERROR: Request method not proper: " + reqMethod);
		}

		response.setContentType("text/html; charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		
		baseRequest.setHandled(true);
	}
	
	/**
	 * getQueryParameters - map of query parameter to value
	 * @param request
	 * @return map of request parameters
	 */
	private Map<String, String> getQueryParameters(HttpServletRequest request){
		Map<String, String> queryParameters = new HashMap<>();
	    String queryString = request.getQueryString();

	    if (StringUtils.isEmpty(queryString)) {
	    	System.out.println("ProcessStartActionHandler.getQueryParameters(): ERROR: query string is empty !!!");
	        return null;
	    }

	    String[] parameters;
	    if(queryString.contains("&")){
	    	parameters = queryString.split("&");
	    } else{
	    	parameters = queryString.split("%26");
	    }
	    for (String parameter : parameters) {
	        String[] keyValuePair = parameter.split("=");
	        queryParameters.put(keyValuePair[0], keyValuePair[1]);
	    }
	    return queryParameters;
	}
}
