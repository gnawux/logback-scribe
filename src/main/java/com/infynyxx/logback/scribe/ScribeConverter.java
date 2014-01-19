package com.infynyxx.logback.scribe;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;

/**
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class ScribeConverter {
    private final String facility;
    private final Map<String, String> additionalFields;
    private final String hostName;

	private DateFormat df;

    public ScribeConverter(String facility, Map<String, String> additionalFields, String hostName) {
        this.facility = facility;
        this.additionalFields = additionalFields;
        this.hostName = hostName;
		this.df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
    }

    public String getMessage(ILoggingEvent eventObject) {

        String message = String.format("%s %s [%s] - %s %s", getHostName(), this.df.format(new Date(eventObject.getTimeStamp())), eventObject.getLevel(), eventObject.getThreadName(), eventObject.getMessage());

        // Format up the stack trace
        IThrowableProxy proxy = eventObject.getThrowableProxy();

        if (proxy != null) {
            message += "\n" + proxy.getClassName() + ": " + proxy.getMessage() + "\n" + toStackTraceString(proxy.getStackTraceElementProxyArray());
        }

        return message;
    }

    private String toStackTraceString(StackTraceElementProxy[] elements) {
        StringBuilder str = new StringBuilder();
        for (StackTraceElementProxy element : elements) {
            str.append(element.getSTEAsString());
        }
        return str.toString();
    }

    private static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "UNKNOWN_HOST";
        }

    }
}
