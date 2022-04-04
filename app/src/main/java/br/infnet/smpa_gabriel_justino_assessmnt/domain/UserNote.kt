package br.infnet.smpa_gabriel_justino_assessmnt.domain

import android.location.Location

data class UserNote(
    val title:String?=null,
    val timestamp:Long?=null,
    val description:String?=null,
    val location: Location?=null,
){
    companion object{
        val timeStampDivider = "\n111111111111111111111111111111111date:\n"
        val locationDivider = "\n111111111111111111111111111111111location:\n"
    }
}
