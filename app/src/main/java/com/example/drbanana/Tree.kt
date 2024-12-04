package com.example.drbanana
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.Date

open class TreeDiseaseRecord : RealmObject() {
    @PrimaryKey
    var id: Long = 0
    var treeDisease: String = ""
    var dateTaken: Date = Date()
}