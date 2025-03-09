package com.jordel.examenjoelprez.ListaProductos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.jordel.probafirebase.Producte
import com.jordel.probafirebase.R
import com.jordel.probafirebase.productoActual

class ProductesAdapter(private val mList: List<Producte>, private val vmProducto: productoActual) :
    RecyclerView.Adapter<ProductesAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_productes, parent, false)

        return ViewHolder(view)
    }


    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val producte = mList[position]


        holder.textViewNombre.text = producte.nombre
        holder.textViewCantidad.text = producte.cantiadad.toString()

        holder.itemView.setOnClickListener {
            vmProducto.setProducte(producte)
        }
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }


    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textViewNombre: TextView = itemView.findViewById(R.id.textViewNombreProducto)
        val textViewCantidad: TextView = itemView.findViewById(R.id.textViewCantidadProducto)
    }
}
