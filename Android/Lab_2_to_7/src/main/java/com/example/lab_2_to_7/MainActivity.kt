@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.lab_2_to_7

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.lab_2_to_7.ui.theme.Lab_2_to_7Theme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val movieModel = MoviesViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbHelper = MovieDbHelper(this)

        if (savedInstanceState != null && savedInstanceState.containsKey("movies")) {
            val tmpMoviesArray = savedInstanceState.getSerializable("movies") as ArrayList<Movie>
            movieModel.clear()
            tmpMoviesArray.forEach { movieModel.add(it) }
        } else {
            if (dbHelper.isEmpty()) {
                println("Database is empty")

                val tmpMovieArray = ArrayList<Movie>()
                movieModel.movieListFlow.value.forEach { tmpMovieArray.add(it) }

                dbHelper.addArrayToDB(tmpMovieArray)
                dbHelper.printDb()
            } else {
                println("Database is NOT empty")
                dbHelper.printDb()

                val tempLangArray = dbHelper.getMoviesArray()

                movieModel.clear()
                tempLangArray.forEach { movieModel.add(it) }
            }
        }

        setContent {
            val lazyListState = rememberLazyListState()

            Lab_2_to_7Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column {
                        AppBar(lazyListState = lazyListState, dbHelper = dbHelper)
                        MovieList(lazyListState = lazyListState, dbHelper = dbHelper)
                    }
                }
            }
        }
    }

    private fun pictureIsInt(picture: String): Boolean {
        val data = try {
            picture.toInt()
        } catch (e: NumberFormatException) {
            null
        }

        return data != null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val tmpMovieArray = ArrayList<Movie>()
        movieModel.movieListFlow.value.forEach { tmpMovieArray.add(it) }

        outState.putSerializable("movies", tmpMovieArray)

        super.onSaveInstanceState(outState)
    }

    @ExperimentalMaterial3Api
    @Composable
    fun AppBar(lazyListState: LazyListState, dbHelper: MovieDbHelper) {
        var isDropdownOpen by remember { mutableStateOf(false) }
        var isDialogOpen by remember { mutableStateOf(false) }

        val scope = rememberCoroutineScope()

        val startForResult =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                if (activityResult.resultCode == Activity.RESULT_OK) {
                    val newMovieItem = activityResult.data?.getSerializableExtra("newMovieItem") as Movie

                    movieModel.add(newMovieItem)
                    dbHelper.addMovie(newMovieItem)

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
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Reset to Defaults",
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            movieModel.clear()
                            dbHelper.clear()
                            movieModel.defaults()
                            isDropdownOpen = false
                        },
                        modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer)
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

    @OptIn(ExperimentalCoilApi::class, ExperimentalFoundationApi::class)
    @Composable
    fun MovieListItem(movie: Movie, movieListState: State<List<Movie>>, dbHelper: MovieDbHelper) {
        var isPopupOpen by remember { mutableStateOf(false) }
        var isDropdownOpen by remember { mutableStateOf(false) }


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

        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                if (activityResult.data?.data != null) {
                    val imgURI = activityResult.data?.data
                    movieModel.changeImage(movie, imgURI.toString())
                    dbHelper.changeImage(movie.name, imgURI.toString())
                }
            }

        Column(
            modifier = Modifier
                .combinedClickable(
                    onClick = { isPopupOpen = true },
                    onLongClick = { isDropdownOpen = true }
                )
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
                    painter = if (pictureIsInt(movie.picture)) painterResource(movie.picture.toInt())
                    else rememberImagePainter(movie.picture),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.height(150.dp).width(100.dp),
                )
                Column(verticalArrangement = Arrangement.SpaceBetween) {
                    MovieListItemRow("Genre", movie.genre)
                    Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                    MovieListItemRow("Director", movie.director)
                    Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                    MovieListItemRow("Company", movie.company)
                }
            }
            DropdownMenu(
                expanded = isDropdownOpen,
                onDismissRequest = { isDropdownOpen = false },
            ) {
                DropdownMenuItem(
                    text = { Text("Change Picture") },
                    onClick = {
                        isDropdownOpen = false

                        val grant =
                            ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_MEDIA_IMAGES)

                        if (grant != PackageManager.PERMISSION_GRANTED) {
                            val permissionList = arrayOfNulls<String>(1)
                            permissionList[0] = Manifest.permission.READ_MEDIA_IMAGES
                            ActivityCompat.requestPermissions(this@MainActivity, permissionList, 1)
                        }

                        val intent = Intent(
                            Intent.ACTION_OPEN_DOCUMENT,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        ).apply { addCategory(Intent.CATEGORY_OPENABLE) }

                        launcher.launch(intent)
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            "Delete Movie",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = {
                        isDropdownOpen = false
                        movieModel.remove(movie)
                        dbHelper.deleteMovie(movie.name)
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer)
                )
            }
        }
    }

    @Composable
    fun ColumnScope.MovieList(lazyListState: LazyListState, dbHelper: MovieDbHelper) {
        val movieListState = movieModel.movieListFlow.collectAsState()
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
        ) {
            items(movieModel.movieListFlow.value) { movie ->
                MovieListItem(
                    movie = movie,
                    movieListState = movieListState,
                    dbHelper = dbHelper,
                )
            }
        }
    }
}

