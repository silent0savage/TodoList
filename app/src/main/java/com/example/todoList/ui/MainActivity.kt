package com.example.todoList.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton


import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoList.viewmodel.TaskViewModel
import com.example.todoList.ui.theme.TasksListsTheme
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

enum class ThemeMode { SYSTEM, LIGHT, DARK }

object ThemePreferences {
    private val _themeMode = mutableStateOf(ThemeMode.SYSTEM)
    val themeMode = _themeMode

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }
}

private fun Color.luminance() : Float {
    return 0.299f * red + 0.587f * green + 0.114f * blue
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val vm: TaskViewModel = viewModel()

            val themeMode by ThemePreferences.themeMode

            TasksListsTheme(themeMode = themeMode) {
                val systemBarsColor = MaterialTheme.colorScheme.surfaceContainer
                val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

                SideEffect {
                    val window = this@MainActivity.window
                    window.statusBarColor = systemBarsColor.toArgb()
                    window.navigationBarColor = systemBarsColor.toArgb()

                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        isAppearanceLightStatusBars = !isDark
                        isAppearanceLightNavigationBars = !isDark
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = { vm.finishEdit() })
                            }
                    ) {
                        Header(
                            themeMode = themeMode,
                            onThemeModeChange = { ThemePreferences.setThemeMode(it) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        TasksList(vm, Modifier.weight(1f))
                        Box(
                            modifier = Modifier.imePadding()
                        ) {
                            AddTaskUI(vm)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.Absolute.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .statusBarsPadding()

    ) {
        Text(
            text = "Ставьте задачи",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        Box {
            IconButton(onClick = { menuOpen = true }) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
            }

            DropdownMenu(
                expanded = menuOpen,
                onDismissRequest = { menuOpen = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Система") },
                    onClick = { onThemeModeChange(ThemeMode.SYSTEM); menuOpen = false }
                )
                DropdownMenuItem(
                    text = { Text("Светлая") },
                    onClick = { onThemeModeChange(ThemeMode.LIGHT); menuOpen = false }
                )
                DropdownMenuItem(
                    text = { Text("Тёмная") },
                    onClick = { onThemeModeChange(ThemeMode.DARK); menuOpen = false }
                )
            }
        }
    }
}

@Composable
fun TasksList(vm: TaskViewModel, modifier: Modifier = Modifier) {
    val tasks by vm.tasks.collectAsState(initial = emptyList())
    val lazyListState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(lazyListState) { from, to ->
        vm.moveTask(from.index, to.index)
    }
    LazyColumn(
        state = lazyListState,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        if (tasks.isEmpty()) {
            item {
                Text(
                    text = "Задач пока нет.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            itemsIndexed(
                items = tasks,
                key = { _, task -> task.id }
            ) { index, task ->
                ReorderableItem(reorderState, key = task.id) { isDragging ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .longPressDraggableHandle()
                    ) {
                        SwipeToDismissBox(
                            state = rememberSwipeToDismissBoxState(
                                confirmValueChange = {
                                    if (it == SwipeToDismissBoxValue.EndToStart) {
                                        vm.deleteTask(task)
                                        true
                                    } else false
                                }
                            ),
                            backgroundContent = {
                                // Material You background + delete affordance
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.errorContainer)
                                        .padding(end = 16.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            },
                            content = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.surface)
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outlineVariant,
                                            MaterialTheme.shapes.medium
                                        )
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "${index + 1}.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )

                                    if (vm.editingId == task.id) {
                                        TextField(
                                            value = vm.editingText,
                                            onValueChange = vm::onEditTextChange,
                                            singleLine = true,
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                                unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
                                                cursorColor = MaterialTheme.colorScheme.primary
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                    } else {
                                        Text(
                                            text = task.name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { vm.startEdit(task) }
                                                .padding(vertical = 6.dp)
                                        )
                                    }

                                    Checkbox(
                                        checked = task.isCompleted,
                                        onCheckedChange = { vm.toggleTask(task) }
                                    )
                                }
                            },
                            enableDismissFromStartToEnd = false,
                            enableDismissFromEndToStart = true,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddTaskUI(vm: TaskViewModel) {
    var inputText by remember { mutableStateOf("") }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .navigationBarsPadding()
    ) {
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            placeholder = {
                Text(
                    text = "Введите задачу",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.weight(1f)
        )

        // Square, Material-colored button
        FilledIconButton(
            onClick = {
                vm.addTask(inputText)
                inputText = ""
            },
            enabled = inputText.isNotBlank(),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier
                .padding(start = 8.dp)
                .height(56.dp)
                .aspectRatio(1f)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
        }
    }
}