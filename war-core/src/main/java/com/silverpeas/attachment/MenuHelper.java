package com.silverpeas.attachment;

import com.stratelia.silverpeas.util.ResourcesWrapper;
import com.stratelia.webactiv.util.attachment.model.AttachmentDetail;
import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.jsp.JspWriter;

/**
 *
 * @author ehugonnet
 */
public class MenuHelper {

  public static void displayActions(AttachmentDetail attachment, boolean useXMLForm, boolean useFileSharing,
      boolean useWebDAV, String userId, String language, ResourcesWrapper resources,
      String httpServerBase, JspWriter out) throws IOException {
    String attachmentId = attachment.getPK().getId();
    boolean webDavOK = false;
    if (useWebDAV && attachment.isOpenOfficeCompatible()) {
      webDavOK = true;
    }
    out.println("<div id=\"basicmenu" + attachmentId + "\" class=\"yuimenu\">");
    out.println("<div class=\"bd\">");
    out.println("<ul class=\"first-of-type\">");
    out.println("<li class=\"yuimenuitem\"><a class=\"yuimenuitemlabel\" href=\"javascript:checkout(" + attachmentId + "," + webDavOK + ")\">" + resources.
        getString("checkOut") + "</a></li>");
    out.println("<li class=\"yuimenuitem\"><a class=\"yuimenuitemlabel\" href=\"javascript:checkoutAndDownload(" + attachmentId + "," + webDavOK + ")\">" + resources.getString("attachment.checkOutAndDownload") + "</a></li>");
    out.println("<li class=\"yuimenuitem\"><a class=\"yuimenuitemlabel\" href=\"javascript:checkoutAndEdit(" + attachmentId + ")\">" + resources.
        getString("attachment.checkOutAndEditOnline") + "</a></li>");
    out.println("<li class=\"yuimenuitem\"><a class=\"yuimenuitemlabel\" href=\"javascript:checkin(" + attachmentId + "," + attachment.
        isOpenOfficeCompatible() + ", false)\">" + resources.getString("checkIn") + "</a></li>");
    out.println("</ul>");
    out.println("<ul>");
    out.println("<li class=\"yuimenuitem\"><a class=\"yuimenuitemlabel\" href=\"javascript:updateAttachment('" + attachmentId + "')\">" + resources.
        getString("GML.modify") + "</a></li>");
    if (useXMLForm) {
      out.println("<li class=\"yuimenuitem\"><a class=\"yuimenuitemlabel\" href=\"javascript:EditXmlForm('" + attachmentId + "','" + language + "')\">" + resources.getString("attachment.xmlForm.Edit") + "</a></li>");
    }
    out.println(
        "<li class=\"yuimenuitem\"><a class=\"yuimenuitemlabel\" href=\"javascript:deleteAttachment('" + attachment.
        getLogicalName(language) + "'," + attachmentId + ")\">" + resources.getString("GML.delete") + "</a></li>");
    out.println("</ul>");
    if (useFileSharing) {
      out.println("<ul>");
      out.println("<li class=\"yuimenuitem\"><a class=\"yuimenuitemlabel\" href=\"javascript:ShareAttachment('" + attachmentId + "')\">" + resources.
          getString("attachment.share") + "</a></li>");
      out.println("</ul>");
    }
    out.println("</div>");
    out.println("</div>");

    out.println("<script type=\"text/javascript\">");

    out.println("var oMenu" + attachmentId + ";");
    out.println("var webDav" + attachmentId + " = \"" + URLEncoder.encode(httpServerBase + attachment.
        getWebdavUrl(language)) + "\";");
    out.println("YAHOO.util.Event.onContentReady(\"basicmenu" + attachmentId + "\", function () {");
    out.println(
        "oMenu" + attachmentId + " = new YAHOO.widget.ContextMenu(\"basicmenu" + attachmentId + "\", { trigger: \"img_" + attachmentId + "\", hidedelay: 100, effect: {effect: YAHOO.widget.ContainerEffect.FADE, duration: 0.30}});");
    out.println("oMenu" + attachmentId + ".render();");
    if (attachment.isReadOnly()) {
      if (userId.equals(attachment.getWorkerId())) {
        out.println("oMenu" + attachmentId + ".getItem(3).cfg.setProperty(\"disabled\", false);");
      }
      out.println("oMenu" + attachmentId + ".getItem(0).cfg.setProperty(\"disabled\", true);");
      out.println("oMenu" + attachmentId + ".getItem(1).cfg.setProperty(\"disabled\", true);");
      out.println("oMenu" + attachmentId + ".getItem(2).cfg.setProperty(\"disabled\", true);");

      if (useWebDAV && attachment.isOpenOfficeCompatible() && userId.equals(attachment.getWorkerId())) {
        out.println("oMenu" + attachmentId + ".getItem(2).cfg.setProperty(\"disabled\", false);");
      }

      //disable delete
      if (useXMLForm) {
        out.println("oMenu" + attachmentId + ".getItem(2,1).cfg.setProperty(\"disabled\", true);");
      } else {
        out.println("oMenu" + attachmentId + ".getItem(1,1).cfg.setProperty(\"disabled\", true);");
      }

      if (!userId.equals(attachment.getWorkerId())) {
        //disable update
        out.println("oMenu" + attachmentId + ".getItem(0, 1).cfg.setProperty(\"disabled\", true);");

        //disable xmlForm
        if (useXMLForm) {
          out.println("oMenu" + attachmentId + ".getItem(1,1).cfg.setProperty(\"disabled\", true);");
        }
      }
    } else {
      out.println("oMenu" + attachmentId + ".getItem(3).cfg.setProperty(\"disabled\", true);");
    }
    if (!useWebDAV || !attachment.isOpenOfficeCompatible()) {
      out.println("oMenu" + attachmentId + ".getItem(2).cfg.setProperty(\"disabled\", true);");
    }
    out.println(
        "YAHOO.util.Event.addListener(\"basicmenu" + attachmentId + "\", \"mouseover\", oMenu" + attachmentId + ".show);");
    out.println(
        "YAHOO.util.Event.addListener(\"basicmenu" + attachmentId + "\", \"mouseout\", oMenu" + attachmentId + ".hide);");
    out.println("});");
    out.println("</script>");
  }
}