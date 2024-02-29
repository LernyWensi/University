@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.lab_2_to_7

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lab_2_to_7.ui.theme.Lab_2_to_7Theme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val lazyListState = rememberLazyListState()
            val movieModel: MoviesViewModel by viewModels()

            Lab_2_to_7Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    movieModel.prepopulate()

                    Column {
                        AppBar(
                            movieModel = movieModel,
                            lazyListState = lazyListState
                        )
                        MovieList(
                            movies = movieModel.movies,
                            lazyListState = lazyListState,
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        )
                    }
                }
            }
        }
    }

    @ExperimentalMaterial3Api
    @Composable
    fun AppBar(movieModel: MoviesViewModel, lazyListState: LazyListState) {
        var isDropdownOpen by remember { mutableStateOf(false) }
        var isDialogOpen by remember { mutableStateOf(false) }

        val scope = rememberCoroutineScope()

        val startForResult =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                if (activityResult.resultCode == Activity.RESULT_OK) {
                    val newMovieItem = activityResult.data?.getSerializableExtra("newMovieItem") as Movie

                    movieModel.addMovie(newMovieItem)
                    scope.launch {
                        lazyListState.scrollToItem(movieModel.movies.lastIndex)
                    }
                }
            }

        if (isDialogOpen) {
            Popup(
                "About",
                text = "John Q. Public",
            ) { isDialogOpen = false }
        }


        TopAppBar(
            title = { Text("Movies") },
            actions = {
                IconButton(onClick = { isDropdownOpen = true }) {
                    Icon(Icons.Default.MoreVert, null)
                }
                DropdownMenu(
                    expanded = isDropdownOpen,
                    onDismissRequest = { isDropdownOpen = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("About") },
                        onClick = {
                            isDropdownOpen = false
                            isDialogOpen = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Add New Movie") },
                        onClick = {
                            val intent = Intent(this@MainActivity, InputActivity::class.java)
                            startForResult.launch(intent)
                            isDropdownOpen = false
                        }
                    )
                }
            }
        )
    }

    @Composable
    fun Popup(
        title: String,
        text: String,
        openPopupHandler: () -> Unit
    ) {
        AlertDialog(
            title = { Text(title) },
            text = { Text(text) },
            onDismissRequest = openPopupHandler,
            confirmButton = {
                Button(onClick = openPopupHandler) {
                    Text("Close")
                }
            },
        )
    }

    @Composable
    fun ColumnScope.MovieListItemRow(title: String, description: String) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .padding(start = 15.dp),
        ) {
            Text(
                title,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(85.dp),
            )
            Divider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier
                    .height(15.dp)
                    .width(1.dp),
            )
            Text(description, modifier = Modifier.weight(1f).padding(start = 15.dp))
        }
    }

    @Composable
    fun MoveListItem(movie: Movie) {
        var isPopupOpen by remember { mutableStateOf(false) }

        val resourceId = this@MainActivity.resources.getIdentifier(
            movie.name.lowercase().replace(" ", "_"),
            "string",
            this@MainActivity.packageName
        )

        if (isPopupOpen) {
            Popup(
                movie.name,
                if (resourceId != 0) stringResource(resourceId) else "No description found"
            ) { isPopupOpen = false }
        }

        Column(
            modifier = Modifier.clickable(onClick = { isPopupOpen = true })
        ) {
            Text(
                movie.name,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(5.dp),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(150.dp),
            ) {
                Image(
                    painter = painterResource(id = movie.picture),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxHeight(),
                )
                Column(verticalArrangement = Arrangement.SpaceBetween) {
                    MovieListItemRow("Genre", movie.genre)
                    Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                    MovieListItemRow("Director", movie.director)
                    Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                    MovieListItemRow("Company", movie.filmCompany)
                }
            }
        }
    }

    @Composable
    fun MovieList(movies: List<Movie>, modifier: Modifier, lazyListState: LazyListState) {
        LazyColumn(modifier, state = lazyListState) {
            items(movies) {
                MoveListItem(it)
            }
        }
    }
}

