package br.infnet.smpa_gabriel_justino_assessmnt.domain

data class UserNote(
    val title:String?=null,
    val timestamp:Long?=null,
    val description:String?=null
){
    companion object{
        val timeStampDivider = "\n111111111111111111111111111111111date:\n"
    }
}
