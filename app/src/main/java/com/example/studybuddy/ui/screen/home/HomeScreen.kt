package com.example.studybuddy.ui.screen.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.studybuddy.domain.model.Homework
import com.example.studybuddy.domain.model.ScheduleEntry
import com.example.studybuddy.enums.HomeworkStatus
import com.example.studybuddy.ui.component.NavigationBarComponent
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModal: HomeViewModal = HomeViewModal()
) {
    val top3Homework by viewModal.top3Homework.collectAsState(emptyList())
    val todaySchedule by viewModal.getTodaySchedule().collectAsState(emptyList())
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home Page") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            )
        },
        bottomBar = { NavigationBarComponent(navController) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeworkSection(
                    homeworkList = top3Homework
                )

                ScheduleSection(
                    todaySchedule = todaySchedule
                )
            }
        }
    }
}

@Composable
fun HomeworkSection(homeworkList: List<Homework>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Upcoming Homework",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (homeworkList.isEmpty()) {
            Text(
                text = "No upcoming homework",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .alpha(0.4f)
            )
        } else {
            homeworkList.forEach { homework ->
                HomeworkItem(homework = homework)
            }
        }
    }
}

@Composable
fun HomeworkItem(homework: Homework) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
    val formattedDueDate = homework.dueDate.format(formatter)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = homework.subject, style = MaterialTheme.typography.titleSmall)
            Text(
                text = "Status: ${homework.status}",
                style = MaterialTheme.typography.bodySmall,
                color = if (homework.status == HomeworkStatus.COMPLETED.displayName) Color.Green else Color.Red
            )
            Text(
                text = "Due Date: $formattedDueDate",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ScheduleSection(todaySchedule: List<ScheduleEntry>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Today's Schedule",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (todaySchedule.isEmpty()) {
            Text(
                text = "No classes scheduled for today",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .alpha(0.4f)
            )
        } else {
            todaySchedule.forEach { scheduleItem ->
                ScheduleItem(schedule = scheduleItem)
            }
        }
    }
}

@Composable
fun ScheduleItem(schedule: ScheduleEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${schedule.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${
                    schedule.endTime.format(
                        DateTimeFormatter.ofPattern("HH:mm")
                    )
                } | ${schedule.subject}",
                style = MaterialTheme.typography.bodySmall
            )
            if (schedule.link?.isNotEmpty() == true) {
                val context = LocalContext.current
                val annotatedLinkString = buildAnnotatedString {
                    append("Open Link")
                    addStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onSecondary,
                            textDecoration = TextDecoration.Underline
                        ),
                        start = 0,
                        end = 9
                    )
                    addStringAnnotation(
                        tag = "URL",
                        annotation = schedule.link,
                        start = 0,
                        end = 9
                    )
                }

                Text(
                    text = annotatedLinkString,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(schedule.link)).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

