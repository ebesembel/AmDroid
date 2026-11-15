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
    // High-Tech Cyber Studio Border Gradient (Cyan Neon -> Electric Blue -> Deep Gunmetal)
    val laserBorderGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF00F0FF), // Cyan Neón brillante
            Color(0xFF0284C7), // Azul Eléctrico
            Color(0xFF1E293B), // Titanio Oscuro
            Color(0xFF0284C7),
            Color(0xFF00F0FF)
        )
    )

    val chassisBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF161C27), // Titanio cepillado superior
            Color(0xFF0E121A)  // Obsidiana profunda base
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            // Ambient Neon Cyan Underglow
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = Color(0x6600F0FF),
                ambientColor = Color(0x330284C7)
            )
            .background(chassisBackground, RoundedCornerShape(8.dp))
            // Striking High-Tech Border
            .border(1.5.dp, laserBorderGradient, RoundedCornerShape(8.dp))
            .padding(6.dp)
    ) {
        // Modern Studio Rackmount Title Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF0B0F17),
                            Color(0xFF1C2433),
                            Color(0xFF0B0F17)
                        )
                    ),
                    RoundedCornerShape(4.dp)
                )
                .border(1.dp, Color(0xFF283548), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Nuevo Logo Oficial E ⚡ B (Vectorial en azul eléctrico, CERO emojis anaranjados)
                AmDroidLogoBadge(height = 16)

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title.uppercase(),
                    color = AmDroidCyanBright,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            if (isCollapsible) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(Color(0xFF1A2230), RoundedCornerShape(3.dp))
                        .border(1.dp, Color(0xFF00F0FF), RoundedCornerShape(3.dp))
                        .clickable { onToggleCollapse?.invoke() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isCollapsed) "+" else "−",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Indicador de Estado LED Cyan
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF00F0FF), RoundedCornerShape(4.dp))
                        .shadow(4.dp, RoundedCornerShape(4.dp), spotColor = Color(0xFF00F0FF))
                )
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        if (!isCollapsed) {
            content()
        }
    }
}
