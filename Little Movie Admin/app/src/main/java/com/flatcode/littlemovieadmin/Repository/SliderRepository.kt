package com.flatcode.littlemovieadmin.repository

import com.flatcode.littlemovieadmin.utils.DATA
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SliderRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    suspend fun getSliderImages(): Map<String, String> {
        val snapshot = database.getReference(DATA.SLIDER_SHOW).get().await()
        val images = mutableMapOf<String, String>()
        for (i in 1..20) {
            val url = snapshot.child(i.toString()).value?.toString() ?: ""
            images[i.toString()] = url
        }
        return images
    }

    suspend fun updateSliderImage(name: String, imageUrl: String) =
        database.getReference(DATA.SLIDER_SHOW).updateChildren(mapOf(name to imageUrl)).await()
}
