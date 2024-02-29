package com.example.lab_1

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lab_1.ui.theme.Lab_1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab_1Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Matrix()
                }
            }
        }
    }

    private fun calculateDeterminant(x1: Float, x2: Float, x3: Float, x4: Float): Float {
        return (x1 * x3) - (x2 * x4)
    }

    @Composable
    fun RowScope.MatrixInput(value: String, valueChangeHandler: (String) -> Unit, label: String) {
        TextField(
            modifier = Modifier.weight(1f),
            value = value,
            onValueChange = valueChangeHandler,
            label = { Text(label) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
            )
        )
    }

    @Composable
    fun Matrix() {
        val (x1, setX1) = remember { mutableStateOf("") }
        val (x2, setX2) = remember { mutableStateOf("") }
        val (x3, setX3) = remember { mutableStateOf("") }
        val (x4, setX4) = remember { mutableStateOf("") }

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    MatrixInput(x1, setX1, "X1")
                    MatrixInput(x2, setX2, "X2")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    MatrixInput(x3, setX3, "X3")
                    MatrixInput(x4, setX4, "X4")
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val determinant = calculateDeterminant(x1.toFloat(), x2.toFloat(), x4.toFloat(), x3.toFloat())
                        val toast = Toast.makeText(this@MainActivity, "Determinant is $determinant", Toast.LENGTH_LONG)
                        toast.show()
                    }
                ) {
                    Text("Calculate")
                }
            }
        }
    }
}

