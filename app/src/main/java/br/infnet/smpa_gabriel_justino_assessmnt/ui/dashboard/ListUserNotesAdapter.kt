package br.infnet.smpa_gabriel_justino_assessmnt.ui.dashboard


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import br.infnet.smpa_gabriel_justino_assessmnt.databinding.FragmentUsernoteItemBinding

import br.infnet.smpa_gabriel_justino_assessmnt.domain.UserNote
import java.text.DateFormat.getDateInstance
import java.text.SimpleDateFormat
import java.util.*

class ListUserNotesAdapter(
    private var listaNotasImg: List<UserNote>,
    val clickCallback:(Int)->Unit
) : RecyclerView.Adapter<ListUserNotesAdapter.ViewHolder>() {

    fun mudarLista(listaNotasImgNova: List<UserNote>){
        listaNotasImg = listaNotasImgNova
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val minhaBindingView = FragmentUsernoteItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)

        val imagemItemViewHolder = ViewHolder(minhaBindingView,clickCallback)

        return imagemItemViewHolder

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = listaNotasImg[position]

        holder.title.text = item.title

        val simpleDateFormat = getDateInstance()
        val dateString = simpleDateFormat.format(item.timestamp)

        holder.timestamp.text = dateString

    }

    override fun getItemCount(): Int = listaNotasImg.size

    inner class ViewHolder(binding: FragmentUsernoteItemBinding,
                           clickCallback:(Int)->Unit ) :
                            RecyclerView.ViewHolder(binding.root) {
        val title: TextView = binding.noteitemTitle
        val timestamp: TextView = binding.noteitemTimestamp
        val background: CardView = binding.cardvUsernoteBackground
        init{
            background.setOnClickListener {
                clickCallback(adapterPosition)
            }
        }

        override fun toString(): String {
            return super.toString() + " '"+title.text
        }

    }




}