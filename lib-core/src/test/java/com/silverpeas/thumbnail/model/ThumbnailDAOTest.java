/**
 * Copyright (C) 2000 - 2009 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://repository.silverpeas.com/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.silverpeas.thumbnail.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.dbunit.JndiBasedDBTestCase;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

/**
 *
 * @author srochet
 */
public class ThumbnailDAOTest extends JndiBasedDBTestCase {

  public ThumbnailDAOTest() {
  }

  private String jndiName = "";

  protected void setUp() throws Exception {
    Hashtable<String, String> env = new Hashtable<String, String>();
    env.put(Context.INITIAL_CONTEXT_FACTORY,
        "com.sun.jndi.fscontext.RefFSContextFactory");
    InitialContext ic = new InitialContext(env);
    Properties props = new Properties();
    props.load(ThumbnailDAO.class.getClassLoader().getResourceAsStream(
        "jdbc.properties"));
    // Construct BasicDataSource reference
    Reference ref = new Reference("javax.sql.DataSource",
        "org.apache.commons.dbcp.BasicDataSourceFactory", null);
    ref.add(new StringRefAddr("driverClassName", props
        .getProperty("driverClassName")));
    ref.add(new StringRefAddr("url", props.getProperty("url")));
    ref.add(new StringRefAddr("username", props.getProperty("username")));
    ref.add(new StringRefAddr("password", props.getProperty("password")));
    ref.add(new StringRefAddr("maxActive", "4"));
    ref.add(new StringRefAddr("maxWait", "5000"));
    ref.add(new StringRefAddr("removeAbandoned", "true"));
    ref.add(new StringRefAddr("removeAbandonedTimeout", "5000"));
    ic.rebind(props.getProperty("jndi.name"), ref);
    jndiName = props.getProperty("jndi.name");
    
    // populate database
    IDatabaseConnection connection = null;
    try{
      connection = getDatabaseTester().getConnection();
      DatabaseOperation.DELETE_ALL.execute(connection, getDataSet());
      DatabaseOperation.CLEAN_INSERT.execute(connection, getDataSet());
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      if (connection != null) {
        try {
          connection.getConnection().close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    
    }

  @Override
  protected DatabaseOperation getTearDownOperation() throws Exception {
    return DatabaseOperation.DELETE_ALL;
  }

  /**
   * Test of insertRow method, of class ThumbnailDAO.
   * insert, test by select, delete row
   */
  @org.junit.Test
  public void testInsertThumbnail() throws Exception {
    System.out.println("insertRow");
    Connection con = getConnection().getConnection();
    
    String instanceId = "kmelia57";
    int objectId = 999999;
    int objectType = ThumbnailDetail.THUMBNAIL_OBJECTTYPE_PUBLICATION_VIGNETTE;
    String mimeType = "image/jpeg";
    String originalFileName = "55555555.jpg";
    String cropFileName = "7777777.jpg";
    int x_start = 25;
    int y_start = 27;
    int x_length = 99;
    int y_length = 111;
    
    ThumbnailDetail detail = new ThumbnailDetail(instanceId,objectId,objectType);
    detail.setOriginalFileName(originalFileName);
    detail.setMimeType(mimeType);
    detail.setCropFileName(cropFileName);
    detail.setXStart(x_start);
    detail.setYStart(y_start);
    detail.setXLength(x_length);
    detail.setYLength(y_length);
    
    ThumbnailDAO.insertThumbnail(con, detail);
    ThumbnailDetail result = ThumbnailDAO.selectByKey(con, instanceId, objectId, objectType);
    assertNotNull(result);
    assertEquals(detail.getInstanceId(), result.getInstanceId());
    assertEquals(detail.getObjectId(), result.getObjectId());
    assertEquals(detail.getObjectType(), result.getObjectType());
    assertEquals(detail.getOriginalFileName(), result.getOriginalFileName());
    assertEquals(detail.getMimeType(), result.getMimeType());
    assertEquals(detail.getCropFileName(), result.getCropFileName());
    assertEquals(detail.getXStart(), result.getXStart());
    assertEquals(detail.getXLength(), result.getXLength());
    assertEquals(detail.getYStart(), result.getYStart());
    assertEquals(detail.getYLength(), result.getYLength());
    
    ThumbnailDAO.deleteThumbnail(con, objectId, objectType, instanceId);
  }

  /**
   * Test of selectByKey method, of class ThumbnailDAO.
   */
  @org.junit.Test
  public void testSelectByKey() throws Exception {
    System.out.println("selectByKey");
    Connection con = getConnection().getConnection();
    ThumbnailDetail result = ThumbnailDAO.selectByKey(con, "kmelia57", 1, 0);
    assertNotNull(result);
    assertEquals("kmelia57", result.getInstanceId());
    assertEquals(1, result.getObjectId());
    assertEquals(0, result.getObjectType());
    assertEquals("123456789.jpg", result.getOriginalFileName());
    assertEquals("image/jpeg", result.getMimeType());
    assertEquals("987654321.jpg", result.getCropFileName());
    assertEquals(10, result.getXStart());
    assertEquals(123, result.getXLength());
    assertEquals(11, result.getYStart());
    assertEquals(321, result.getYLength());
  }
  
  /**
   * Test of delete method, of class ThumbnailDAO.
   * insert row, delete row, test by select
   */
  @org.junit.Test
  public void testDeleteThumbnail() throws Exception {
    System.out.println("insertRow");
    Connection con = getConnection().getConnection();
    
    String instanceId = "kmelia57";
    int objectId = 999999;
    int objectType = ThumbnailDetail.THUMBNAIL_OBJECTTYPE_PUBLICATION_VIGNETTE;
    String mimeType = "image/jpeg";
    String originalFileName = "55555555.jpg";
    String cropFileName = "7777777.jpg";
    int x_start = 25;
    int y_start = 27;
    int x_length = 99;
    int y_length = 111;
    
    ThumbnailDetail detail = new ThumbnailDetail(instanceId,objectId,objectType);
    detail.setOriginalFileName(originalFileName);
    detail.setMimeType(mimeType);
    detail.setCropFileName(cropFileName);
    detail.setXStart(x_start);
    detail.setYStart(y_start);
    detail.setXLength(x_length);
    detail.setYLength(y_length);
    
    ThumbnailDAO.insertThumbnail(con, detail);
    
    ThumbnailDAO.deleteThumbnail(con, objectId, objectType, instanceId);
    
    ThumbnailDetail result = ThumbnailDAO.selectByKey(con, instanceId, objectId, objectType);
    assertNull(result);
    
  }
  
  
  /**
   * Test of delete method, of class ThumbnailDAO.
   * insert row, delete row, test by select
   */
  @org.junit.Test
  public void testDeleteAllThumbnails() throws Exception {
    System.out.println("deleteAllRows");
    Connection con = getConnection().getConnection();
    
    String instanceId = "kmelia58";
    int objectId = 999999;
    int objectType = ThumbnailDetail.THUMBNAIL_OBJECTTYPE_PUBLICATION_VIGNETTE;
    String mimeType = "image/jpeg";
    String originalFileName = "55555555.jpg";
    String cropFileName = "7777777.jpg";
    int x_start = 25;
    int y_start = 27;
    int x_length = 99;
    int y_length = 111;
    
    ThumbnailDetail detail = new ThumbnailDetail(instanceId,objectId,objectType);
    detail.setOriginalFileName(originalFileName);
    detail.setMimeType(mimeType);
    detail.setCropFileName(cropFileName);
    detail.setXStart(x_start);
    detail.setYStart(y_start);
    detail.setXLength(x_length);
    detail.setYLength(y_length);
    
    ThumbnailDAO.insertThumbnail(con, detail);
    
    int objectId2 = 777777;
    detail.setObjectId(objectId2);
    
    ThumbnailDAO.insertThumbnail(con, detail);
    
    ThumbnailDAO.deleteAllThumbnails(con, instanceId);
    
    ThumbnailDetail result = ThumbnailDAO.selectByKey(con, instanceId, objectId, objectType);
    assertNull(result);
    ThumbnailDetail result2 = ThumbnailDAO.selectByKey(con, instanceId, objectId2, objectType);
    assertNull(result2);
    
  }
  
  
  /**
   * Test of update method, of class ThumbnailDAO.
   * insert row, test by select, update row, test by select, delete row
   */
  @org.junit.Test
  public void testUpdateRow() throws Exception {
    System.out.println("updateRow");
    Connection con = getConnection().getConnection();
    
    String instanceId = "kmelia57";
    int objectId = 999999;
    int objectType = ThumbnailDetail.THUMBNAIL_OBJECTTYPE_PUBLICATION_VIGNETTE;
    String mimeType = "image/jpeg";
    String originalFileName = "55555555.jpg";
    String cropFileName = "7777777.jpg";
    int x_start = 25;
    int y_start = 27;
    int x_length = 99;
    int y_length = 111;
    
    ThumbnailDetail detail = new ThumbnailDetail(instanceId,objectId,objectType);
    detail.setOriginalFileName(originalFileName);
    detail.setMimeType(mimeType);
    detail.setCropFileName("");
    detail.setXStart(0);
    detail.setYStart(0);
    detail.setXLength(0);
    detail.setYLength(0);
    
    ThumbnailDAO.insertThumbnail(con, detail);
    
    ThumbnailDetail result = ThumbnailDAO.selectByKey(con, instanceId, objectId, objectType);
    assertNotNull(result);
    assertEquals(detail.getCropFileName(), "");
    assertEquals(detail.getXStart(), 0);
    assertEquals(detail.getXLength(), 0);
    assertEquals(detail.getYStart(), 0);
    assertEquals(detail.getYLength(), 0);
    
    detail.setCropFileName(cropFileName);
    detail.setXStart(x_start);
    detail.setYStart(y_start);
    detail.setXLength(x_length);
    detail.setYLength(y_length);
    
    ThumbnailDAO.updateThumbnail(con, detail);
    
    result = ThumbnailDAO.selectByKey(con, instanceId, objectId, objectType);
    assertNotNull(result);
    assertEquals(detail.getInstanceId(), result.getInstanceId());
    assertEquals(detail.getObjectId(), result.getObjectId());
    assertEquals(detail.getObjectType(), result.getObjectType());
    assertEquals(detail.getOriginalFileName(), result.getOriginalFileName());
    assertEquals(detail.getMimeType(), result.getMimeType());
    assertEquals(detail.getCropFileName(), result.getCropFileName());
    assertEquals(detail.getXStart(), result.getXStart());
    assertEquals(detail.getXLength(), result.getXLength());
    assertEquals(detail.getYStart(), result.getYStart());
    assertEquals(detail.getYLength(), result.getYLength());
    
    ThumbnailDAO.deleteThumbnail(con, objectId, objectType, instanceId);
    
  }  

@Override
protected String getLookupName() {
	return jndiName;
}

@Override
protected IDataSet getDataSet() throws Exception {
	ReplacementDataSet dataSet = new ReplacementDataSet(new FlatXmlDataSet(
	        ThumbnailDAOTest.class
	            .getResourceAsStream("test-thumbnail-dao-dataset.xml")));
	    dataSet.addReplacementObject("[NULL]", null);
	    return dataSet;
	}
}