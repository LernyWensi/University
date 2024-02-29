package com.example.lab_2_to_7

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.lab_2_to_7.ui.theme.Lab_2_to_7Theme

class InputActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab_2_to_7Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Bottom) {
                        Menu()
                    }
                }
            }
        }
    }

    @Composable
    fun Menu() {
        var name by remember { mutableStateOf("") }
        var director by remember { mutableStateOf("") }
        var genre by remember { mutableStateOf("") }
        var filmCompany by remember { mutableStateOf("") }

        val handleClickEvent: () -> Unit = {
            val newMovieItem = Movie(name, director, genre, filmCompany)
            val intent = Intent()

            intent.putExtra("newMovieItem", newMovieItem)
            setResult(RESULT_OK, intent)
            finish()
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .padding(5.dp)
                .imePadding(),
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
                onClick = handleClickEvent,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Add Movie")
            }
        }
    }
}

