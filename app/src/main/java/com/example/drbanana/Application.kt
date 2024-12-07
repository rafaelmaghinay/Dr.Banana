package com.example.drbanana

import android.app.Application
import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration
import io.realm.RealmSchema

import java.util.Date

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .schemaVersion(3)
            .migration(MyMigration())
            .allowWritesOnUiThread(true) // Enable writes on the UI thread
            .build()
        Realm.setDefaultConfiguration(config)
    }
}

class MyMigration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema: RealmSchema = realm.schema
        var version = oldVersion

        if (version == 1L) {
            // Example migration: Add the Disease class
            schema.create("Disease")
                .addField("id", Long::class.java, FieldAttribute.PRIMARY_KEY)
                .addField("treeDisease", String::class.java)
                .addField("imageUri", String::class.java)
                .addField("dateTaken", Date::class.java)
            version++
        }

        // Add more migration steps as needed
    }
}