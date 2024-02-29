package com.example.lab_1

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lab_1.ui.theme.Lab_1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab_1Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MakeUI(this)
                }
            }
        }
    }
}

fun calculateDeterminant(x1: Float, x2: Float, x3: Float, x4: Float): Float {
    return (x1 * x3) - (x2 * x4)
}

@Composable
fun MakeUI(ctx: MainActivity) {
    var x1 by remember { mutableStateOf("") }
    var x2 by remember { mutableStateOf("") }
    var x3 by remember { mutableStateOf("") }
    var x4 by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.padding(10.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                TextField(
                    modifier = Modifier.weight(1f),
                    value = x1,
                    onValueChange = { x1 = it },
                    placeholder = { Text("X1") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                TextField(
                    modifier = Modifier.weight(1f),
                    value = x2,
                    onValueChange = { x2 = it },
                    placeholder = { Text("X2") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                TextField(
                    modifier = Modifier.weight(1f),
                    value = x3,
                    onValueChange = { x3 = it },
                    placeholder = { Text("X3") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                TextField(
                    modifier = Modifier.weight(1f),
                    value = x4,
                    onValueChange = { x4 = it },
                    placeholder = { Text("X4") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val determinant = calculateDeterminant(x1.toFloat(), x2.toFloat(), x4.toFloat(), x3.toFloat())
                    val toast = Toast.makeText(ctx, "Determinant is $determinant", Toast.LENGTH_LONG)
                    toast.show()
                }
            ) {
                Text("Calculate")
            }
        }
    }
}
