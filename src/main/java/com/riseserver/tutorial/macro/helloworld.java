package com.riseserver.tutorial.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class helloworld implements Macro {

    private PageBuilderService pageBuilderService;

    @Autowired
    public helloworld(@ComponentImport PageBuilderService pageBuilderService) {
        this.pageBuilderService = pageBuilderService;
    }


    public String execute(Map<String, String> map, String s, ConversionContext conversionContext) throws MacroExecutionException {

        pageBuilderService.assembler().resources().requireWebResource("com.atlassian.tutorial.myConfluenceMacro:myConfluenceMacro-resources");

        String output = "<div class =\"helloworld\">";
        output = output + "<div class = \"" + map.get("Color") + "\">";


        if (map.get("Name") != null) {
            output = output + ("<h1>Hello " + map.get("Name") + "!</h1>");
        } else {
            output = output + "<h1>Hello World!<h1>";
        }

        output = output + "</div>" + "</div>";

        return output;
    }

    public BodyType getBodyType() { return BodyType.NONE; }

    public OutputType getOutputType() { return OutputType.BLOCK; }
}
