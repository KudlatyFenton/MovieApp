import javax.imageio.ImageIO;
import javax.swing.*;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;


public class MenuFrame extends JFrame{
    private JButton searchBtn;
    private JPanel panel;
    private JButton moviesBtn;
    private JButton moreBtn;
    private JButton searchButton;
    private JTextField movieNameText;
    private JPanel searchPanel;
    private JPanel menuPanel;
    private JList movieList;
    private JPanel moviesPanel;
    private JList list1;
    private JPanel detailsPanel;
    private JLabel movieTitleLabel;
    private JLabel releaseDateLabel;
    private JLabel originalTitleLabel;
    private JTextArea overviewTextArea;
    private JLabel voteAverageLabel;
    private JLabel imageLabel;

    public MenuFrame() {

        searchBtn.addActionListener(e -> {


            panel.removeAll();
            panel.add(searchPanel);
            panel.revalidate();

        });
        searchButton.addActionListener(e -> {


            String encotedTitle = null;
            try {
                encotedTitle = URLEncoder.encode(movieNameText.getText(), "UTF-8").replace("+", "%20");
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("Search button clicked");
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();
            Response response = null;
            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/search/movie?api_key={21a9da83bb164b7801aa191f21e1db99}&query="+ encotedTitle)//.url("https://api.themoviedb.org/3/movie/top_rated?language=en-US&page=1")
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

            JSONObject jobject = new JSONObject(jsonData);
            JSONArray jarray = jobject.getJSONArray("results");
            ResponseList responseList = gson.fromJson(jsonData, ResponseList.class);


            System.out.println(jobject);
            System.out.println(jarray);

            DefaultListModel model = new DefaultListModel();
            for(int i =0; i< jarray.length(); i++){
                model.addElement(responseList.results.get(i).getTitle());
            }
            System.out.println(model);
            movieList.setModel(model);

        });

        movieList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JList theList = (JList) e.getSource();
                Object o = theList.getModel().getElementAt(theList.locationToIndex(e.getPoint()));
                System.out.println(o);

                movieTitleLabel.setText(o.toString());

                String encotedTitle = null;
                try {
                    encotedTitle = URLEncoder.encode(o.toString(), "UTF-8").replace("+", "%20");
                } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex);
                }

                OkHttpClient client = new OkHttpClient();
                Gson gson = new Gson();
                Response response = null;
                Request request = new Request.Builder()
                        .url("https://api.themoviedb.org/3/search/movie?api_key={21a9da83bb164b7801aa191f21e1db99}&query="+ encotedTitle)//.url("https://api.themoviedb.org/3/movie/top_rated?language=en-US&page=1")
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

                //JSONObject jobject = new JSONObject(jsonData);
                ResponseList responseList = gson.fromJson(jsonData, ResponseList.class);

                releaseDateLabel.setText(responseList.results.get(0).release_date);
                originalTitleLabel.setText(responseList.results.get(0).original_title);
                overviewTextArea.setText(responseList.results.get(0).overview);
                voteAverageLabel.setText(String.valueOf(responseList.results.get(0).vote_average));

                URL imageUrl;
                BufferedImage image = null;
                try {
                    image = ImageIO.read(new File("src/main/resources/img.png"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    imageUrl = new URL("https://image.tmdb.org/t/p/w600_and_h900_bestv2"+responseList.results.get(0).backdrop_path);
                    image = ImageIO.read(imageUrl);
                } catch (IOException ex) {

                    //throw new RuntimeException(ex);
                }
                Image scaledImage = image.getScaledInstance(200,300, Image.SCALE_DEFAULT);
                imageLabel.setIcon(new ImageIcon(scaledImage));

                panel.removeAll();
                panel.add(detailsPanel);
                panel.revalidate();
            }
        });
    }

    public static void main(String[] args) {

        MenuFrame frame = new MenuFrame();
        frame.setContentPane(frame.panel);
        frame.setTitle("MovieApp");
        frame.setSize(500,500);
        frame.setVisible(true);
    }

}
