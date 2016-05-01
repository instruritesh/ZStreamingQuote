package com.ritesh.zstreamingquote.webservice.jetty.handlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.ritesh.zstreamingquote.config.ZStreamingConfig;
import com.ritesh.zstreamingquote.control.ZStreamingQuoteControl;
import com.ritesh.zstreamingquote.quote.StreamingQuote;
import com.ritesh.zstreamingquote.quote.mode.StreamingQuoteModeFull;
import com.ritesh.zstreamingquote.quote.mode.StreamingQuoteModeLtp;
import com.ritesh.zstreamingquote.quote.mode.StreamingQuoteModeQuote;

public class TimeRangeStreamingQuoteActionHandler extends ContextHandler {
	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private TimeZone timeZone = TimeZone.getTimeZone("IST");

	/**
	 * constructor
	 */
	public TimeRangeStreamingQuoteActionHandler() {
		setContextPath(ZStreamingConfig.getJettyServerTimeRangeStreamingQuoteURL());
		dateFormat.setTimeZone(timeZone);
	}

	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String reqMethod = request.getMethod();
		
		//map request params to values
		Map<String, String> queryValMap = getQueryParameters(request);

		// Get the requested format for data
		String dataFormat = queryValMap.get(ZStreamingConfig.getJettyServerTimeRangeStreamingQuoteformatReqParam());
		// Get Time Range requested
		String fromTime = queryValMap.get(ZStreamingConfig.getJettyServerTimeRangeStreamingQuotefromTimeReqParam());
		String toTime = queryValMap.get(ZStreamingConfig.getJettyServerTimeRangeStreamingQuotetoTimeReqParam());
		String instrumentToken = queryValMap.get(ZStreamingConfig.getJettyServerTimeRangeStreamingQuoteinstrumentReqParam());

		if (reqMethod.equals("GET")) {
			// GET method
			if(ZStreamingConfig.isWebServiceLogsPrintable()){
				System.out.println(
					"TimeRangeStreamingQuoteActionHandler.doHandle(): ZStreamingQuote Time Range Streaming Quote [GET]: "
							+ "Requested Format: " + "[" + dataFormat + "] fromTime: [" + fromTime + "] toTime: ["
							+ toTime + "] instrumentToken: [" + instrumentToken + "] - [" + dateFormat.format(Calendar.getInstance(timeZone).getTime()) + "]");
			}

			List<StreamingQuote> quoteList = ZStreamingQuoteControl.getInstance()
					.getQuoteListByTimeRange(instrumentToken, fromTime, toTime);
			String outData = null;
			if (quoteList == null) {
				outData = "<h1>Requested Data could NOT be fetched, may be DB problem</h1>";
			} else {
				if (dataFormat.equals("json")) {
					// format JSON
					outData = formatQuoteListToJSON(quoteList);
				}/* else if (dataFormat.equals("csv")) {
					// format CSV
					outData = formatQuoteListToCSV(quoteList);
				}*/ else {
					outData = "<h1>Requested Format " + dataFormat + " NOT supported, only csv or json</h1>";
					System.out.println(
							"TimeRangeStreamingQuoteActionHandler.doHandle(): ERROR: [" + dataFormat + "] NOT Supported");
				}
			}

			// write HTTP o/p back to client
			PrintWriter out = response.getWriter();
			out.println(outData);
		} else if (reqMethod.equals("POST")) {
			// POST method not handled currently
		} else {
			// Default - other handlers not supported
			System.out.println("TimeRangeStreamingQuoteActionHandler.doHandle(): ERROR: Request method not proper: " + reqMethod);
		}

		response.setContentType("text/html; charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);

		baseRequest.setHandled(true);
	}

	/**
	 * formatQuoteListToJSON - convert quote list to JSON
	 * 
	 * @param quote list
	 * @return JSON formatted quote list
	 */
	private String formatQuoteListToJSON(List<StreamingQuote> quoteList) {
		String jsonData = null;
		ObjectMapper mapper = new ObjectMapper();

		try {
			jsonData = mapper.writeValueAsString(quoteList);
		} catch (JsonProcessingException e) {
			System.out.println(
					"TimeRangeStreamingQuoteActionHandler.formatQuoteListToJSON(): ERROR: JsonProcessingException on quote list !!!");
			e.printStackTrace();
		}

		return jsonData;
	}

	/**
	 * formatQuoteListToCSV - convert quote list to CSV
	 * 
	 * @param quote list
	 * @return CSV formatted Quote list
	 */
	private String formatQuoteListToCSV(List<StreamingQuote> quoteList) {
		String csvData = null;
		CsvMapper mapper = new CsvMapper();
		CsvSchema schema = null;
		
		if(quoteList.get(0).getClass() == StreamingQuoteModeLtp.class){
			schema = mapper.schemaFor(StreamingQuoteModeLtp.class).withHeader().withColumnSeparator(',');
		} else if(quoteList.get(0).getClass() == StreamingQuoteModeQuote.class){
			schema = mapper.schemaFor(StreamingQuoteModeQuote.class).withHeader().withColumnSeparator(',');
		} else if(quoteList.get(0).getClass() == StreamingQuoteModeFull.class){
			schema = mapper.schemaFor(StreamingQuoteModeFull.class).withHeader().withColumnSeparator(',');
		} else{
			System.out.println("TimeRangeStreamingQuoteActionHandler.formatQuoteListToCSV(): ERROR: Wrong POJO class to map");
		}

		try {
			csvData = mapper.writer(schema).writeValueAsString(quoteList);
		} catch (JsonProcessingException e) {
			System.out
					.println("TimeRangeStreamingQuoteActionHandler.formatQuoteListToCSV(): ERROR: JsonProcessingException on quote list !!!");
			e.printStackTrace();
		}

		return csvData;
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
	    	System.out.println("TimeRangeStreamingQuoteActionHandler.getQueryParameters(): ERROR: query string is empty !!!");
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
