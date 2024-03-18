package com.andforce.socket

sealed class MouseEvent {
    data object None : MouseEvent()
    data class Down(val x:Int, val y:Int, val remoteWidth: Int, val remoteHeight: Int) : MouseEvent()
    data class Up(val x:Int, val y:Int, val remoteWidth: Int, val remoteHeight: Int) : MouseEvent()
    data class Move(val x:Int, val y:Int, val remoteWidth: Int, val remoteHeight: Int) : MouseEvent()
}
