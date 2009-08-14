package com.anhquan.demo;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;

public class HttpPostRequestTester {

	private String requestService = "/";
	private String requestMethod = "POST";
	private String requestQuery = "";
	private int targetPort = 8080;
	private String targetHost = "localhost";
	private int numOfRequests = 1;

	private static final Options OPTS = new Options();

	static {
		OPTS.addOption("h", "host", true, "Target host under the test");
		OPTS.addOption("t", "times", true, "Number of requests");
		OPTS.addOption("p", "port", true, "Target port");
		OPTS.addOption("m", "method", true, "POST or GET");
		OPTS.addOption("s", "service", true, "Path to the service");
		OPTS.addOption("q", "query", true, "Parameters to use the service");
	}

	public static void main(String[] args) {

		// init params
		HttpPostRequestTester mrq = new HttpPostRequestTester();

		mrq.execute(args);
	}

	private CommandLine cmd;

	private void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("mrq", OPTS);
	}

	private void execute(String[] args) {

		CommandLineParser parser = new PosixParser();
		try {
			cmd = parser.parse(OPTS, args);

			if (cmd.hasOption("help")) {
				printUsage();
				return;
			}

			boolean isParamValid = false;
			if (cmd.hasOption("h") && cmd.hasOption("p") && cmd.hasOption("m") && cmd.hasOption("s") && cmd.hasOption("q")) {
				isParamValid = true;
			}
			
			if (isParamValid) {
				targetHost = cmd.hasOption("h") ? cmd.getOptionValue("host") : "localhost";
				targetPort = cmd.hasOption("p") ? Integer.parseInt(cmd.getOptionValue("p")) : 8080;
				requestMethod = cmd.hasOption("m") ? cmd.getOptionValue("m") : "POST";
				requestService = cmd.hasOption("s") ? cmd.getOptionValue("s") : "/";
				numOfRequests = cmd.hasOption("t") ? Integer.parseInt(cmd.getOptionValue("t")) : 1;
				

				try {
					processRequest();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				System.out.println("Parameters are not enough!!!");
				printUsage();
			}

		} catch (ParseException e) {
			printUsage();
		}
	}

	public void processRequest() throws UnknownHostException, IOException, HttpException {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
		HttpProtocolParams.setUseExpectContinue(params, true);

		BasicHttpProcessor httpproc = new BasicHttpProcessor();
		// Required protocol interceptors
		httpproc.addInterceptor(new RequestContent());
		httpproc.addInterceptor(new RequestTargetHost());
		// Recommended protocol interceptors
		httpproc.addInterceptor(new RequestConnControl());
		httpproc.addInterceptor(new RequestUserAgent());
		httpproc.addInterceptor(new RequestExpectContinue());

		HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

		HttpContext context = new BasicHttpContext(null);

		HttpHost host = new HttpHost(targetHost, targetPort);

		DefaultHttpClientConnection conn = new DefaultHttpClientConnection();
		ConnectionReuseStrategy connStrategy = new DefaultConnectionReuseStrategy();

		context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
		context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);

		try {

			HttpEntity[] requestBodies = { new StringEntity("keywords=Harry,Potter", "UTF-8")};
			for (int i = 0; i < numOfRequests; i++) {
				if (!conn.isOpen()) {
					Socket socket = new Socket(host.getHostName(), host.getPort());
					conn.bind(socket, params);
				}
				BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(requestMethod, requestService);
				//request.setEntity(requestBodies[0]);
				System.out.println("[Req#"+i+"] Request URI: " + request.getRequestLine().getUri());

				request.setParams(params);
				httpexecutor.preProcess(request, httpproc, context);
				HttpResponse response = httpexecutor.execute(request, conn, context);
				response.setParams(params);
				httpexecutor.postProcess(response, httpproc, context);

				System.out.println("<< Response: " + response.getStatusLine());
				System.out.println(EntityUtils.toString(response.getEntity()));
				System.out.println("==============");
				if (!connStrategy.keepAlive(response, context)) {
					conn.close();
				} else {
					System.out.println("Connection kept alive...");
				}
			}
		} finally {
			conn.close();
			System.out.println("Connection is closed");
		}
	}
}
