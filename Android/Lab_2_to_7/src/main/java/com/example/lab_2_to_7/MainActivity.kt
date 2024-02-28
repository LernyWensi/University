@file:OptIn(ExperimentalComposeUiApi::class)

package com.example.lab_2_to_7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.lab_2_to_7.ui.theme.Lab_2_to_7Theme

data class Movie(
        val name: String,
        val director: String,
        val genre: String,
        val filmCompany: String,
        var picture: Int,
)

class MoviesViewModel : ViewModel() {
    val languages = mutableStateListOf<Movie>()

    fun addMovie(
            name: String,
            director: String,
            genre: String,
            filmCompany: String,
            picture: Int = R.drawable.movie_roll,
    ) {
        languages.add(Movie(name, director, genre, filmCompany, picture))
    }

    fun prepopulate(): MoviesViewModel {
        this.addMovie("The Dark Knight", "Christopher Nolan", "Action", "Warner Bros. Pictures", R.drawable.the_dark_knight)
        this.addMovie("Interstellar", "Christopher Nolan", "Science Fiction", "Paramount Pictures", R.drawable.interstellar)
        this.addMovie("Gladiator", "Ridley Scott", "Epic", "DreamWorks Pictures", R.drawable.gradiator)
        this.addMovie("The Shawshank Redemption", "Frank Darabont", "Drama", "Columbia Pictures", R.drawable.the_shawshank_redemption)
        this.addMovie("The Green Mile", "Frank Darabont", "Drama", "Castle Rock Entertainment", R.drawable.the_green_mile)
        this.addMovie("Inception", "Christopher Nolan", "Science Fiction", "Warner Bros. Pictures", R.drawable.inception)

        return this
    }
}

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    private val viewModel: MoviesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab_2_to_7Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    viewModel.prepopulate()

                    Column {
                        MovieList(viewModel.languages, modifier = Modifier.fillMaxHeight().weight(1f))
                        Menu(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MovieListItemRow(title: String, description: String) {
    Row(modifier = Modifier.padding(5.dp)) {
        Text(
                title,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(90.dp),
        )
        Text(description, modifier = Modifier.weight(1f))
    }
}

@Composable
fun MoveListItem(movie: Movie) {
    Column {
        Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Image(
                    painter = painterResource(id = movie.picture),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(50.dp)
            )
            Text(
                    movie.name,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
            )
        }
        Column {
            MovieListItemRow("Genre", movie.genre)
            Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
            MovieListItemRow("Director", movie.director)
            Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
            MovieListItemRow("Company", movie.filmCompany)
        }
    }
}

@Composable
fun MovieList(movies: List<Movie>, modifier: Modifier) {
    LazyColumn(modifier) {
        items(movies) {
            MoveListItem(it)
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun Menu(viewModel: MoviesViewModel) {
    var name by remember { mutableStateOf("") }
    var director by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var filmCompany by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current

    fun handleClickEvent() {
        viewModel.addMovie(name, director, genre, filmCompany)

        name = ""
        director = ""
        genre = ""
        filmCompany = ""

        keyboardController?.hide()
    }

    Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(5.dp).height(IntrinsicSize.Min).imePadding()
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            TextField(
                    value = filmCompany,
                    onValueChange = { filmCompany = it.trim() },
                    label = { Text("Film Company") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.weight(1f),
            )
            TextField(
                    value = genre,
                    onValueChange = { genre = it.trim() },
                    label = { Text("Genre") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.weight(1f),
            )
        }

        TextField(
                value = director,
                onValueChange = { director = it.trim() },
                label = { Text("Director") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
        )
        TextField(
                value = name,
                onValueChange = { name = it.trim() },
                label = { Text("Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
        )

        Button(
                onClick = { handleClickEvent() },
                modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Add Movie")
        }
    }
}