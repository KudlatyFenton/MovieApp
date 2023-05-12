import javax.swing.*;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;


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

    public MenuFrame() {

        searchBtn.addActionListener(e -> {


            panel.removeAll();
            panel.add(searchPanel);
            panel.revalidate();

        });
        searchButton.addActionListener(e -> {

            System.out.println("Search button clicked");
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/search/movie?query=The%2520Covenant&include_adult=false&language=pl-PL&page=1")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMWE5ZGE4M2JiMTY0Yjc4MDFhYTE5MWYyMWUxZGI5OSIsInN1YiI6IjY0NWNkYjJkNmFhOGUwMDBlNGJlYjJiYiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.XlRR3UzQTFdfOZIWuj_eiLmxoNveXnNh9ZGcujLK9sI")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                System.out.println(request);
                System.out.println(response.body().string());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
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
