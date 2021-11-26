package com.netcracker.logging.configuration.parsers;

import com.netcracker.logging.configuration.Configuration;
import com.netcracker.logging.filters.Filter;
import com.netcracker.logging.filters.impl.LevelFilter;
import com.netcracker.logging.handlers.Handler;
import com.netcracker.logging.handlers.impl.ConsoleHandler;
import com.netcracker.logging.handlers.layouts.PatternLayout;
import com.netcracker.logging.levels.Level;
import com.netcracker.logging.loggers.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class XMLConfigurationParser {
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
                                Node filterName = filterNode.getAttributes().getNamedItem("name");
                                Node filterLevel = filterNode.getAttributes().getNamedItem("level");
                                Level level = null;
                                if (filterName == null)
                                    continue;
                                if (filterLevel != null) {
                                    try {
                                        level = Level.valueOf(filterLevel.getNodeValue().toUpperCase(Locale.ROOT));
                                    } catch (IllegalArgumentException ignored) { }
                                }

                                if (level == null)
                                    filters.put(filterName.getNodeValue(), new LevelFilter());
                                else
                                    filters.put(filterName.getNodeValue(), new LevelFilter(level));

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
                            Node loggerName = loggerNode.getAttributes().getNamedItem("name");
                            if (loggerName == null)
                                continue;

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

                            Logger logger = new Logger(loggerName.getNodeValue(), loggerHandlers, loggerFilters);
                            loggers.put(loggerName.getNodeValue(), logger);
                        }
                    }
                }
            }

//            System.out.println(loggers);

            return new Configuration(loggers);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            return null;
        }
    }

    private static void parseConsoleHandler(Node handlerNode, Map<String, Handler> handlers) {
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

        Node handlerName = handlerNode.getAttributes().getNamedItem("name");
        if (handlerName == null)
            return;

        ConsoleHandler consoleHandler = new ConsoleHandler(layout);
        handlers.put(handlerName.getNodeValue(), consoleHandler);
    }

    public static void main(String[] args) {
        parse(Paths.get(".", "resources", "log-config.xml"));
    }
}
