package com.example.lab_2_to_7

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.Serializable

data class Movie(
    val name: String,
    val director: String,
    val genre: String,
    val filmCompany: String,
    var picture: String = R.drawable.movie_roll.toString(),
) : Serializable

class MoviesViewModel : ViewModel() {
    val movies = mutableStateListOf<Movie>(
        Movie(
            "The Dark Knight",
            "Christopher Nolan",
            "Action",
            "Warner Bros. Pictures",
            R.drawable.the_dark_knight.toString(),
        ),
        Movie(
            "Interstellar",
            "Christopher Nolan",
            "Science Fiction",
            "Paramount Pictures",
            R.drawable.interstellar.toString(),
        ),
        Movie("Gladiator", "Ridley Scott", "Epic", "DreamWorks Pictures", R.drawable.gladiator.toString()),
        Movie(
            "The Shawshank Redemption",
            "Frank Darabont",
            "Drama",
            "Columbia Pictures",
            R.drawable.the_shawshank_redemption.toString(),
        ),
        Movie(
            "The Green Mile",
            "Frank Darabont",
            "Drama",
            "Castle Rock Entertainment",
            R.drawable.the_green_mile.toString(),
        ),
        Movie(
            "Inception",
            "Christopher Nolan",
            "Science Fiction",
            "Warner Bros. Pictures",
            R.drawable.inception.toString(),
        ),
    )

    private val _movieListFlow = MutableStateFlow(movies)
    val movieListFlow: StateFlow<List<Movie>> get() = _movieListFlow

    fun add(
        name: String,
        director: String,
        genre: String,
        filmCompany: String,
        picture: String = R.drawable.movie_roll.toString(),
    ) {
        movies.add(Movie(name, director, genre, filmCompany, picture))
    }

    fun add(movie: Movie) {
        movies.add(movie)
    }

    fun remove(movie: Movie) {
        movies.remove(movie)
    }

    fun changeImage(movie: Movie, newPicturePath: String) {
        movies[movies.indexOf(movie)] = movie.copy(picture = newPicturePath)
    }

    fun clear() {
        movies.clear()
    }
}