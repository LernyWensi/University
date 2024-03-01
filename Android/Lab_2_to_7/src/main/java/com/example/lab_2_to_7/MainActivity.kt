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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
                        AppBar(
                            lazyListState = lazyListState,
                            dbHelper = dbHelper,
                            movieModel = movieModel
                        )
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val tmpMovieArray = ArrayList<Movie>()
        movieModel.movieListFlow.value.forEach { tmpMovieArray.add(it) }

        outState.putSerializable("movies", tmpMovieArray)

        super.onSaveInstanceState(outState)
    }
}

fun pictureIsInt(picture: String): Boolean {
    val data = try {
        picture.toInt()
    } catch (e: NumberFormatException) {
        null
    }

    return data != null
}


@ExperimentalMaterial3Api
@Composable
fun AppBar(lazyListState: LazyListState, dbHelper: MovieDbHelper, movieModel: MoviesViewModel) {
    val context = LocalContext.current

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
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
            stringResource(R.string.about),
            text = "John Q. Public",
        ) { isDialogOpen = false }
    }

    TopAppBar(
        title = { Text(stringResource(R.string.movies)) },
        navigationIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        if (drawerState.isClosed) drawerState.open()
                        else drawerState.close()
                    }
                },
            ) {
                Icon(
                    Icons.Rounded.Menu,
                    contentDescription = ""
                )
            }
        },
        actions = {
            IconButton(onClick = { isDropdownOpen = true }) {
                Icon(Icons.Default.MoreVert, null)
            }
            DropdownMenu(
                expanded = isDropdownOpen,
                onDismissRequest = { isDropdownOpen = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.about)) },
                    onClick = {
                        isDropdownOpen = false
                        isDialogOpen = true
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.add_new_movie)) },
                    onClick = {
                        val intent = Intent(context, InputActivity::class.java)
                        startForResult.launch(intent)
                        isDropdownOpen = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(R.string.reset_to_defaults),
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text(stringResource(R.string.drawing)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        val intent = Intent(context, DrawingActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        },
        content = {
            Column {
                MovieList(
                    lazyListState = lazyListState,
                    dbHelper = dbHelper,
                    movieModel = movieModel
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
                Text(stringResource(R.string.close))
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
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(75.dp),
        )
        Divider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier
                .height(15.dp)
                .width(1.dp),
        )
        Text(
            description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f)
                .padding(start = 15.dp)
        )
    }
}

@OptIn(ExperimentalCoilApi::class, ExperimentalFoundationApi::class)
@Composable
fun MovieListItem(
    movie: Movie,
    dbHelper: MovieDbHelper,
    movieModel: MoviesViewModel
) {
    val context = LocalContext.current

    var isPopupOpen by remember { mutableStateOf(false) }
    var isDropdownOpen by remember { mutableStateOf(false) }

    val resourceId = context.resources.getIdentifier(
        movie.name.lowercase().replace(" ", "_"),
        "string",
        context.packageName
    )

    if (isPopupOpen) {
        Popup(
            movie.name,
            if (resourceId != 0) stringResource(resourceId) else stringResource(R.string.no_description)
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
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(10.dp),
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
                MovieListItemRow(stringResource(R.string.genre), movie.genre)
                Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                MovieListItemRow(stringResource(R.string.director), movie.director)
                Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                MovieListItemRow(stringResource(R.string.film_company), movie.company)
            }
        }
        DropdownMenu(
            expanded = isDropdownOpen,
            onDismissRequest = { isDropdownOpen = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.change_picture)) },
                onClick = {
                    isDropdownOpen = false

                    val grant =
                        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)

                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        val permissionList = arrayOfNulls<String>(1)
                        permissionList[0] = Manifest.permission.READ_EXTERNAL_STORAGE
                        ActivityCompat.requestPermissions(context as Activity, permissionList, 1)
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
                        stringResource(R.string.delete_movie),
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
fun ColumnScope.MovieList(lazyListState: LazyListState, dbHelper: MovieDbHelper, movieModel: MoviesViewModel) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f),
    ) {
        items(
            items = movieModel.movieListFlow.value,
            key = { movie -> movie.name },
            itemContent = { movie ->
                MovieListItem(movie, dbHelper, movieModel)
            }
        )
    }
}

