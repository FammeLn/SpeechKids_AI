package com.example.speechkids_ai.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.speechkids_ai.R
import com.google.android.material.chip.Chip

data class SysUser(val name: String, val email: String, val role: String)

class AdminUsersFragment : Fragment() {
    
    private val userList = mutableListOf(
        SysUser("Иван Иванов", "parent@test.com", "Родитель"),
        SysUser("Елена Смирнова", "therapist@test.com", "Логопед"),
        SysUser("Администратор", "admin@test.com", "Админ")
    )

    private lateinit var container: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_admin_users, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        container = view.findViewById(R.id.adminUsersContainer)
        val nameInput = view.findViewById<EditText>(R.id.adminAddUserName)
        val emailInput = view.findViewById<EditText>(R.id.adminAddUserEmail)
        val chipParent = view.findViewById<Chip>(R.id.chipRoleParent)
        val chipTherapist = view.findViewById<Chip>(R.id.chipRoleTherapist)
        val createButton = view.findViewById<Button>(R.id.adminCreateUserButton)

        createButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val role = when {
                chipParent.isChecked -> "Родитель"
                chipTherapist.isChecked -> "Логопед"
                else -> "Админ"
            }

            if (name.isNotEmpty() && email.isNotEmpty()) {
                userList.add(SysUser(name, email, role))
                nameInput.text.clear()
                emailInput.text.clear()
                Toast.makeText(context, "Пользователь создан!", Toast.LENGTH_SHORT).show()
                renderUsers()
            } else {
                Toast.makeText(context, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        renderUsers()
    }

    private fun renderUsers() {
        container.removeAllViews()
        for (user in userList) {
            val view = layoutInflater.inflate(R.layout.item_admin_user, container, false)
            view.findViewById<TextView>(R.id.userNameText).text = "${user.name} (${user.role})"
            view.findViewById<TextView>(R.id.userEmailText).text = user.email

            val iconText = when (user.role) {
                "Родитель" -> "👪"
                "Логопед" -> "👩‍⚕️"
                else -> "⚙️"
            }
            view.findViewById<TextView>(R.id.userIconText).text = iconText

            view.findViewById<Button>(R.id.deleteUserButton).setOnClickListener {
                userList.remove(user)
                Toast.makeText(context, "Пользователь ${user.name} удален", Toast.LENGTH_SHORT).show()
                renderUsers()
            }

            container.addView(view)
        }
    }
}
