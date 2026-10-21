package com.banglamotion.studio.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglamotion.studio.theme.*
import com.example.data.model.MotionProject
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProjectsScreen(
    projectsList: List<MotionProject>,
    onOpenProject: (MotionProject) -> Unit,
    onDeleteProject: (MotionProject) -> Unit,
    onCreateNewProject: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sortByDate by remember { mutableStateOf(true) }

    val sortedProjects = remember(projectsList, sortByDate) {
        if (sortByDate) {
            projectsList.sortedByDescending { it.updatedAt }
        } else {
            projectsList.sortedBy { it.title }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBgBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your Projects",
                        color = TextWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "${projectsList.size} Saved Projects • Room DB",
                        color = TextGrayMuted,
                        fontSize = 12.sp
                    )
                }

                // Sort toggle
                TextButton(
                    onClick = { sortByDate = !sortByDate },
                    colors = ButtonDefaults.textButtonColors(contentColor = OrangePrimary)
                ) {
                    Text(if (sortByDate) "Sort: Date ▾" else "Sort: Name ▾", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (sortedProjects.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = "No projects",
                            tint = Color(0xFF4A1010),
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "No saved projects yet",
                            color = TextGrayMuted,
                            fontSize = 14.sp
                        )
                        Button(
                            onClick = onCreateNewProject,
                            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                        ) {
                            Text("Create 1080x1920 Project", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(sortedProjects) { project ->
                        ProjectCardItem(
                            project = project,
                            onClick = { onOpenProject(project) },
                            onDelete = { onDeleteProject(project) }
                        )
                    }
                }
            }
        }

        // Green + FAB at bottom right to create new project
        FloatingActionButton(
            onClick = onCreateNewProject,
            containerColor = GreenFAB,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(58.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Project", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun ProjectCardItem(
    project: MotionProject,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val dateStr = remember(project.updatedAt) { dateFormat.format(Date(project.updatedAt)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CardBorderGradient, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = StudioCardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini 9:16 Thumbnail preview
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(72.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1F0606))
                    .border(1.dp, RedPrimary, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = project.text.take(4),
                    color = OrangeYellow,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.title,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Badges row: 9:16 badge, 1080p, 30fps
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ProjectBadge(text = project.aspectRatio)
                    ProjectBadge(text = "1080p")
                    ProjectBadge(text = "30fps")
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$dateStr • ${project.durationMs / 1000}s • Room DB",
                    color = TextGrayMuted,
                    fontSize = 11.sp
                )
            }

            // Delete action
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFF6B2020)
                )
            }
        }
    }
}

@Composable
fun ProjectBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF2E0909))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text, color = OrangePrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}
