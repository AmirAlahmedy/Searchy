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

    public void addNewPage(PageContent page) {
        try{
            String query="INSERT INTO `pages` (`id`, `url`, `title`, `h1`, `h2`, `h3`, `h4`, `h5`, `h6`, `body`, `alt`, `meta`) VALUES (NULL,? ,? ,?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, page.getLink());
            preparedStatement.setString(2, page.getTitle());
            preparedStatement.setString(3, page.getH1());
            preparedStatement.setString(4, page.getH2());
            preparedStatement.setString(5, page.getH3());
            preparedStatement.setString(6, page.getH4());
            preparedStatement.setString(7, page.getH5());
            preparedStatement.setString(8, page.getH6());
            preparedStatement.setString(9, page.getBody());
            preparedStatement.setString(10, page.getAlt());
            preparedStatement.setString(11, page.getMeta());
            preparedStatement.execute();
            System.out.println("Added page to database successfully");
        } catch (SQLException e) {
            e.printStackTrace();
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

    public void addNewTerm(String term, int pageId, int htmlTag) {
        try {
            String query = "INSERT INTO `Terms` (`id`, `Term`, `Page_Id`, `TF`, `IDF`, `Title`, `Meta`, `H1`, `H2`, `H3`, `H4`, `H5`, `H6`, `Alt`) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" +
                    " ON DUPLICATE KEY UPDATE TF = TF + 1";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, term);
            preparedStatement.setInt(2, pageId);
            //TODO: Add TF and IDF
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
}
