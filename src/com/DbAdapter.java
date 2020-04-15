package com;

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

    public void addNewPage(String url,String title,String h1,String h2,String h3,String h4,String h5, String h6,String body,String alt,String meta ) {
        try{
            statement = connection.createStatement();
            //statement.executeUpdate("INSERT INTO `pages` (`id`, `url`, `title`, `h1`, `h2`, `h3`, `h4`, `h5`, `h6`, `body`, `alt`, `meta`) VALUES (NULL,'"+url+"','"+title+"','"+h1+"', '"+h2+"', '"+h3+"', '"+h4+"', '"+h5+"', '"+h6+"', '"+body+"', '"+alt+"', '"+meta+"');");
            String query="INSERT INTO `pages` (`id`, `url`, `title`, `h1`, `h2`, `h3`, `h4`, `h5`, `h6`, `body`, `alt`, `meta`) VALUES (NULL,? ,? ,?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement= connection.prepareStatement(query);
            preparedStatement.setString(1,url);
            preparedStatement.setString(2,title);
            preparedStatement.setString(3,h1);
            preparedStatement.setString(4,h2);
            preparedStatement.setString(5,h3);
            preparedStatement.setString(6,h4);
            preparedStatement.setString(7,h5);
            preparedStatement.setString(8,h6);
            preparedStatement.setString(9,body);
            preparedStatement.setString(10,alt);
            preparedStatement.setString(11,meta);
            preparedStatement.execute();
            System.out.println("Added page to database successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
