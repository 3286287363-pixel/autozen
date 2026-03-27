package com.autozen.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClockWidget(
    modifier: Modifier = Modifier,
    timeColor: Color = Color.White,
    dateColor: Color = Color.Gray
) {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            now = System.currentTimeMillis()
        }
    }

    val timeFmt = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val dateFmt = remember { SimpleDateFormat("MM月dd日 E", Locale.CHINESE) }

    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = timeFmt.format(Date(now)),
            fontSize = 28.sp,
            fontWeight = FontWeight.Light,
            color = timeColor,
            letterSpacing = 2.sp
        )
        Text(
            text = dateFmt.format(Date(now)),
            fontSize = 13.sp,
            color = dateColor
        )
    }
}
