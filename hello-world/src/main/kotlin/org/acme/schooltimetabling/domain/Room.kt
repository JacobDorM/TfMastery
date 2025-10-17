package org.acme.schooltimetabling.domain
// Info: problem fact resource
data class Room(
    val name: String) {

    override fun toString(): String = name

}