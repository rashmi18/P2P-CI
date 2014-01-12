package ingle.p2pci.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageParser {

	public RequestHandler parse(BufferedReader input) throws IOException {
		String inputLine;
		// System.out.println("received" + sample);
		Request request = new Request();
		RequestHandler requestHandler = null;
		Pattern pattern;
		Matcher matcher;
		String regex;

		inputLine = input.readLine();
		System.out.println(inputLine);
		regex = new String("^([^ ]+) ([A-Z]*?) *?([0-9]*?) ([^ ]+)$");
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(inputLine);
		if (matcher.find()) {
			// System.out.println(matcher.group(1));

			request.setRequestType(matcher.group(1));
			request.setVersion(matcher.group(4));
			System.out.println(request.getRequestType());
			if (request.getRequestType().equals("LIST")) {
				requestHandler = new ListRequestHandler();
				requestHandler.setRequest(request);

			}
			if (request.getRequestType().equals("ADD")) {
				request.setRFCNo(Integer.parseInt(matcher.group(3)));
				requestHandler = new AddRequestHandler();
				requestHandler.setRequest(request);
			}
			if (request.getRequestType().equals("LOOKUP")) {
				request.setRFCNo(Integer.parseInt(matcher.group(3)));
				requestHandler = new LookupRequestHandler();
				requestHandler.setRequest(request);
			}

		}

		inputLine = input.readLine();
		regex = "^([^ ]+): (.*)$";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(inputLine);

		if (matcher.find()) {
			if (matcher.group(1).equalsIgnoreCase("host")) {
				request.setHost(matcher.group(2));
			}
		}
		inputLine = input.readLine();
		matcher = pattern.matcher(inputLine);

		if (matcher.find()) {

			if (matcher.group(1).equalsIgnoreCase("port")) {
				request.setPort(Integer.parseInt(matcher.group(2)));
			}
		}

		if (!request.getRequestType().equals("LIST")) {
			inputLine = input.readLine();
			matcher = pattern.matcher(inputLine);
			if (matcher.find()) {
				if (matcher.group(1).equalsIgnoreCase("title")) {
					request.setTitle(matcher.group(2));
				}

			}

		}

		printRFCInfo(request);
		return requestHandler;

	}

	public void printRFCInfo(Request request) {

		System.out.println("Request is:" + request.getRequestType());
		System.out.println("RFC is:" + request.getRFCNo());
		System.out.println("Host is:" + request.getHost());
		System.out.println("Port is:" + request.getPort());
		System.out.println("Title is:" + request.getTitle());
		System.out.println("Version is:" + request.getVersion());

	}

}
