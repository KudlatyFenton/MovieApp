public class Movie {
    String title;
    String original_title;
    //String director;
    double vote_average;
    //String[] genres;
    int id;
    String release_date;
    String overview;
    String backdrop_path;
    String poster_path;

    String notes;

    int movie_rating;
    public String getTitle() {
        return title;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setMovie_rating(int movie_rating) {
        this.movie_rating = movie_rating;
    }
}
