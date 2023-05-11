import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame implements ActionListener {

    GUI(){
        this.setTitle("MovieApp");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(500,500);


        this.getContentPane().setBackground(Color.darkGray);
        this.setLayout(null);

        JPanel panel=new JPanel();
        panel.setBounds(150,100,200,250);
        panel.setBackground(Color.gray);
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JButton searchButton=new JButton("Search");
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchButton.setBackground(Color.lightGray);
        searchButton.addActionListener(e -> searchButtonPressed());

        JButton moviesButton=new JButton("My movies");
        moviesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        moviesButton.setBackground(Color.lightGray);

        JButton moreButton=new JButton("More...");
        moreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        moreButton.setBackground(Color.lightGray);

        panel.add(searchButton);
        panel.add(moviesButton);
        panel.add(moreButton);
        this.add(panel);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public void searchButtonPressed(){
        JPanel panel=new JPanel();
        panel.setBounds(150,100,200,250);
        panel.setBackground(Color.red);
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        this.add(panel);
    }
}
