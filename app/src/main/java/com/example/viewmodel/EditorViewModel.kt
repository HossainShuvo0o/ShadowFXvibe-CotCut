package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.EditorState
import com.example.data.model.MotionProject
import com.example.data.repository.MotionProjectRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditorViewModel(
    private val repository: MotionProjectRepository
) : ViewModel() {

    private val _editorState = MutableStateFlow(EditorState())
    val editorState: StateFlow<EditorState> = _editorState.asStateFlow()

    // Undo / Redo stacks
    private val undoStack = mutableListOf<EditorState>()
    private val redoStack = mutableListOf<EditorState>()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    // Playback state
    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTimeMs = MutableStateFlow(0f)
    val currentTimeMs: StateFlow<Float> = _currentTimeMs.asStateFlow()

    private var playbackJob: Job? = null

    init {
        startPlaybackLoop()
    }

    private fun startPlaybackLoop() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val frameIntervalMs = 16L // ~60 FPS
            while (true) {
                if (_isPlaying.value) {
                    val state = _editorState.value
                    val totalDuration = (state.durationMs + state.delayMs).toFloat().coerceAtLeast(100f)
                    val nextTime = _currentTimeMs.value + (frameIntervalMs * state.speed)

                    if (nextTime >= totalDuration) {
                        if (state.isLoop) {
                            _currentTimeMs.value = 0f
                        } else {
                            _currentTimeMs.value = totalDuration
                            _isPlaying.value = false
                        }
                    } else {
                        _currentTimeMs.value = nextTime
                    }
                }
                delay(frameIntervalMs)
            }
        }
    }

    fun updateState(newState: EditorState, pushToHistory: Boolean = true) {
        if (pushToHistory) {
            undoStack.add(_editorState.value)
            if (undoStack.size > 30) undoStack.removeAt(0)
            redoStack.clear()
            _canUndo.value = true
            _canRedo.value = false
        }
        _editorState.value = newState
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val previous = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(_editorState.value)
            _editorState.value = previous
            _canUndo.value = undoStack.isNotEmpty()
            _canRedo.value = true
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(_editorState.value)
            _editorState.value = next
            _canUndo.value = true
            _canRedo.value = redoStack.isNotEmpty()
        }
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun replay() {
        _currentTimeMs.value = 0f
        _isPlaying.value = true
    }

    fun seekTo(timeMs: Float) {
        val total = (_editorState.value.durationMs + _editorState.value.delayMs).toFloat()
        _currentTimeMs.value = timeMs.coerceIn(0f, total)
    }

    fun toggleLoop() {
        val updated = _editorState.value.copy(isLoop = !_editorState.value.isLoop)
        updateState(updated, pushToHistory = false)
    }

    fun setSpeed(speed: Float) {
        val updated = _editorState.value.copy(speed = speed)
        updateState(updated, pushToHistory = false)
    }

    fun setAspectRatio(ratio: String) {
        val updated = _editorState.value.copy(aspectRatio = ratio)
        updateState(updated)
    }

    fun applyTemplate(templateState: EditorState) {
        updateState(templateState.copy(projectId = _editorState.value.projectId))
        replay()
    }

    fun loadProject(projectId: Long) {
        viewModelScope.launch {
            val project = repository.getProjectById(projectId)
            if (project != null) {
                undoStack.clear()
                redoStack.clear()
                _canUndo.value = false
                _canRedo.value = false
                _editorState.value = EditorState.fromMotionProject(project)
                replay()
            }
        }
    }

    fun createNewProject() {
        undoStack.clear()
        redoStack.clear()
        _canUndo.value = false
        _canRedo.value = false
        _editorState.value = EditorState()
        replay()
    }

    fun saveProject(onSaved: (Long) -> Unit) {
        viewModelScope.launch {
            val currentState = _editorState.value
            val project = currentState.toMotionProject()
            val savedId = repository.saveProject(project)
            _editorState.value = currentState.copy(projectId = savedId)
            onSaved(savedId)
        }
    }
}
