package com.example.drbanana

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId
import java.util.Date

open class Disease(
    @PrimaryKey
    var id: ObjectId = ObjectId(),
    var treeDisease: String = "",
    var imageUri: String = "",
    var dateTaken: Date = Date()
) : RealmObject()