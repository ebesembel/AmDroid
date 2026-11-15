package com.amdroid.player.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amdroid.player.ui.theme.*

@Composable
fun AmDroidLogoBadge(
    modifier: Modifier = Modifier,
    height: Int = 18
) {
    Row(
        modifier = modifier
            .background(Color(0xFF090D15), RoundedCornerShape(4.dp))
            .border(1.dp, Color(0xFF0284C7), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Letra E (Blanco Brillante)
        Text(
            text = "E",
            color = Color.White,
            fontSize = (height * 0.65).sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.5).sp
        )

        Spacer(modifier = Modifier.width(3.dp))

        // Rayo Delgado Azul Eléctrico (Dibujado vectorialmente, CERO emojis)
        Canvas(modifier = Modifier.size(width = 10.dp, height = height.dp)) {
            val w = size.width
            val h = size.height

            val boltPath = Path().apply {
                moveTo(w * 0.58f, h * 0.10f)
                lineTo(w * 0.35f, h * 0.52f)
                lineTo(w * 0.55f, h * 0.52f)
                lineTo(w * 0.45f, h * 0.92f)
                lineTo(w * 0.78f, h * 0.46f)
                lineTo(w * 0.58f, h * 0.46f)
                close()
            }

            // Glow / Stroke
            drawPath(
                path = boltPath,
                color = Color(0xFF00F0FF)
            )
        }

        Spacer(modifier = Modifier.width(3.dp))

        // Letra B (Blanco Brillante)
        Text(
            text = "B",
            color = Color.White,
            fontSize = (height * 0.65).sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.5).sp
        )
    }
}
