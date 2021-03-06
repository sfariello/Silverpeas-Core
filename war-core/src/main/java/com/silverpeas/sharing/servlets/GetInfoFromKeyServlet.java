/**
* Copyright (C) 2000 - 2012 Silverpeas
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* As a special exception to the terms and conditions of version 3.0 of
* the GPL, you may redistribute this Program in connection with Free/Libre
* Open Source Software ("FLOSS") applications as described in Silverpeas's
* FLOSS exception. You should have received a copy of the text describing
* the FLOSS exception, and it is also available here:
* "http://www.silverpeas.org/docs/core/legal/floss_exception.html"
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package com.silverpeas.sharing.servlets;

import com.silverpeas.look.SilverpeasLook;
import com.silverpeas.sharing.model.NodeTicket;
import com.silverpeas.sharing.model.SimpleFileTicket;
import com.silverpeas.sharing.model.Ticket;
import com.silverpeas.sharing.model.VersionFileTicket;
import com.silverpeas.sharing.services.SharingServiceFactory;
import com.stratelia.silverpeas.peasCore.URLManager;
import com.stratelia.webactiv.beans.admin.ComponentInstLight;
import com.stratelia.webactiv.util.FileRepositoryManager;
import com.stratelia.webactiv.util.ResourceLocator;
import org.silverpeas.core.admin.OrganisationControllerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.silverpeas.attachment.model.HistorisedDocument;
import org.silverpeas.attachment.model.SimpleDocument;

import static com.silverpeas.sharing.servlets.FileSharingConstants.*;

public class GetInfoFromKeyServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private static final ResourceLocator settings = new ResourceLocator("org.silverpeas.sharing.settings.sharing", "");

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String token = request.getParameter(PARAM_KEYFILE);
    Ticket ticket = SharingServiceFactory.getSharingTicketService().getTicket(token);
    request.setAttribute(ATT_TICKET, ticket);
    if (ticket == null || !ticket.isValid()) {
      getServletContext().getRequestDispatcher("/sharing/jsp/invalidTicket.jsp").forward(
          request, response);
    } else if (ticket instanceof NodeTicket) {
      String url = getURLForFolderSharing(request, token);
      response.sendRedirect(url);
    } else {
      if (ticket instanceof SimpleFileTicket) {
        SimpleDocument attachment = ((SimpleFileTicket) ticket).getResource().getAccessedObject();
        request.setAttribute("fileIcon", attachment.getDisplayIcon());
        request.setAttribute("fileSize", FileRepositoryManager.formatFileSize(attachment.getSize()));
      } else if (ticket instanceof VersionFileTicket) {
        HistorisedDocument document = ((VersionFileTicket) ticket).getResource().getAccessedObject();
        SimpleDocument version = document.getPublicVersions().iterator().next();
        request.setAttribute("fileIcon", version.getDisplayIcon());
        request.setAttribute("fileSize", FileRepositoryManager.formatFileSize(version.getSize()));
      }
      request.setAttribute(ATT_WALLPAPER, getWallpaperFor(ticket));
      request.setAttribute(ATT_KEYFILE, token);
      getServletContext().getRequestDispatcher("/sharing/jsp/displayTicketInfo.jsp").forward(
          request, response);
    }
  }

  /**
* Gets the wallpaper of the space to which the component corresponding to the ticket belongs. The
* wallpaper is fetched from the direct space of the component upto the first parent space that
* have a specific wallpapers.
*
* @return the URL of the wallpaper.
*/
  private String getWallpaperFor(final Ticket ticket) {
    ComponentInstLight component = OrganisationControllerFactory.getOrganisationController()
        .getComponentInstLight(ticket.getComponentId());
    return SilverpeasLook.getSilverpeasLook().getWallpaperOfSpaceOrDefaultOne(component.
        getDomainFatherId());
  }

  private String getURLForFolderSharing(HttpServletRequest request, String token) {
    String url = settings.getString("sharing.folder.webapp");
    if (!url.startsWith("http")) {
      url = URLManager.getServerURL(request) + url;
    }
    return url + "?" + token;
  }
}