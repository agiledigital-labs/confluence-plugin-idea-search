package au.com.agiledigital.idea_search.helpers;


import au.com.agiledigital.idea_search.model.FedexIdea;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.setup.settings.SettingsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static au.com.agiledigital.idea_search.helpers.PageHelper.wrapBody;

public class Utilities {
  private static final Logger log = LoggerFactory.getLogger(Utilities.class);
  private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

  private Utilities() {throw new IllegalStateException("Utility class"); }

  /**
   * Extract form data string form macro
   *
   * @param macros NodeList of macro elements
   * @return the data from a idea-structured-data macro
   */
  public static String getFormData(NodeList macros) {
    for (int i = 0; i < macros.getLength(); i++) {
      Node node = macros.item(i);

      String nodeName = node.getAttributes().getNamedItem("ac:name").getNodeValue();
      if (nodeName.equals("idea-structured-data")) {
        return node.getTextContent();
      }
    }

    return null;
  }


  /**
   * Parses XML to Java Dom objects
   *
   * @param xml string of read-in XML content
   * @return Object containing the structure of the XML which has functionality for navigating the
   * dom
   */
  private static Document parseXML(String xml)
    throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

    return builder.parse(new ByteArrayInputStream(xml.getBytes()));
  }


  /**
   * Extracts the formData from the macro object in the confluence page
   *
   * @param settingsManager from confluence
   * @param page that the data macro is on
   * @return a FedexIdea from the page data.
   */
  @Nonnull
  public static FedexIdea getPageData(SettingsManager settingsManager, AbstractPage page) {

      BodyContent content = page.getBodyContent();

      try {
        Document bodyParsed = parseXML(wrapBody(content.getBody()));
        NodeList macros = bodyParsed.getElementsByTagName("ac:structured-macro");

        String formData = getFormData(macros);

        return new FedexIdea.Builder().withTitle(page.getTitle())
          .withUrl(settingsManager.getGlobalSettings().getBaseUrl() + page.getUrlPath())
          .withFormData(formData)
          .withContentId(page.getContentId())
          .withCreator(page.getCreator())
          .build();


      } catch (ParserConfigurationException | IOException | SAXException e) {
        log.warn(e.toString());
      }

      return new FedexIdea.Builder().build();
    }
}
