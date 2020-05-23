package com;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import java.sql.*;
import java.util.ArrayList;

public class DbAdapter {

    private Connection connection;

    private String url = "jdbc:mysql://localhost:3306/search_engine?serverTimezone=UTC";
    private String user = "root";


    public DbAdapter() {
        try {
            connection = DriverManager.getConnection(url, user, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean addNewPage(String url, String title, String h1, String h2, String h3, String h4, String h5, String h6, String body, String alt, String meta) {
        try {
            //statement = connection.createStatement();
            //statement.executeUpdate("INSERT INTO `pages` (`id`, `url`, `title`, `h1`, `h2`, `h3`, `h4`, `h5`, `h6`, `body`, `alt`, `meta`) VALUES (NULL,'"+url+"','"+title+"','"+h1+"', '"+h2+"', '"+h3+"', '"+h4+"', '"+h5+"', '"+h6+"', '"+body+"', '"+alt+"', '"+meta+"');");
            if (isLinkUsedBefore(url)) {
                System.out.println("Page already added before" + url);

                return false;
            }
            String query = "INSERT INTO `pages` (`id`, `url`, `title`, `h1`, `h2`, `h3`, `h4`, `h5`, `h6`, `body`, `alt`, `meta`, `words`, `indexed`) VALUES (NULL,? ,? ,?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, url);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, h1);
            preparedStatement.setString(4, h2);
            preparedStatement.setString(5, h3);
            preparedStatement.setString(6, h4);
            preparedStatement.setString(7, h5);
            preparedStatement.setString(8, h6);
            preparedStatement.setString(9, body);
            preparedStatement.setString(10, alt);
            preparedStatement.setString(11, meta);
            preparedStatement.setBoolean(12,false);
            preparedStatement.execute();
            System.out.println("Added page to database successfully");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updatePageWordCount(int pageId, int words) {
        try {
            String query = "UPDATE `pages` SET `words` = ? WHERE `id` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, words);
            preparedStatement.setInt(2, pageId);

            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean isLinkUsedBefore(String url) {
        try {
            String query = "SELECT * FROM `pages` WHERE `url` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, url);
            ResultSet r = preparedStatement.executeQuery();
            //System.out.println(r.next());
            return r.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public void addPageToBackup(String url){
        try{
            String query = "INSERT INTO `Crawler_Backup` (`id`, `url`) VALUES (NULL,?)"+
                    "ON DUPLICATE KEY UPDATE url = ?";
            //Equivalent to on duplicate update do nothing
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,url);
            preparedStatement.setString(2,url);
            preparedStatement.execute();
        }  catch (SQLException throwables) {
            if (throwables instanceof SQLIntegrityConstraintViolationException){
                //Ignore duplicate entry error
            }
            else {
                throwables.printStackTrace();
            }
        }
    }

    public void removePageFromBackup(String url){
        try{
            String query = "DELETE FROM `Crawler_Backup` WHERE `url` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,url);
            preparedStatement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int pageBackupCount(){
        ResultSet resultSet = null;
        try {
            String query = "SELECT COUNT(*) FROM `Crawler_Backup`";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean isPageInBackup(String url){
        try{
            String query = "SELECT * FROM `Crawler_Backup` WHERE url = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,url);
            ResultSet resultSet=preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return true;
        }
    }

    public ResultSet getPagesInBackup(){
        try{
            String query = "SELECT * FROM `Crawler_Backup`";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            return  preparedStatement.executeQuery();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public void markPageAsIndexed(int pageId){
        try{
            String query= "UPDATE `pages` SET `indexed` = ? WHERE `id` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setBoolean(1,true);
            preparedStatement.setInt(2,pageId);
            preparedStatement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ResultSet readPagesThreads(int size,int offset) {
        ResultSet resultSet = null;
        try {
            String query = "SELECT * FROM `pages` LIMIT ? OFFSET ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,size);
            preparedStatement.setInt(2,offset);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }


    public ResultSet readURLID() {
        ResultSet resultSet = null;
        try {
            String query = "SELECT id, url FROM `pages`";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet readID(String URL) {
        ResultSet resultSet = null;
        try {
            String query = "SELECT id FROM `pages` WHERE `url` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, URL);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public int pagesRows() {
        ResultSet resultSet = null;
        try {
            String query = "SELECT COUNT(*) FROM `pages`";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void fillRanks(double initialRank) {
        try {
            String query = "DELETE FROM Ranks";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.executeUpdate();
            String query0 = "INSERT INTO `Ranks` (pageId)  SELECT id FROM `pages`";
            preparedStatement = this.connection.prepareStatement(query0);
            preparedStatement.executeUpdate(query0);
            String query1 = "UPDATE Ranks SET PR = ?";
            preparedStatement = this.connection.prepareStatement(query1);
            preparedStatement.setDouble(1, initialRank);
            preparedStatement.executeUpdate();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }

    public void setPR(int pageID, double pr) {
        try {
            String query = "UPDATE Ranks SET PR = ? WHERE pageId = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setDouble(1, pr);
            preparedStatement.setInt(2, pageID);
            preparedStatement.executeUpdate();
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    public double getPR(int pageID) {
        ResultSet resultSet = null;

        try {
            String query = "SELECT PR FROM `Ranks` WHERE pageID = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setInt(1, pageID);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getDouble(1);
        } catch (Exception var5) {
            var5.printStackTrace();
            return 0.0D;
        }
    }

    public void addNewTerm(String term, int pageId, int htmlTag) {

        boolean update;
        try {
            String query = "SELECT * FROM `Terms` WHERE `Term` = ? AND `Page_Id` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, term);
            preparedStatement.setInt(2, pageId);
            ResultSet r = preparedStatement.executeQuery();
            //System.out.println(r.next());
            update = r.next();
            if (update) {
                updateTerm(r, htmlTag);
            } else {
                insertTerm(term, pageId, htmlTag);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void insertTerm(String term, int pageId, int htmlTag) {

        try {
            String query = "INSERT INTO `Terms` (`id`, `Term`, `Page_Id`, `TF`, `IDF`, `Title`, `Meta`, `H1`, `H2`, `H3`, `H4`, `H5`, `H6`, `Alt`) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, term);
            preparedStatement.setInt(2, pageId);

            preparedStatement.setDouble(3, 1);
            preparedStatement.setDouble(4, 0);

            preparedStatement.setBoolean(5, false);
            preparedStatement.setBoolean(6, false);
            preparedStatement.setBoolean(7, false);
            preparedStatement.setBoolean(8, false);
            preparedStatement.setBoolean(9, false);
            preparedStatement.setBoolean(10, false);
            preparedStatement.setBoolean(11, false);
            preparedStatement.setBoolean(12, false);
            preparedStatement.setBoolean(13, false);

            if (htmlTag == 3) {
                preparedStatement.setBoolean(5, true);
            } else if (htmlTag == 4) {
                preparedStatement.setBoolean(7, true);
            } else if (htmlTag == 5) {
                preparedStatement.setBoolean(8, true);
            } else if (htmlTag == 6) {
                preparedStatement.setBoolean(9, true);
            } else if (htmlTag == 7) {
                preparedStatement.setBoolean(10, true);
            } else if (htmlTag == 8) {
                preparedStatement.setBoolean(11, true);
            } else if (htmlTag == 9) {
                preparedStatement.setBoolean(12, true);
            }
//            } else if(htmlTag == 12) {
//                preparedStatement.setBoolean(6, true);
//            } else if(htmlTag == 11) {
//                preparedStatement.setBoolean(13, true);
//            }

            preparedStatement.execute();

        } catch (MySQLIntegrityConstraintViolationException e) {
            System.err.println("Duplicate Primary Key: " + term + "-" + pageId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void updateTerm(ResultSet r, int htmlTag) {
        try {
            String query = "UPDATE `Terms` SET `TF` = ?,`H1` = ?, `H2` = ?, `H3` = ?, `H4` = ?, `H5` = ?, `H6` = ? " +
                    "WHERE `Term` = ? AND  `Page_Id` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, r.getInt("TF") + 1);
            preparedStatement.setBoolean(2, r.getBoolean("H1"));
            preparedStatement.setBoolean(3, r.getBoolean("H2"));
            preparedStatement.setBoolean(4, r.getBoolean("H3"));
            preparedStatement.setBoolean(5, r.getBoolean("H4"));
            preparedStatement.setBoolean(6, r.getBoolean("H5"));
            preparedStatement.setBoolean(7, r.getBoolean("H6"));
            preparedStatement.setString(8, r.getString("Term"));
            preparedStatement.setInt(9, r.getInt("Page_Id"));


            if (htmlTag == 4) {
                preparedStatement.setBoolean(2, true);
            } else if (htmlTag == 5) {
                preparedStatement.setBoolean(3, true);
            } else if (htmlTag == 6) {
                preparedStatement.setBoolean(4, true);
            } else if (htmlTag == 7) {
                preparedStatement.setBoolean(5, true);
            } else if (htmlTag == 8) {
                preparedStatement.setBoolean(6, true);
            } else if (htmlTag == 9) {
                preparedStatement.setBoolean(7, true);
            }
            preparedStatement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void updateTF(int pageId, int words) {
        try {
            String query = "UPDATE `Terms` SET `TF` = `TF`/ ? WHERE `Page_Id` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, words);
            preparedStatement.setInt(2, pageId);

            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setIDF() {
        try {
            ResultSet r = getDistinctTerms();
            int allDocs = pagesRows();
            while (r.next()) {
                setTermIDF(r.getString("Term"), allDocs);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setTermIDF(String term, int allDocs) {
        try {
            String query = "UPDATE `Terms` SET `IDF` = ? WHERE `Term` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            int termDocs = termRows(term);
            preparedStatement.setDouble(1, Math.log((double) allDocs / termDocs));
            preparedStatement.setString(2, term);

            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int termRows(String term) {
        try {
            String query = "SELECT COUNT(*) FROM `Terms` WHERE `Term` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, term);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public ResultSet getDistinctTerms() {
        try {
            String query = "SELECT DISTINCT `Term` FROM `Terms`";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            return preparedStatement.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void deleteOldIndex(int pageId){
        try{
            String query = "DELETE FROM `Terms` WHERE `Page_Id` = ?";
            String query2 = "DELETE FROM `Images` WHERE `Page_Id` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,pageId);
            preparedStatement.execute();
            preparedStatement =connection.prepareStatement(query2);
            preparedStatement.setInt(1,pageId);
            preparedStatement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addNewImg(int pageId, String term, String url) {
        try {
            String query = "INSERT INTO `Images` (`id`,`term`,`page_Id`,`src`) VALUES (NULL,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, term);
            preparedStatement.setInt(2, pageId);
            preparedStatement.setString(3, url);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public ResultSet getTerms() {
        try {
            String query = "SELECT * FROM `Terms`";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getRanks() {
        try {
            String query = "SELECT * FROM `Ranks`";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public double getIDF(String term) {
        try {
            String query = "SELECT DISTINCT IDF FROM `Terms` WHERE `Term` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, term);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0D;
        }
    }
    public double getTF(String term, Integer pageID) {
        try {
            String query = "SELECT TF FROM `Terms` WHERE `Term` = ? AND `Page_Id` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, term);
            preparedStatement.setInt(2, pageID);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0D;
        }
    }

    public ResultSet selectCommonPages (ArrayList <String> terms) {
        int numberOfTerms = terms.size();
        try {
            String query = "SELECT Page_Id FROM `Terms` WHERE `Term` = ? ";
            for(int i=1;i<numberOfTerms;i++)
            {
                query+= "INTERSECT SELECT Page_Id FROM `Terms` WHERE `Term` = ? ";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, terms.get(0));
            for(int i=1;i<numberOfTerms;i++)
            {
                preparedStatement.setString(i+1, terms.get(i));
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            //resultSet.next();
            return resultSet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    public ResultSet selectCommonPages_images (ArrayList <String> terms) {
        int numberOfTerms = terms.size();
        try {
            String query = "SELECT page_Id FROM `Images` WHERE `term` = ? ";
            for(int i=1;i<numberOfTerms;i++)
            {
                query+= "INTERSECT SELECT page_Id FROM `Images` WHERE `term` = ? ";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, terms.get(0));
            for(int i=1;i<numberOfTerms;i++)
            {
                preparedStatement.setString(i+1, terms.get(i));
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            //resultSet.next();
            return resultSet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    public ResultSet getPagesInfo (Integer[] page_id) {
        try {
            String query = "SELECT ID, URL, TITLE, BODY  FROM `pages` WHERE `id` = ? ";
            for(int i=1;i<page_id.length;i++)
            {
                query+= "UNION SELECT ID, URL, TITLE, BODY  FROM `pages` WHERE `id` = ? ";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,page_id[0]);
            for(int i=1;i<page_id.length;i++)
            {
                preparedStatement.setInt(i+1, page_id[i]);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    public ResultSet getImageSRCs (Integer[] page_id) {
        try {
            String query = "SELECT id,src  FROM `Images` WHERE `page_Id` = ? ";
            for(int i=1;i<page_id.length;i++)
            {
                query+= "UNION SELECT id,src  FROM `Images` WHERE `page_Id` = ? ";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,page_id[0]);
            for(int i=1;i<page_id.length;i++)
            {
                preparedStatement.setInt(i+1, page_id[i]);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    public void addNameTrend (String name, String country){
        try {
            String query = "INSERT INTO `Trends` (`id`,`name`,`country`,`frequency`) VALUES (NULL,?,?,?)" +
                    "ON DUPLICATE KEY UPDATE frequency=frequency+1";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, country);
            preparedStatement.setInt(3, 1);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getTrends(String country){
        try {
            String query = "SELECT * FROM `Trends` WHERE `country` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,country);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteAll(){
        try{
            String query = "DELETE FROM `Terms`";
            String query2 = "DELETE FROM `Images`";
            String query3 = "DELETE FROM `pages`";
            String query4 = "DELETE FROM `Crawler_Backup`";
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.execute();
            preparedStatement = connection.prepareStatement(query2);
            preparedStatement.execute();
            preparedStatement = connection.prepareStatement(query3);
            preparedStatement.execute();
            preparedStatement = connection.prepareStatement(query4);
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
