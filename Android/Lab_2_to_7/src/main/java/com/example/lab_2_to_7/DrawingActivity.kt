package com.example.lab_2_to_7

import CanvasViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.lab_2_to_7.ui.theme.Lab_2_to_7Theme

class DrawingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val myView = CanvasViewModel(applicationContext)
            val viewRemember = remember {
                mutableStateOf(myView)
            }

            val buttonNames =
                arrayOf(
                    stringResource(R.string.rect),
                    stringResource(R.string.circle),
                    stringResource(R.string.image),
                    stringResource(R.string.save),
                )

            Lab_2_to_7Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Menu(buttonNames, viewRemember.value)
                        Canvas(viewRemember.value)
                    }
                }
            }
        }
    }
}

@Composable
fun Menu(buttonNames: Array<String>, myView: CanvasViewModel?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
        modifier = Modifier.fillMaxWidth().padding(5.dp)
    ) {
        buttonNames.forEach {
            Button(
                shape = ShapeDefaults.Small,
                onClick = { myView!!.funcArray[buttonNames.lastIndexOf(it)]() },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = it,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun Canvas(myView: CanvasViewModel?) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { myView!! },
    )
}