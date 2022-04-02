package br.infnet.smpa_gabriel_justino_assessmnt.services

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import br.infnet.smpa_gabriel_justino_assessmnt.domain.UserNote
import java.io.File

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
                val name:String = f.getName()
                filesNames.add(name)
                val unEncryptedString = readEncryptFile(context,name)
                val splitedString = unEncryptedString.split(UserNote.timeStampDivider)
                var note:UserNote
                if(splitedString.size==2){
                    note = UserNote(name, splitedString[1].trim().toLong(), splitedString[0])
                    userNotes.add(note)
                }


            }
        }
        return userNotes
    }
}