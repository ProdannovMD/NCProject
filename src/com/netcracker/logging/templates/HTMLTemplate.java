package com.netcracker.logging.templates;

public class HTMLTemplate {
    private static final String TEMPLATE_HEAD = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>%s</title>\n";

    private static final String TEMPLATE_TAIL = "    <style>\n" +
            "        body {\n" +
            "            background: #fafafa url(https://jackrugile.com/images/misc/noise-diagonal.png);\n" +
            "            color: #444;\n" +
            "            font: 100%/30px 'Helvetica Neue', helvetica, arial, sans-serif;\n" +
            "            text-shadow: 0 1px 0 #fff;\n" +
            "        }\n" +
            "\n" +
            "        strong {\n" +
            "            font-weight: bold;\n" +
            "        }\n" +
            "\n" +
            "        em {\n" +
            "            font-style: italic;\n" +
            "        }\n" +
            "\n" +
            "        table {\n" +
            "            background: #f5f5f5;\n" +
            "            border-collapse: separate;\n" +
            "            box-shadow: inset 0 1px 0 #fff;\n" +
            "            font-size: 12px;\n" +
            "            line-height: 24px;\n" +
            "            margin: 30px auto;\n" +
            "            text-align: left;\n" +
            "            max-width: available;\n" +
            "        }\n" +
            "\n" +
            "        th {\n" +
            "            background: url(https://jackrugile.com/images/misc/noise-diagonal.png), linear-gradient(#777, #444);\n" +
            "            border-left: 1px solid #555;\n" +
            "            border-right: 1px solid #777;\n" +
            "            border-top: 1px solid #555;\n" +
            "            border-bottom: 1px solid #333;\n" +
            "            box-shadow: inset 0 1px 0 #999;\n" +
            "            color: #fff;\n" +
            "            font-weight: bold;\n" +
            "            padding: 10px 15px;\n" +
            "            position: relative;\n" +
            "            text-shadow: 0 1px 0 #000;\n" +
            "        }\n" +
            "\n" +
            "        th:after {\n" +
            "            background: linear-gradient(rgba(255,255,255,0), rgba(255,255,255,.08));\n" +
            "            content: '';\n" +
            "            display: block;\n" +
            "            height: 25%;\n" +
            "            left: 0;\n" +
            "            margin: 1px 0 0 0;\n" +
            "            position: absolute;\n" +
            "            top: 25%;\n" +
            "            width: 100%;\n" +
            "        }\n" +
            "\n" +
            "        th:first-child {\n" +
            "            border-left: 1px solid #777;\n" +
            "            box-shadow: inset 1px 1px 0 #999;\n" +
            "        }\n" +
            "\n" +
            "        th:last-child {\n" +
            "            box-shadow: inset -1px 1px 0 #999;\n" +
            "        }\n" +
            "\n" +
            "        td {\n" +
            "            border-right: 1px solid #fff;\n" +
            "            border-left: 1px solid #e8e8e8;\n" +
            "            border-top: 1px solid #fff;\n" +
            "            border-bottom: 1px solid #e8e8e8;\n" +
            "            padding: 10px 15px;\n" +
            "            position: relative;\n" +
            "            transition: all 300ms;\n" +
            "        }\n" +
            "\n" +
            "        td:first-child {\n" +
            "            box-shadow: inset 1px 0 0 #fff;\n" +
            "        }\n" +
            "\n" +
            "        td:last-child {\n" +
            "            border-right: 1px solid #e8e8e8;\n" +
            "            box-shadow: inset -1px 0 0 #fff;\n" +
            "        }\n" +
            "\n" +
            "        tr {\n" +
            "            background: url(https://jackrugile.com/images/misc/noise-diagonal.png);\n" +
            "        }\n" +
            "\n" +
            "        tr:nth-child(odd) td {\n" +
            "            background: #f1f1f1 url(https://jackrugile.com/images/misc/noise-diagonal.png);\n" +
            "        }\n" +
            "\n" +
            "        tr:last-of-type td {\n" +
            "            box-shadow: inset 0 -1px 0 #fff;\n" +
            "        }\n" +
            "\n" +
            "        tr:last-of-type td:first-child {\n" +
            "            box-shadow: inset 1px -1px 0 #fff;\n" +
            "        }\n" +
            "\n" +
            "        tr:last-of-type td:last-child {\n" +
            "            box-shadow: inset -1px -1px 0 #fff;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "<table>\n" +
            "    <tr>\n" +
            "        <th>DateTime</th>\n" +
            "        <th>Thread</th>\n" +
            "        <th>Level</th>\n" +
            "        <th>Name</th>\n" +
            "        <th>Message</th>\n" +
            "        <th>Throwable</th>\n" +
            "    </tr>\n" +
            "</table>\n" +
            "</body>\n" +
            "</html>";

    public static String getTemplate(String title) {
        return String.format(TEMPLATE_HEAD, title) + TEMPLATE_TAIL;
    }
}
