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
            .shadow(4.dp, RoundedCornerShape(2.dp))
            .background(WinampMetalMid, RoundedCornerShape(2.dp))
            .border(2.dp, WinampBorderHighlight)
            .border(1.dp, WinampBorderShadow)
            .padding(4.dp)
    ) {
        // Classic Title Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(WinampMetalDark)
                .padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title.uppercase(),
                color = WinampLcdGreen,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            if (isCollapsible) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(WinampButtonFace)
                        .border(1.dp, WinampBorderHighlight)
                        .clickable { onToggleCollapse?.invoke() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isCollapsed) "+" else "_",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        if (!isCollapsed) {
            content()
        }
    }
}
