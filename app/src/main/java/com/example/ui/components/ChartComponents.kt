package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangePrimaryVariant
import java.util.Locale

data class ChartSlice(
    val label: String,
    val value: Float,
    val color: Color
)

@Composable
fun DonutPieChartCard(
    title: String,
    slices: List<ChartSlice>,
    modifier: Modifier = Modifier
) {
    val total = remember(slices) { slices.sumOf { it.value.toDouble() }.toFloat() }
    var selectedSliceIndex by remember { mutableStateOf<Int?>(null) }

    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(slices) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(700))
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Donut Chart Canvas
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(slices) {
                                detectTapGestures { offset ->
                                    val center = Offset(size.width / 2f, size.height / 2f)
                                    val angle = (Math.toDegrees(
                                        Math.atan2(
                                            (offset.y - center.y).toDouble(),
                                            (offset.x - center.x).toDouble()
                                        )
                                    ).toFloat() + 360f + 90f) % 360f

                                    var cumulative = 0f
                                    selectedSliceIndex = null
                                    for ((index, slice) in slices.withIndex()) {
                                        val sweep = (slice.value / total) * 360f
                                        if (angle >= cumulative && angle <= cumulative + sweep) {
                                            selectedSliceIndex = index
                                            break
                                        }
                                        cumulative += sweep
                                    }
                                }
                            }
                    ) {
                        val strokeWidth = 26.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val topLeft = Offset(
                            (size.width - diameter) / 2f,
                            (size.height - diameter) / 2f
                        )
                        val arcSize = Size(diameter, diameter)

                        var startAngle = -90f
                        slices.forEachIndexed { index, slice ->
                            val sweepAngle = if (total > 0f) {
                                (slice.value / total) * 360f * animationProgress.value
                            } else 0f

                            val isSelected = selectedSliceIndex == index
                            val currentStroke = if (isSelected) strokeWidth + 6.dp.toPx() else strokeWidth

                            drawArc(
                                color = slice.color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = currentStroke, cap = StrokeCap.Butt)
                            )
                            startAngle += sweepAngle
                        }
                    }

                    // Center information
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val displaySlice = selectedSliceIndex?.let { slices.getOrNull(it) }
                        if (displaySlice != null) {
                            Text(
                                text = "${displaySlice.value.toInt()}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = displaySlice.color
                            )
                            Text(
                                text = displaySlice.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "${total.toInt()}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "TOTAL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Legend Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    slices.forEachIndexed { index, slice ->
                        val pct = if (total > 0f) (slice.value / total) * 100f else 0f
                        val isSelected = selectedSliceIndex == index

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isSelected) slice.color.copy(alpha = 0.15f) else Color.Transparent
                                )
                                .clickable {
                                    selectedSliceIndex = if (isSelected) null else index
                                }
                                .padding(vertical = 4.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(slice.color)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = slice.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${slice.value.toInt()} (${String.format(Locale.US, "%.0f", pct)}%)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveBarChartCard(
    title: String,
    data: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    val maxValue = remember(data) { (data.maxOfOrNull { it.second } ?: 1).coerceAtLeast(4) }
    var selectedBarIndex by remember { mutableStateOf<Int?>(null) }

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, tween(800))
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                selectedBarIndex?.let { idx ->
                    val item = data.getOrNull(idx)
                    if (item != null) {
                        Text(
                            text = "${item.first}: ${item.second} registered",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bar chart canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(data) {
                            detectTapGestures { offset ->
                                val barSpacing = size.width / data.size
                                val clickedIdx = (offset.x / barSpacing).toInt().coerceIn(0, data.size - 1)
                                selectedBarIndex = if (selectedBarIndex == clickedIdx) null else clickedIdx
                            }
                        }
                ) {
                    val height = size.height - 24.dp.toPx()
                    val width = size.width
                    val count = data.size
                    val slotWidth = width / count
                    val barWidth = slotWidth * 0.48f

                    // Grid lines
                    val gridColor = Color.Gray.copy(alpha = 0.15f)
                    for (step in 0..3) {
                        val y = height * (step / 3f)
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Draw bars
                    data.forEachIndexed { index, (label, value) ->
                        val barHeight = ((value.toFloat() / maxValue) * height) * animProgress.value
                        val x = (index * slotWidth) + (slotWidth - barWidth) / 2f
                        val y = height - barHeight

                        val isSelected = selectedBarIndex == index
                        val barColor = if (isSelected) {
                            OrangePrimaryVariant
                        } else if (value == maxValue) {
                            OrangePrimary
                        } else {
                            Color(0xFF3B82F6)
                        }

                        // Bar rectangle with rounded top
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(barColor, barColor.copy(alpha = 0.7f))
                            ),
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight.coerceAtLeast(4f)),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // X-axis labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                data.forEachIndexed { index, (label, value) ->
                    val isSelected = selectedBarIndex == index
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable {
                            selectedBarIndex = if (isSelected) null else index
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TrendLineChartCard(
    title: String,
    dataPoints: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    val maxVal = remember(dataPoints) { (dataPoints.maxOfOrNull { it.second } ?: 1).coerceAtLeast(3) }
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(dataPoints) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, tween(900))
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Cumulative Velocity",
                    fontSize = 11.sp,
                    color = OrangePrimaryVariant,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(dataPoints) {
                            detectTapGestures { offset ->
                                val spacing = size.width / (dataPoints.size - 1).coerceAtLeast(1)
                                val index = ((offset.x + spacing / 2) / spacing).toInt()
                                    .coerceIn(0, dataPoints.size - 1)
                                selectedPointIndex = if (selectedPointIndex == index) null else index
                            }
                        }
                ) {
                    if (dataPoints.size < 2) return@Canvas

                    val h = size.height - 18.dp.toPx()
                    val w = size.width
                    val stepX = w / (dataPoints.size - 1)

                    // Draw line path
                    val path = Path()
                    val fillPath = Path()

                    dataPoints.forEachIndexed { i, (_, v) ->
                        val x = i * stepX
                        val y = h - ((v.toFloat() / maxVal) * h * animProgress.value)
                        if (i == 0) {
                            path.moveTo(x, y)
                            fillPath.moveTo(x, h)
                            fillPath.lineTo(x, y)
                        } else {
                            path.lineTo(x, y)
                            fillPath.lineTo(x, y)
                        }
                    }

                    fillPath.lineTo(w, h)
                    fillPath.close()

                    // Draw area gradient
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                OrangePrimary.copy(alpha = 0.35f),
                                OrangePrimary.copy(alpha = 0.02f)
                            ),
                            startY = 0f,
                            endY = h
                        )
                    )

                    // Draw stroke line
                    drawPath(
                        path = path,
                        color = OrangePrimary,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw points
                    dataPoints.forEachIndexed { i, (_, v) ->
                        val x = i * stepX
                        val y = h - ((v.toFloat() / maxVal) * h * animProgress.value)
                        val isSelected = selectedPointIndex == i

                        drawCircle(
                            color = if (isSelected) OrangePrimaryVariant else Color.White,
                            radius = if (isSelected) 7.dp.toPx() else 4.dp.toPx(),
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = OrangePrimary,
                            radius = if (isSelected) 7.dp.toPx() else 4.dp.toPx(),
                            center = Offset(x, y),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dataPoints.forEachIndexed { index, (label, _) ->
                    val isSelected = selectedPointIndex == index
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
