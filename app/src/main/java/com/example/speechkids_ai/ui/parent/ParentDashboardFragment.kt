package com.example.speechkids_ai.ui.parent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.speechkids_ai.R
import com.example.speechkids_ai.model.Child
import com.example.speechkids_ai.viewmodel.ParentViewModel

class ParentDashboardFragment : Fragment() {
    private val viewModel: ParentViewModel by viewModels()
    private lateinit var childAdapter: ChildAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_parent_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.childrenRecyclerView)
        setupRecyclerView()
        observeChildren()
    }

    private fun setupRecyclerView() {
        childAdapter = ChildAdapter(emptyList())
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = childAdapter
        }
    }

    private fun observeChildren() {
        viewModel.children.observe(viewLifecycleOwner) { children ->
            childAdapter.updateItems(children)
        }
    }

    class ChildAdapter(
        private var items: List<Child>
    ) : RecyclerView.Adapter<ChildAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val nameText: TextView = itemView.findViewById(android.R.id.text1)
            private val progressText: TextView = itemView.findViewById(android.R.id.text2)

            fun bind(item: Child) {
                nameText.text = item.name
                progressText.text = "Прогресс: ${item.progress}%"
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = android.widget.TextView(parent.context).apply {
                layoutParams = RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    100
                )
                setLines(2)
            }
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount() = items.size

        fun updateItems(newItems: List<Child>) {
            items = newItems
            notifyDataSetChanged()
        }
    }
}


