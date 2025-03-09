package com.jordel.probafirebase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.jordel.examenjoelprez.ListaProductos.ProductesAdapter
import com.jordel.probafirebase.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private val vmUser: VMuser by activityViewModels()
    private val vmProducto: productoActual by activityViewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ProductesAdapter
    private val arrayProductes = ArrayList<Producte>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {})
        }
        vmProducto.producte.observe(viewLifecycleOwner) { producte ->
            binding.editTextNombreP.setText(producte.nombre)
            binding.editTextCantidadP.setText(producte.cantiadad.toString())
        }
        db.firestoreSettings = settings

        val recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ProductesAdapter(arrayProductes, vmProducto)
        recyclerView.adapter = adapter

        cargarDatos()

        binding.buttonDelete.setOnClickListener {
            val productoId = vmProducto.producte.value?.id ?: ""

            if (productoId.isNotEmpty()) {
                db.collection("productos").document(vmUser.user).update("productos.$productoId", FieldValue.delete())
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Producto borrado", Toast.LENGTH_SHORT).show()
                        cargarDatos()
                        vmProducto.clearProducte()
                    }
                    .addOnFailureListener { exception: Exception ->
                        println("Error borrando el producto: ${exception.message}")
                        Toast.makeText(requireContext(), "Error al borrar el producto", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Selecciona un producto para borrar", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonUpdate.setOnClickListener {
            val nombre = binding.editTextNombreP.text.toString()
            val cantidadString = binding.editTextCantidadP.text.toString()
            val productoId = vmProducto.producte.value?.id ?: ""
            if (nombre.isNotEmpty() && cantidadString.isNotEmpty() && productoId.isNotEmpty()) {
                try {
                    val cantidad = cantidadString.toInt()
                    if (cantidad > 0) {
                        val updates = mapOf(
                            "productos.$productoId.nombre" to nombre,
                            "productos.$productoId.cantidad" to cantidad
                        )
                        db.collection("productos").document(vmUser.user).update(updates)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Producto actualizado", Toast.LENGTH_SHORT).show()
                                cargarDatos()
                                vmProducto.clearProducte()
                            }
                            .addOnFailureListener { exception: Exception ->
                                println("Error actualizando el documento: ${exception.message}")
                                Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(requireContext(), "La cantidad debe ser mayor que 0", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(requireContext(), "Cantidad no válida", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonSave.setOnClickListener {
            val nombre = binding.editTextNombreP.text.toString()
            val cantidadString = binding.editTextCantidadP.text.toString()
            if (nombre.isNotEmpty() && cantidadString.isNotEmpty()) {
                try {
                    val cantidad = cantidadString.toInt()
                    if (cantidad > 0) {
                        db.runTransaction { transaction ->
                            val documentSnapshot = transaction.get(db.collection("productos").document(vmUser.user))
                            val contadorActual = documentSnapshot.getLong("contador") ?: 0
                            val nuevoContador = contadorActual + 1
                            val nuevoProducto = mapOf("nombre" to nombre, "cantidad" to cantidad)
                            val productosActuales = documentSnapshot.get("productos") as? MutableMap<String, Map<String, Any>> ?: mutableMapOf()
                            productosActuales[nuevoContador.toString()] = nuevoProducto
                            transaction.update(db.collection("productos").document(vmUser.user), mapOf(
                                "contador" to nuevoContador,
                                "productos" to productosActuales
                            ))
                            null
                        }.addOnSuccessListener {
                            Toast.makeText(requireContext(), "Producto guardado", Toast.LENGTH_SHORT).show()
                            cargarDatos()
                            vmProducto.clearProducte()
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), "Error al guardar", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "La cantidad debe ser mayor que 0", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(requireContext(), "Cantidad no válida", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }
    private fun cargarDatos() {
        db.collection("productos").document(vmUser.user).get()
            .addOnSuccessListener { document: DocumentSnapshot? ->
                arrayProductes.clear()
                if (document != null && document.exists()) {
                    val datos = document.data
                    if (datos != null && datos.containsKey("productos")) {
                        val productosMap = datos["productos"] as? Map<String, Map<String, Any>>
                        productosMap?.forEach { (id, productoData) ->
                            val nombre = productoData["nombre"] as? String ?: ""
                            val cantidad = (productoData["cantidad"] as? Long)?.toInt() ?: 0
                            arrayProductes.add(Producte(id, nombre, cantidad))
                        }
                    }
                }else {
                    val initialData = mapOf("contador" to 0, "productos" to mapOf<String, Map<String, Any>>())
                    db.collection("productos").document(vmUser.user).set(initialData)
                        .addOnSuccessListener {
                            cargarDatos()
                        }
                        .addOnFailureListener { exception: Exception ->
                            println("Error creando el documento: ${exception.message}")
                        }
                    return@addOnSuccessListener
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception: Exception ->
                println("Error obteniendo el documento: ${exception.message}")
            }
    }
}