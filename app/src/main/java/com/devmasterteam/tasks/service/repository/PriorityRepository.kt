package com.devmasterteam.tasks.service.repository

import android.content.Context
import com.devmasterteam.tasks.R
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.repository.local.TaskDatabase
import com.devmasterteam.tasks.service.repository.remote.PriorityService
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PriorityRepository(context: Context) : BaseRepository(context) {

    private val remote = RetrofitClient.getService(PriorityService::class.java)
    private val dataBase = TaskDatabase.getDatabase(context).priorityDAO()

    // Cache - implementação de cache no app
    companion object {
        private val cache = mutableMapOf<Int, String>()

        fun getDescription(id: Int): String {
            return cache[id] ?: ""
        }

        fun setDescription(id: Int, description: String) {
            cache[id] = description
        }
    }

    fun getDescription(id: Int): String {
        val cached = PriorityRepository.getDescription(id)

        return if (cached == "") {
            val description = dataBase.getDescription(id)
            PriorityRepository.setDescription(id, description)
            description
        } else {
            cached
        }
    }

    fun list(listener: APIListener<List<PriorityModel>>) {
        val call = remote.list()
        executeCall(call, listener)
    }

    fun priorityList(): List<PriorityModel> {
        return dataBase.list()
    }

    fun save(list: List<PriorityModel>) {
        dataBase.clear()
        dataBase.save(list)
    }
}
