package com.example.kelompok2.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kelompok2.DataModels.UserModel
import com.example.kelompok2.R

class UserAdapter(
    private val userList: List<UserModel>,
    private val onChatClick: (UserModel) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount() = userList.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.tv_user_name)
        private val userType: TextView = itemView.findViewById(R.id.tv_user_type)
        private val profileImage: ImageView = itemView.findViewById(R.id.user_profile_image)
        private val chatButton: Button = itemView.findViewById(R.id.btn_chat)

        fun bind(user: UserModel) {
            userName.text = user.fullName
            userType.text = user.userType
            Glide.with(itemView.context)
                .load(user.profileImage)
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(profileImage)

            chatButton.setOnClickListener {
                onChatClick(user)
            }
        }
    }
}