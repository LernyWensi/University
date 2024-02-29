package com.example.lab_2_to_7

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.io.Serializable

data class Movie(
    val name: String,
    val director: String,
    val genre: String,
    val filmCompany: String,
    var picture: Int = R.drawable.movie_roll,
) : Serializable

class MoviesViewModel : ViewModel() {
    val movies = mutableStateListOf<Movie>()

    fun addMovie(
        name: String,
        director: String,
        genre: String,
        filmCompany: String,
        picture: Int = R.drawable.movie_roll,
    ) {
        movies.add(Movie(name, director, genre, filmCompany, picture))
    }

    fun addMovie(movie: Movie) {
        movies.add(movie)
    }

    fun prepopulate(): MoviesViewModel {
        this.addMovie(
            "The Dark Knight",
            "Christopher Nolan",
            "Action",
            "Warner Bros. Pictures",
            R.drawable.the_dark_knight
        )
        this.addMovie(
            "Interstellar",
            "Christopher Nolan",
            "Science Fiction",
            "Paramount Pictures",
            R.drawable.interstellar
        )
        this.addMovie("Gladiator", "Ridley Scott", "Epic", "DreamWorks Pictures", R.drawable.gladiator)
        this.addMovie(
            "The Shawshank Redemption",
            "Frank Darabont",
            "Drama",
            "Columbia Pictures",
            R.drawable.the_shawshank_redemption
        )
        this.addMovie(
            "The Green Mile",
            "Frank Darabont",
            "Drama",
            "Castle Rock Entertainment",
            R.drawable.the_green_mile
        )
        this.addMovie(
            "Inception",
            "Christopher Nolan",
            "Science Fiction",
            "Warner Bros. Pictures",
            R.drawable.inception
        )

        return this
    }
}