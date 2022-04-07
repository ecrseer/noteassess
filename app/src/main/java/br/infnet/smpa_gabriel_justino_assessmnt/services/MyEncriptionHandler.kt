package br.infnet.smpa_gabriel_justino_assessmnt.services

import android.content.Context
import android.location.Location
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import br.infnet.smpa_gabriel_justino_assessmnt.domain.UserNote
import java.io.File
import java.lang.Double

class MyEncriptionHandler(
    private val email: String
) {
    class MyFileEncrypt(val builder: EncryptedFile, val path: String)

    fun getMasterKeyByString(string: String, context: Context): MasterKey {
        return MasterKey.Builder(context, string)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build();
    }

    private fun getEncryptFileTaskPointer(
        context: Context,
        chosenFileName: String,
        shouldCreateNewFile:Boolean
    ): MyFileEncrypt {
        val filePointer = File(context.filesDir, chosenFileName)
        if (shouldCreateNewFile && filePointer.exists())
            filePointer.delete()

        val masterkey = getMasterKeyByString(email, context)

        val encryptFileTask = EncryptedFile.Builder(
            context, filePointer, masterkey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        )
            .build()
        val bundle = MyFileEncrypt(encryptFileTask, filePointer.path)
        return bundle
    }


    fun insertEncryptFile(
        context: Context, chosenFileName: String,
        textToWrite: String
    ): String {
        val encryptFileTask =
            getEncryptFileTaskPointer(context, chosenFileName,true)

        val bytesOutput = encryptFileTask.builder.openFileOutput()
        bytesOutput.write(
            textToWrite.toByteArray()
        )
        bytesOutput.close()
        return encryptFileTask.path

    }

    fun readEncryptFile(
        context: Context, chosenFileName: String
    ): String {
        val encryptFileTask =
            getEncryptFileTaskPointer(context, chosenFileName,false)

        val bytesInput = encryptFileTask.builder.openFileInput()
        val bytes = bytesInput.readBytes()
        bytesInput.close()
        return String(bytes)
    }
    fun listFiles(context:Context): MutableList<UserNote> {

        val filePointer = File(context.filesDir,"")
        val filesNames = mutableListOf<String>()
        val userNotes = mutableListOf<UserNote>()
        for (f:File in filePointer.listFiles()) {
            if (f.isFile){
                val name:String = f.name
                filesNames.add(name)
                val unEncryptedString = readEncryptFile(context,name)

                var note = tryGetUserNoteByString(unEncryptedString,f)
                note?.let {
                    userNotes.add(it)
                }


            }
        }
        userNotes.sortByDescending { it.timestamp }
        return userNotes
    }
    private fun getLocationByString(txt: String): Location {
        val location = Location("")

        fun getFirstDouble(key:String): kotlin.Double? {
            try {


                val regex = """$key:(-?\d*\.{0,1}\d{1,20})""".toRegex()
                val labelAndNumberArray = regex.find(txt)?.value?.split(":")

                labelAndNumberArray?.get(1)?.trim()?.let { number->
                    if(number!=null) return number.toDouble()
                }
                return null
            }catch (e:Exception){
                return null
            }
        }
        val lat = getFirstDouble("latitude")
        val lon = getFirstDouble("longitude")
        if(lat!=null && lon !=null) {
            location.latitude = lat
            location.longitude = lon
        }
        return location
    }

    private fun tryGetUserNoteByString(
        unEncryptedString:String,
        f: File,
    ): UserNote? {
        try{
            var note=UserNote()
            val descriptionAndTimestamp = unEncryptedString.split(UserNote.timeStampDivider)
            if (descriptionAndTimestamp.size == 2) {
                var timestampAndLocation =  descriptionAndTimestamp[1].split(UserNote.locationDivider)
                val description = descriptionAndTimestamp[0]
                val timestamp = timestampAndLocation[0].trim().toLong()
                val locationTxt= timestampAndLocation[1]
                val local = getLocationByString(timestampAndLocation[1])

                note = UserNote(f.nameWithoutExtension,timestamp,description, local)
            }
            return note;
        }catch(except:Exception) {
            println(except.message)
            return null
        }

    }
}