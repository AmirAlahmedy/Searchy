package com;

import com.crawler.PageContent;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import java.sql.*;

public class DbAdapter {

    private Connection connection;

    private String url="jdbc:mysql://localhost:3306/search_engine";
    private String user="root";


    public DbAdapter(){
        try{
            connection= DriverManager.getConnection(url,user,null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean addNewPage(String url,String title,String h1,String h2,String h3,String h4,String h5,String h6,String body,String alt, String meta, int words) {
        try{
            //statement = connection.createStatement();
            //statement.executeUpdate("INSERT INTO `pages` (`id`, `url`, `title`, `h1`, `h2`, `h3`, `h4`, `h5`, `h6`, `body`, `alt`, `meta`) VALUES (NULL,'"+url+"','"+title+"','"+h1+"', '"+h2+"', '"+h3+"', '"+h4+"', '"+h5+"', '"+h6+"', '"+body+"', '"+alt+"', '"+meta+"');");
            if(isLinkUsedBefore(url)){
                System.out.println("Page already added before" + url);

                return false;
            }
            String query="INSERT INTO `pages` (`id`, `url`, `title`, `h1`, `h2`, `h3`, `h4`, `h5`, `h6`, `body`, `alt`, `meta`, `words`) VALUES (NULL,? ,? ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement= connection.prepareStatement(query);
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
            preparedStatement.setInt(12, words);
            boolean a =preparedStatement.execute();
            System.out.println("Added page to database successfully");
            return a;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized boolean isLinkUsedBefore(String url){
        try{
        String query = "SELECT * FROM `pages` WHERE `url` = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,url);
        ResultSet r= preparedStatement.executeQuery();
        //System.out.println(r.next());
        return r.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }


    public ResultSet readPages() {
        ResultSet resultSet = null;
        try {
            String query = "SELECT * FROM `pages`";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public void addNewTerm(String term, int pageId, int htmlTag, int words) {
        try {
            String query = "INSERT INTO `Terms` (`id`, `Term`, `Page_Id`, `TF`, `IDF`, `Title`, `Meta`, `H1`, `H2`, `H3`, `H4`, `H5`, `H6`, `Alt`) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" +
                    " ON DUPLICATE KEY UPDATE TF = TF +" + (double)1/words;

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, term);
            preparedStatement.setInt(2, pageId);
            //TODO: Add TF and IDF
//            String q1 = "SELECT COUNT(*) FROM Terms WHERE Term = " + term + " AND Page_Id = " + pageId;
//            String q2 = "SELECT COUNT(*) FROM Terms WHERE Page_Id = " + pageId;
//
//            PreparedStatement ps1 = connection.prepareStatement(q1);
//            PreparedStatement ps2 = connection.prepareStatement(q2);
//
//            ResultSet resultSet = ps1.executeQuery();
//            while (resultSet.next()) {
//                double termFrequency = resultSet.getInt(1);
//            }
//
//            resultSet = ps2.executeQuery();
//
//            while (resultSet.next()) {
//                 words = resultSet.getInt(1);
//            }

            preparedStatement.setDouble(3, (double) 1/words);
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

            if(htmlTag == 3) {
                preparedStatement.setBoolean(5, true);
            } else if(htmlTag == 4) {
                preparedStatement.setBoolean(7, true);
            } else if(htmlTag == 5) {
                preparedStatement.setBoolean(8, true);
            } else if(htmlTag == 6) {
                preparedStatement.setBoolean(9, true);
            } else if(htmlTag == 7) {
                preparedStatement.setBoolean(10, true);
            } else if(htmlTag == 8) {
                preparedStatement.setBoolean(11, true);
            } else if(htmlTag == 9) {
                preparedStatement.setBoolean(12, true);
            } else if(htmlTag == 12) {
                preparedStatement.setBoolean(6, true);
            } else if(htmlTag == 11) {
                preparedStatement.setBoolean(13, true);
            }

            preparedStatement.execute();

        } catch (MySQLIntegrityConstraintViolationException e) {
            System.err.println("Duplicate Primary Key: " + term +"-"+ pageId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    public void addNewImg(int pageId,String term, String url){
        try {
            String query = "INSERT INTO `images` (`id`,`term`,`page_Id`,`src`) VALUES (NULL,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,term);
            preparedStatement.setInt(2,pageId);
            preparedStatement.setString(3,url);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
