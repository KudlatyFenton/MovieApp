import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;


public class MenuFrame extends JFrame{
    private JButton menuSearchButton;
    private JPanel panel;
    private JButton moviesButton;
    private JButton moreButton;
    private JButton searchButton;
    private JTextField movieNameText;
    private JPanel searchPanel;
    private JPanel menuPanel;
    private JList movieList;
    private JPanel moviesPanel;
    private JList myMoviesList;
    private JPanel detailsPanel;
    private JLabel movieTitleLabel;
    private JLabel releaseDateLabel;
    private JLabel originalTitleLabel;
    private JTextArea overviewTextArea;
    private JLabel voteAverageLabel;
    private JLabel imageLabel;
    private JButton backButton;
    private JButton addButton;
    private JScrollBar scoreBar;
    private JLabel voteLabel;
    private JSlider voteSlider;
    private JLabel iconLabel;
    private JLabel appNameLabel;
    private JLabel sLogoLabel;
    private JButton sBackButton;
    private JTextArea notesTextArea;
    private JButton updateButton;
    private JButton mBackButton;

    private int currentMovieId;

    private JPanel previousPanel = menuPanel;

    public MenuFrame() {



        menuSearchButton.addActionListener(e -> {

            previousPanel = menuPanel;
            panel.removeAll();
            panel.add(searchPanel);
            panel.revalidate();
            panel.updateUI();

        });
        Action searchAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ResponseList responseList = responseList(movieNameText.getText());

                DefaultListModel model = new DefaultListModel();
                for(int i =0; i< responseList.results.size(); i++){
                    model.addElement(responseList.results.get(i).getTitle());
                }
                System.out.println(model);
                movieList.setModel(model);
            }
        };
        searchButton.addActionListener(searchAction);
        movieNameText.addActionListener(searchAction);

        movieList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JList theList = (JList) e.getSource();
                Object o = theList.getModel().getElementAt(theList.locationToIndex(e.getPoint()));
                previousPanel = searchPanel;
                movieTitleLabel.setText(o.toString());

                ResponseList responseList = responseList(o.toString());
                releaseDateLabel.setText(responseList.results.get(0).release_date);
                originalTitleLabel.setText(responseList.results.get(0).original_title);
                overviewTextArea.setText(responseList.results.get(0).overview);
                voteAverageLabel.setText(String.valueOf(responseList.results.get(0).vote_average));
                currentMovieId = responseList.results.get(0).id;
                notesTextArea.setText(responseList.results.get(0).notes);
                voteSlider.setValue(responseList.results.get(0).movie_rating);

                URL imageUrl;
                BufferedImage image = null;
                try {
                    image = ImageIO.read(new File("src/main/resources/img.png"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    imageUrl = new URL("https://image.tmdb.org/t/p/w600_and_h900_bestv2"+responseList.results.get(0).poster_path);
                    image = ImageIO.read(imageUrl);
                } catch (IOException ex) {

                    //throw new RuntimeException(ex);
                }
                Image scaledImage = image.getScaledInstance(200,300, Image.SCALE_DEFAULT);
                imageLabel.setIcon(new ImageIcon(scaledImage));

                panel.remove(searchPanel);
                panel.add(detailsPanel);
                addButton.show();
                updateButton.hide();
                panel.revalidate();
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.remove(detailsPanel);
                panel.add(previousPanel);
                panel.revalidate();
                //searchButton.getAction();
            }
        });
        moviesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.removeAll();
                panel.add(moviesPanel);
                panel.revalidate();
                panel.updateUI();

                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb","root","");
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM my_movie_list WHERE status=0");

                    DefaultListModel model = new DefaultListModel();
                    while (resultSet.next()){

                        Movie movie = movieById(Integer.parseInt(resultSet.getString("movie_id")));

                        model.addElement(movie.title);
                    }
                    myMoviesList.setModel(model);
                }
                catch (Exception exception){
                    exception.printStackTrace();
                }




            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb","root","");

                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM my_movie_list WHERE movie_id = "+currentMovieId);

                    if(!resultSet.next()) {

                        if (voteSlider.getValue()==0) {
                            String sql = "INSERT INTO `my_movie_list` (`movie_name`, `movie_id`, `movie_rating`, `date`, `status`, `notes`) VALUES (?,?, ?, current_timestamp(), ?, ?)";
                            PreparedStatement preparedStatement = connection.prepareStatement(sql);
                            preparedStatement.setString(1, originalTitleLabel.getText());
                            preparedStatement.setInt(2, currentMovieId);
                            preparedStatement.setDouble(3, 0);
                            preparedStatement.setInt(4, 0);
                            preparedStatement.setString(5, notesTextArea.getText());

                            preparedStatement.execute();
                        }
                        else {
                            String sql = "INSERT INTO `my_movie_list` (`movie_name`, `movie_id`, `movie_rating`, `date`, `status`, `notes`) VALUES (?,?, ?, current_timestamp(), ?, ?)";
                            PreparedStatement preparedStatement = connection.prepareStatement(sql);
                            preparedStatement.setString(1, originalTitleLabel.getText());
                            preparedStatement.setInt(2, currentMovieId);
                            preparedStatement.setDouble(3, voteSlider.getValue());
                            preparedStatement.setInt(4, 1);
                            preparedStatement.setString(5, notesTextArea.getText());

                            preparedStatement.execute();
                        }

                    }

                }
                catch (Exception exception){
                    exception.printStackTrace();
                }
            }
        });

        voteSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                voteLabel.setText(String.valueOf((double)voteSlider.getValue()/10));
            }
        });
        sBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.remove(searchPanel);
                panel.add(menuPanel);
                panel.revalidate();
            }
        });
        myMoviesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JList theList = (JList) e.getSource();
                Object o = theList.getModel().getElementAt(theList.locationToIndex(e.getPoint()));
                previousPanel = moviesPanel;



                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb","root","");
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM my_movie_list WHERE movie_name='"+o.toString()+"'");
                    resultSet.next();



                    notesTextArea.setText(resultSet.getString("notes"));
                    voteSlider.setValue(Integer.parseInt(resultSet.getString("movie_rating")));


                }
                catch (Exception exception){
                    exception.printStackTrace();
                }

                ResponseList responseList = responseList(o.toString());
                movieTitleLabel.setText(responseList.results.get(0).title);
                releaseDateLabel.setText(responseList.results.get(0).release_date);
                originalTitleLabel.setText(responseList.results.get(0).original_title);
                overviewTextArea.setText(responseList.results.get(0).overview);
                voteAverageLabel.setText(String.valueOf(responseList.results.get(0).vote_average));
                currentMovieId = responseList.results.get(0).id;

                URL imageUrl;
                BufferedImage image = null;
                try {
                    image = ImageIO.read(new File("src/main/resources/img.png"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    imageUrl = new URL("https://image.tmdb.org/t/p/w600_and_h900_bestv2"+responseList.results.get(0).poster_path);
                    image = ImageIO.read(imageUrl);
                } catch (IOException ex) {

                    //throw new RuntimeException(ex);
                }
                Image scaledImage = image.getScaledInstance(200,300, Image.SCALE_DEFAULT);
                imageLabel.setIcon(new ImageIcon(scaledImage));

                System.out.println(currentMovieId);
                panel.remove(moviesPanel);
                panel.add(detailsPanel);
                addButton.hide();
                updateButton.show();
                panel.revalidate();

            }
        });
        moreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.removeAll();
                panel.add(moviesPanel);
                panel.revalidate();
                panel.updateUI();

                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb","root","");
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM my_movie_list WHERE status=1");

                    DefaultListModel model = new DefaultListModel();
                    while (resultSet.next()){
                        Movie movie = movieById(Integer.parseInt(resultSet.getString("movie_id")));
                        model.addElement(movie.title);

                    }
                    myMoviesList.setModel(model);
                }
                catch (Exception exception){
                    exception.printStackTrace();
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb","root","");

                    String sql = "UPDATE `my_movie_list` SET `movie_rating`=?, `status`=?, `notes`=? WHERE `movie_name`=?";
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setInt(1, voteSlider.getValue());
                    preparedStatement.setInt(2, 1);
                    preparedStatement.setString(3, notesTextArea.getText());
                    preparedStatement.setString(4, originalTitleLabel.getText());


                    preparedStatement.execute();
                }
                catch (Exception exception){
                    exception.printStackTrace();
                }
            }
        });
        mBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.remove(moviesPanel);
                panel.add(menuPanel);
                panel.revalidate();
            }
        });
    }


    public ResponseList responseList(String movieTitle){

        String encodedTitle = null;
        try {
            encodedTitle = URLEncoder.encode(movieTitle, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }

        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        Response response = null;
        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/search/movie?api_key={21a9da83bb164b7801aa191f21e1db99}&query="+ encodedTitle + "&include_adult=false&language=pl-P")//.url("https://api.themoviedb.org/3/movie/top_rated?language=en-US&page=1")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMWE5ZGE4M2JiMTY0Yjc4MDFhYTE5MWYyMWUxZGI5OSIsInN1YiI6IjY0NWNkYjJkNmFhOGUwMDBlNGJlYjJiYiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.XlRR3UzQTFdfOZIWuj_eiLmxoNveXnNh9ZGcujLK9sI")
                .build();

        String jsonData;
        try {
            response = client.newCall(request).execute();
            jsonData = response.body().string();

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }


        ResponseList responseList = gson.fromJson(jsonData, ResponseList.class);

        return responseList;
    }

    public Movie movieById(int id){


        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        Response response = null;
        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/movie/"+id+"?api_key=21a9da83bb164b7801aa191f21e1db99")//.url("https://api.themoviedb.org/3/movie/top_rated?language=en-US&page=1")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMWE5ZGE4M2JiMTY0Yjc4MDFhYTE5MWYyMWUxZGI5OSIsInN1YiI6IjY0NWNkYjJkNmFhOGUwMDBlNGJlYjJiYiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.XlRR3UzQTFdfOZIWuj_eiLmxoNveXnNh9ZGcujLK9sI")
                .build();

        String jsonData;
        try {
            response = client.newCall(request).execute();
            jsonData = response.body().string();

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }


        Movie movie = gson.fromJson(jsonData, Movie.class);
        return movie;
    }
    public static void main(String[] args) {

        MenuFrame frame = new MenuFrame();
        frame.setContentPane(frame.panel);
        frame.setTitle("MovieApp");
        frame.setSize(800,1000);
        frame.setVisible(true);

        BufferedImage logo;
        try {
            logo = ImageIO.read(new File("src/main/resources/logo.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        frame.iconLabel.setIcon(new ImageIcon(logo));
    }

}
