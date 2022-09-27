package com.sugadev.noteit.base.config.model

import com.google.gson.annotations.SerializedName

data class AddNotesPlaceholder(
    @SerializedName("title") val title: LocalisedString = LocalisedString(),
    @SerializedName("desc") val desc: LocalisedString = LocalisedString()
) {
    companion object {
        val DEFAULT = AddNotesPlaceholder(
            title = LocalisedString(
                english = "Add meaning title here",
                id = "Tambahkan judul"
            ),
            desc = LocalisedString(
                english = "Add marvelous detail for your notes",
                id = "Tulis aja dulu"
            )
        )
    }
}