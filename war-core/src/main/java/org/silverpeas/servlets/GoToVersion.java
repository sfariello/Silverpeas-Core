/**
 * Copyright (C) 2000 - 2012 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of the GPL, you may
 * redistribute this Program in connection with Free/Libre Open Source Software ("FLOSS")
 * applications as described in Silverpeas's FLOSS exception. You should have received a copy of the
 * text describing the FLOSS exception, and it is also available here:
 * "http://www.silverpeas.org/docs/core/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.silverpeas.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.silverpeas.attachment.AttachmentServiceFactory;
import org.silverpeas.attachment.model.SimpleDocument;
import org.silverpeas.attachment.model.SimpleDocumentPK;
import org.silverpeas.permalinks.PermalinkServiceFactory;

import com.silverpeas.util.StringUtil;

public class GoToVersion extends GoToDocument {

  private static final long serialVersionUID = 1L;

  @Override
  public String getDestination(String objectId, HttpServletRequest req, HttpServletResponse res)
      throws Exception {
    SimpleDocument document;
    if (StringUtil.isInteger(objectId)) {
      document = PermalinkServiceFactory.getPermalinkCompatibilityService().
          findDocumentVersionByOldId(Integer.parseInt(objectId));
    } else {
      document = AttachmentServiceFactory.getAttachmentService().searchDocumentById(
          new SimpleDocumentPK(objectId), null);
    }
    if (document != null) {
      SimpleDocument version = document.getLastPublicVersion();
      if (version != null) {
        return redirectToFile(version, req, res);
      }
    }
    return null;
  }
}
