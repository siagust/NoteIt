package com.sugadev.noteit.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoteDb(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "text") val body: String,
    @ColumnInfo(name = "date") val date: Long
)

val DUMMY_NOTES: MutableList<Pair<String, String>> = mutableListOf(
    Pair(
        "The Outline method",
        "The Outline method is one of the best and most popular note-taking methods for college students. It lets you organize your notes in a structured form, helping you save a lot of time for further reviewing and editing."
    ),
    Pair("Fix Voucher", "Voucher need to be applied if no payment method selected."),
    Pair(
        "CC/DC Poject",
        "Support cc/dc on gosend id region. Add new gopay payment intent *-v2. Add litmus config to control cc/dc rollout."
    ),
    Pair(
        "The Outline method",
        "The Outline method is one of the best and most popular note-taking methods for college students. It lets you organize your notes in a structured form, helping you save a lot of time for further reviewing and editing."
    ),
    Pair("Fix Voucher", "Voucher need to be applied if no payment method selected."),
    Pair(
        "CC/DC Poject",
        "Support cc/dc on gosend id region. Add new gopay payment intent *-v2. Add litmus config to control cc/dc rollout."
    ),
    Pair(
        "The Outline method",
        "The Outline method is one of the best and most popular note-taking methods for college students. It lets you organize your notes in a structured form, helping you save a lot of time for further reviewing and editing."
    ),
    Pair("Fix Voucher", "Voucher need to be applied if no payment method selected."),
    Pair(
        "CC/DC Poject",
        "Support cc/dc on gosend id region. Add new gopay payment intent *-v2. Add litmus config to control cc/dc rollout."
    ),
    Pair("Fix Voucher", "Voucher need to be applied if no payment method selected."),
    Pair(
        "CC/DC Poject",
        "Support cc/dc on gosend id region. Add new gopay payment intent *-v2. Add litmus config to control cc/dc rollout."
    ),
    Pair(
        "The Outline method",
        "The Outline method is one of the best and most popular note-taking methods for college students. It lets you organize your notes in a structured form, helping you save a lot of time for further reviewing and editing."
    ),
    Pair("Fix Voucher", "Voucher need to be applied if no payment method selected."),
    Pair(
        "CC/DC Poject",
        "Support cc/dc on gosend id region. Add new gopay payment intent *-v2. Add litmus config to control cc/dc rollout."
    )
)