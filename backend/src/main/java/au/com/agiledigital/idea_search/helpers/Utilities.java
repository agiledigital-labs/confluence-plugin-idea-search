package au.com.agiledigital.idea_search.helpers;


import au.com.agiledigital.idea_search.dao.AoFedexIdea;
import au.com.agiledigital.idea_search.model.FedexIdea;
import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.user.UserKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.atlassian.confluence.user.UserAccessor;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import static au.com.agiledigital.idea_search.helpers.PageHelper.wrapBody;

public class Utilities {
  private static final Logger log = LoggerFactory.getLogger(Utilities.class);
  private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

  private Utilities() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Extract form data string from macro
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
   * @param page that the data macro is on
   * @return a FedexIdea from the page data.
   */
  @Nonnull
  public static FedexIdea fedexIdeaFromPage(AbstractPage page) {

    BodyContent content = page.getBodyContent();

    try {
      Document bodyParsed = parseXML(wrapBody(content.getBody()));
      NodeList macros = bodyParsed.getElementsByTagName("ac:structured-macro");

      String formData = getFormData(macros);

      return new FedexIdea.Builder().withTitle(page.getTitle())
        .withFormData(formData)
        .withContentId(page.getContentId())
        .withCreator(page.getCreator())
        .build();


    } catch (ParserConfigurationException | IOException | SAXException e) {
      log.warn(e.toString());
    }

    return new FedexIdea.Builder().build();
  }

  /**
   * Convert fedex idea active object to a fedex idea model object
   *
   * @param aoFedexIdea active object to be converted
   * @return FedexIdea object
   */
  public static FedexIdea asFedexIdea(AoFedexIdea aoFedexIdea, PageService pageService, ConfluenceUser user ) {
    try {
      return new FedexIdea.Builder()
        .withGlobalId(aoFedexIdea.getGlobalId())
        .withTitle(aoFedexIdea.getTitle())
        .withContentId(pageService.getIdPageLocator(aoFedexIdea.getContentId()).getPage().getContentId())
        .withCreator(user)
        .withFormData(aoFedexIdea.getFormData())
        .build();
    } catch (NullPointerException nullPointerException){
      return new FedexIdea.Builder().build();
    }
  }

  /**
   * Convert fedex idea active object to a fedex idea model object
   *
   * @param aoFedexIdea active object to be converted
   * @return FedexIdea object
   */
  public static FedexIdea asFedexIdea(AoFedexIdea aoFedexIdea, PageService pageService, ConfluenceUser user, List<String> indexData ) {
    try {
      return new FedexIdea.Builder()
        .withGlobalId(aoFedexIdea.getGlobalId())
        .withTitle(aoFedexIdea.getTitle())
        .withContentId(pageService.getIdPageLocator(aoFedexIdea.getContentId()).getPage().getContentId())
        .withCreator(user)
        .withFormData(aoFedexIdea.getFormData())
        .withIndexData(indexData)
        .build();
    } catch (NullPointerException nullPointerException){
      return new FedexIdea.Builder().build();
    }
  }

  /**
   * Convert a user key ID to the users name
   *
   * @param userKey string of the user key id
   * @return userName string
   */
  public static ConfluenceUser getUsername(String userKey, UserAccessor userAccessor) {
    if (userKey != null) {
      return userAccessor.getUserByKey(new UserKey(userKey));
    }

    return null;
  }


}
