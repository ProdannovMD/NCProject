package com.netcracker.logging.configuration.parsers;

import com.netcracker.logging.configuration.Configuration;
import com.netcracker.logging.filters.Filter;
import com.netcracker.logging.filters.impl.LevelFilter;
import com.netcracker.logging.filters.impl.LevelFilterModes;
import com.netcracker.logging.handlers.Handler;
import com.netcracker.logging.handlers.impl.ConsoleHandler;
import com.netcracker.logging.handlers.impl.FileHandler;
import com.netcracker.logging.handlers.impl.HTMLHandler;
import com.netcracker.logging.handlers.impl.MailHandler;
import com.netcracker.logging.handlers.layouts.HTMLLayout;
import com.netcracker.logging.handlers.layouts.PatternLayout;
import com.netcracker.logging.levels.Level;
import com.netcracker.logging.loggers.Logger;
import com.netcracker.logging.loggers.LoggerFiltersModes;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class XMLConfigurationParser {
    private static final Logger LOGGER = Configuration.DEFAULT_CONFIGURATION.getLogger("Configuration");

    public static Configuration parse(Path path) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Map<String, Handler> handlers = new HashMap<>();
        Map<String, Filter> filters = new HashMap<>();
        Map<String, Logger> loggers = new HashMap<>();


        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(path.toFile());
            doc.normalizeDocument();
            NodeList configurationNodes = doc.getElementsByTagName("Configuration");
            if (configurationNodes.getLength() < 1) {
                throw new ParserConfigurationException();
            }
            NodeList childNodes = configurationNodes.item(0).getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                if (child.getNodeName().equals("Handlers")) {
                    NodeList handlerNodes = child.getChildNodes();
                    for (int j = 0; j < handlerNodes.getLength(); j++) {
                        Node handlerNode = handlerNodes.item(j);
                        switch (handlerNode.getNodeName()) {
                            case "Console":
                                parseConsoleHandler(handlerNode, handlers);
                                break;

                            case "File":
                                parseFileHandler(handlerNode, handlers);
                                break;

                            case "Mail":
                                parseMailHandler(handlerNode, handlers);
                                break;

                            case "HTML":
                                parseHTMLHandler(handlerNode, handlers);
                                break;

                            default:
                        }
                    }
                }

                if (child.getNodeName().equals("Filters")) {
                    NodeList filterNodes = child.getChildNodes();
                    for (int j = 0; j < filterNodes.getLength(); j++) {
                        Node filterNode = filterNodes.item(j);
                        switch (filterNode.getNodeName()) {
                            case "Level":
                                parseLevelFilter(filterNode, filters);
                                break;

                            default:
                        }
                    }
                }
            }

            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                if (child.getNodeName().equals("Loggers")) {
                    NodeList loggerNodes = child.getChildNodes();
                    for (int j = 0; j < loggerNodes.getLength(); j++) {
                        Node loggerNode = loggerNodes.item(j);

                        if (loggerNode.getNodeName().equals("Logger")) {
                            parseLogger(handlers, filters, loggers, loggerNode);
                        }
                    }
                }
            }

            return new Configuration(loggers);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            return null;
        }
    }

    private static void parseLogger(
            Map<String, Handler> handlers, Map<String, Filter> filters, Map<String, Logger> loggers, Node loggerNode
    ) {
        Node loggerName = loggerNode.getAttributes().getNamedItem("name");
        Node filtersMode = loggerNode.getAttributes().getNamedItem("filtersMode");
        if (loggerName == null)
            return;

        LoggerFiltersModes mode = LoggerFiltersModes.AND;
        if (filtersMode != null) {
            try {
                mode = LoggerFiltersModes.valueOf(filtersMode.getNodeValue().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) { }
        }

        List<Handler> loggerHandlers = new ArrayList<>();
        List<Filter> loggerFilters = new ArrayList<>();

        NodeList loggerRefNodes = loggerNode.getChildNodes();
        for (int k = 0; k < loggerRefNodes.getLength(); k++) {
            Node refNode = loggerRefNodes.item(k);
            switch (refNode.getNodeName()) {
                case "HandlerRef":
                    Node handlerRef = refNode.getAttributes().getNamedItem("ref");
                    if (handlerRef == null)
                        continue;
                    String handlerRefValue = handlerRef.getNodeValue();
                    if (handlers.containsKey(handlerRefValue))
                        loggerHandlers.add(handlers.get(handlerRefValue));
                    break;

                case "FilterRef":
                    Node filterRef = refNode.getAttributes().getNamedItem("ref");
                    if (filterRef == null)
                        continue;
                    String filterRefValue = filterRef.getNodeValue();
                    if (filters.containsKey(filterRefValue))
                        loggerFilters.add(filters.get(filterRefValue));
                    break;

                default:
            }
        }

        Logger logger = new Logger(loggerName.getNodeValue(), loggerHandlers, loggerFilters, mode);
        loggers.put(loggerName.getNodeValue(), logger);
    }

    private static void parseLevelFilter(Node filterNode, Map<String, Filter> filters) {
        Node filterName = filterNode.getAttributes().getNamedItem("name");
        Node filterLevel = filterNode.getAttributes().getNamedItem("level");
        Node filterMode = filterNode.getAttributes().getNamedItem("mode");
        Level level = null;
        if (filterName == null)
            return;

        if (filterLevel == null) {
            LOGGER.warn(
                    "Warning for filter " +
                            filterName.getNodeValue() +
                            ": no level specified, using default level"
            );
        } else {
            try {
                level = Level.valueOf(filterLevel.getNodeValue().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                LOGGER.warn(
                        "Warning for filter " +
                                filterName.getNodeValue() +
                                ": unsupported level value, using default level"
                );
            }
        }

        LevelFilterModes mode = null;
        if (filterMode != null) {
            try {
                mode = LevelFilterModes.valueOf(filterMode.getNodeValue().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
            }
        }

        filters.put(filterName.getNodeValue(), new LevelFilter(level, mode));

    }

    private static void parseConsoleHandler(Node handlerNode, Map<String, Handler> handlers) {
        PatternLayout layout = parsePatternLayout(handlerNode);

        Node handlerName = handlerNode.getAttributes().getNamedItem("name");
        if (handlerName == null)
            return;

        Handler consoleHandler = new ConsoleHandler(layout);
        handlers.put(handlerName.getNodeValue(), consoleHandler);
    }

    private static void parseFileHandler(Node handlerNode, Map<String, Handler> handlers) {
        PatternLayout layout = parsePatternLayout(handlerNode);

        Node handlerName = handlerNode.getAttributes().getNamedItem("name");
        if (handlerName == null)
            return;

        Node fileName = handlerNode.getAttributes().getNamedItem("fileName");
        if (fileName == null) {
            LOGGER.error(
                    "Error with handler " +
                            handlerName.getNodeValue() +
                            ": no file name specified"
            );
            return;
        }

        try {
            Handler fileHandler = new FileHandler(fileName.getNodeValue(), layout);
            handlers.put(handlerName.getNodeValue(), fileHandler);
        } catch (IOException e) {
            LOGGER.error(
                    "Error with handler " +
                            handlerName.getNodeValue() +
                            ": cannot write to file " +
                            fileName.getNodeValue()
            );
        }
    }

    private static void parseMailHandler(Node handlerNode, Map<String, Handler> handlers) {
        PatternLayout layout = parsePatternLayout(handlerNode);

        Node handlerName = handlerNode.getAttributes().getNamedItem("name");
        if (handlerName == null)
            return;

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.subject", "Application Logs");
        parseProperties(handlerNode, properties);

        Handler mailHandler = new MailHandler(properties, layout);
        handlers.put(handlerName.getNodeValue(), mailHandler);

    }

    private static void parseHTMLHandler(Node handlerNode, Map<String, Handler> handlers) {
        HTMLLayout layout = parseHTMLLayout(handlerNode);

        Node handlerName = handlerNode.getAttributes().getNamedItem("name");
        if (handlerName == null)
            return;

        Node fileName = handlerNode.getAttributes().getNamedItem("fileName");
        if (fileName == null) {
            LOGGER.error(
                    "Error with handler " +
                            handlerName.getNodeValue() +
                            ": no file name specified"
            );
            return;
        }

        try {
            Handler fileHandler = new HTMLHandler(fileName.getNodeValue(), layout);
            handlers.put(handlerName.getNodeValue(), fileHandler);
        } catch (IOException e) {
            LOGGER.error(
                    "Error with handler " +
                            handlerName.getNodeValue() +
                            ": cannot write to file " +
                            fileName.getNodeValue()
            );
        }
    }

    private static void parseProperties(Node handlerNode, Properties properties) {
        NodeList childNodes = handlerNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeName().equals("Properties")) {
                NodeList propertyNodes = childNode.getChildNodes();
                for (int j = 0; j < propertyNodes.getLength(); j++) {
                    Node propertyNode = propertyNodes.item(j);
                    if (propertyNode.getNodeName().equals("Property")) {
                        Node propName = propertyNode.getAttributes().getNamedItem("name");
                        Node propValue = propertyNode.getAttributes().getNamedItem("value");
                        if (propName == null || propValue == null)
                            continue;

                        properties.put(propName.getNodeValue(), propValue.getNodeValue());
                    }
                }

            }
        }
    }

    private static PatternLayout parsePatternLayout(Node handlerNode) {
        PatternLayout layout = PatternLayout.DEFAULT;
        NodeList childNodes = handlerNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeName().equals("PatternLayout")) {
                Node patternAttr = childNode.getAttributes().getNamedItem("pattern");
                if (patternAttr == null) {
                    layout = PatternLayout.DEFAULT;
                } else {
                    layout = new PatternLayout(patternAttr.getNodeValue());
                }
            }
        }
        return layout;
    }

    private static HTMLLayout parseHTMLLayout(Node handlerNode) {
        HTMLLayout layout = HTMLLayout.DEFAULT;
        NodeList childNodes = handlerNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeName().equals("HTMLLayout")) {
                Node htmlAttr = childNode.getAttributes().getNamedItem("title");
                Node dateTimePatternAttr = childNode.getAttributes().getNamedItem("dateTimePattern");
                String pattern = dateTimePatternAttr == null ? null : dateTimePatternAttr.getNodeValue();
                if (htmlAttr == null) {
                    layout = HTMLLayout.DEFAULT;
                } else {
                    layout = new HTMLLayout(htmlAttr.getNodeValue(), pattern);
                }
            }
        }
        return layout;
    }
}
