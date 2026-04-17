package com.stampcollect.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.util.*
import androidx.compose.ui.res.stringResource
import com.stampcollect.R
import com.stampcollect.util.DateFormat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.stampcollect.data.entity.StampEntity
import com.stampcollect.ui.theme.*
import com.stampcollect.ui.viewmodel.CollectionViewModel

@Composable
fun CalendarScreen(
    onDayClick: (Long) -> Unit, viewModel: CollectionViewModel = hiltViewModel()
) {
    val allStampsRes by viewModel.allStamps.collectAsState()
    val allStamps = allStampsRes.data() ?: emptyList()

    var currentMonthOffset by remember { mutableIntStateOf(0) }

    val currentCalendar = remember(currentMonthOffset) {
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, currentMonthOffset)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(top = 56.dp, bottom = 100.dp, start = 24.dp, end = 24.dp),
    ) {
        MonthItem(
            calendar = currentCalendar,
            stamps = allStamps,
            onDayClick = onDayClick,
            onPrevClick = { currentMonthOffset-- },
            onNextClick = { currentMonthOffset++ },
            isNextEnabled = currentMonthOffset < 0
        )
    }
}

@Composable
fun MonthItem(
    calendar: Calendar,
    stamps: List<StampEntity>,
    onDayClick: (Long) -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    isNextEnabled: Boolean
) {
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

    val todayCal = Calendar.getInstance()
    val isCurrentMonth = todayCal.get(Calendar.MONTH) == currentMonth && todayCal.get(Calendar.YEAR) == currentYear
    val todayDay = if (isCurrentMonth) todayCal.get(Calendar.DAY_OF_MONTH) else -1

    val activeStamps = remember(stamps, currentMonth, currentYear) {
        val stampMap = mutableMapOf<Int, String>()
        val sc = Calendar.getInstance()
        stamps.sortedBy { it.timestamp }.forEach { stamp ->
            sc.timeInMillis = stamp.timestamp
            if (sc.get(Calendar.MONTH) == currentMonth && sc.get(Calendar.YEAR) == currentYear) {
                val day = sc.get(Calendar.DAY_OF_MONTH)
                if (!stampMap.containsKey(day)) stampMap[day] = stamp.imagePath
            }
        }
        stampMap
    }

    val daysList = mutableListOf<Int?>()
    for (i in 0 until firstDayOfWeek) daysList.add(null)
    for (i in 1..maxDays) daysList.add(i)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val monthTitle = remember(calendar) { DateFormat.MONTH_ONLY.format(calendar.timeInMillis) }
            Text(
                text = monthTitle,
                style = MaterialTheme.typography.displayLarge,
                color = TextPrimary
            )
            Row {
                IconButton(onClick = onPrevClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.prev_month), tint = Primary)
                }
                IconButton(onClick = onNextClick, enabled = isNextEnabled) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, stringResource(R.string.next_month), tint = if (isNextEnabled) Primary else TextTertiary)
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$currentYear",
                style = MaterialTheme.typography.titleLarge,
                color = TextSecondary
            )

            // Statistics Summary
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatItem(label = stringResource(R.string.total).uppercase(), value = "${stamps.filter { 
                    val sCal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                    sCal.get(Calendar.MONTH) == currentMonth && sCal.get(Calendar.YEAR) == currentYear
                }.size}")
                Spacer(modifier = Modifier.width(16.dp))
                StatItem(label = stringResource(R.string.active_days).uppercase(), value = "${activeStamps.size}")
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Weekday labels
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            listOf(R.string.sun, R.string.mon, R.string.tue, R.string.wed, R.string.thu, R.string.fri, R.string.sat).forEach {
                Text(
                    stringResource(it).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        val rows = daysList.chunked(7)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { day ->
                    if (day == null) {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val isToday = day == todayDay
                        val stampPath = activeStamps[day]

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isToday) Primary.copy(alpha = 0.12f) else SurfaceSection)
                                .then(if (isToday) Modifier.border(1.5.dp, Primary, RoundedCornerShape(12.dp)) else Modifier)
                                .clickable {
                                    val clickCal = calendar.clone() as Calendar
                                    clickCal.set(Calendar.DAY_OF_MONTH, day)
                                    onDayClick(clickCal.timeInMillis)
                                }
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Text(
                                    day.toString(),
                                    style = if (isToday) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                                    color = if (isToday) Primary else TextPrimary,
                                    textAlign = TextAlign.Center
                                )

                                if (stampPath != null) {
                                    val file = File(stampPath)
                                    if (file.exists()) {
                                        Image(
                                            painter = rememberAsyncImagePainter(model = Uri.fromFile(file)),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp).padding(top = 2.dp).clip(RoundedCornerShape(4.dp)).background(TertiaryFixed).padding(1.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                if (row.size < 7) {
                    for (i in 0 until (7 - row.size)) {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.End) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = Primary, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
    }
}
