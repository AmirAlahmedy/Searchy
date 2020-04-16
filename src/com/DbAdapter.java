package com;

import com.crawler.PageContent;

import java.sql.*;

public class DbAdapter {

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    private String url="jdbc:mysql://localhost:3306/search_engine";
    private String user="root";
    public DbAdapter(){
        try{
            connection= DriverManager.getConnection(url,user,null);
            //statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNewPage(PageContent page) {
        try{
            statement = connection.createStatement();
            //statement.executeUpdate("INSERT INTO `pages` (`id`, `url`, `title`, `h1`, `h2`, `h3`, `h4`, `h5`, `h6`, `body`, `alt`, `meta`) VALUES (NULL,'"+url+"','"+title+"','"+h1+"', '"+h2+"', '"+h3+"', '"+h4+"', '"+h5+"', '"+h6+"', '"+body+"', '"+alt+"', '"+meta+"');");
            String query="INSERT INTO `pages` (`id`, `url`, `title`, `h1`, `h2`, `h3`, `h4`, `h5`, `h6`, `body`, `alt`, `meta`) VALUES (NULL,? ,? ,?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement= connection.prepareStatement(query);
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
}
