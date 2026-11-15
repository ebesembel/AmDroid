package com.amdroid.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amdroid.player.ui.theme.*

@Composable
fun RetroWindowBox(
    title: String,
    modifier: Modifier = Modifier,
    isCollapsible: Boolean = false,
    isCollapsed: Boolean = false,
    onToggleCollapse: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(4.dp))
            .background(AmDroidMetalMid, RoundedCornerShape(4.dp))
            .border(1.dp, AmDroidBorderHighlight, RoundedCornerShape(4.dp))
            .padding(5.dp)
    ) {
        // Modernized Title Bar with Cyan & Blue Accents
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(AmDroidMetalDark, AmDroidMetalLight, AmDroidMetalDark)
                    ),
                    RoundedCornerShape(2.dp)
                )
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(2.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Mini Electric Lightning Bolt Accent
                Text(
                    text = "⚡",
                    color = AmDroidCyanGlow,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = title.uppercase(),
                    color = AmDroidCyanGlow,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            if (isCollapsible) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(AmDroidButtonFace, RoundedCornerShape(2.dp))
                        .border(1.dp, AmDroidBorderHighlight, RoundedCornerShape(2.dp))
                        .clickable { onToggleCollapse?.invoke() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isCollapsed) "+" else "−",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (!isCollapsed) {
            content()
        }
    }
}
