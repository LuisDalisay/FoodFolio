package com.example.foodfolio

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast

class DatabaseHelper(private val context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FoodFolio.db"
        private const val DATABASE_VERSION = 1

        //Users Table
        private const val TABLE_USERS = "Users"
        private const val COLUMN_USERID = "userID"
        private const val COLUMN_NAME = "FullName"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"


        //Recipe Table
        private const val TABLE_RECIPE = "Recipes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_FK_USERID = "userID"
        private const val COLUMN_TITLE = "recTitle"
        private const val COLUMN_CATEGORY = "recCategory"
        private const val COLUMN_INGREDIENTS = "recIngredients"
        private const val COLUMN_PROCEDURE = "recProcedure"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        //Users Table
        val createUserTableQuery = ("CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_USERID INTEGER PRIMARY KEY autoincrement, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_USERNAME TEXT, " +
                "$COLUMN_PASSWORD TEXT)")
        db?.execSQL(createUserTableQuery)

        //Recipe Table
        val createRecipeTableQuery = "CREATE TABLE $TABLE_RECIPE($COLUMN_ID INTEGER PRIMARY KEY autoincrement, " +
                "$COLUMN_FK_USERID INTEGER, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_CATEGORY TEXT, " +
                "$COLUMN_INGREDIENTS TEXT, " +
                "$COLUMN_PROCEDURE TEXT)"
        db?.execSQL(createRecipeTableQuery)
        insertCategories(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //Users Table
        val dropUserTableQuery = "DROP TABLE IF EXISTS $TABLE_USERS"

        //Recipe Table
        val dropRecipeTableQuery = "DROP TABLE IF EXISTS $TABLE_RECIPE"

        db?.execSQL(dropUserTableQuery)
        db?.execSQL(dropRecipeTableQuery)
        onCreate(db)
    }

    fun insertUser(user: User): Long {
        if (isUsernameExists(user.username)) {
            return -1L
        }

        val values = ContentValues().apply {
            put(COLUMN_NAME, user.name)
            put(COLUMN_USERNAME, user.username)
            put(COLUMN_PASSWORD, user.password)
        }
        val db = writableDatabase
        return db.insert(TABLE_USERS, null, values)
    }

    /*validation sa username, para walang magkakapareha*/
    private fun isUsernameExists(username: String): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ?"
        val selectionArgs = arrayOf(username)
        val cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null)

        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    /*literal na nakapasok sa db, pero nakanull ibang values*/
    private fun insertCategories(db: SQLiteDatabase?) {
        /*dito napapalitan categories, delete muna db before changing*/
        val categories = listOf("Breakfast", "Lunch", "Dinner", "Snack")

        for (category in categories) {
            val contentValues = ContentValues()
            contentValues.put(COLUMN_CATEGORY, category)
            db?.insert(TABLE_RECIPE, null, contentValues)
        }
    }

    /*para hindi magadd ulit sa spinner ung existing categories*/
    fun getUniqueCategoriesFromDatabase(): List<String> {
        val categories = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT $COLUMN_CATEGORY FROM $TABLE_RECIPE", null)

        try {
            val columnNameIndex = cursor.getColumnIndex(COLUMN_CATEGORY)

            while (cursor.moveToNext()) {
                val category = cursor.getString(columnNameIndex)
                categories.add(category)
            }
        } finally {
            cursor.close()
            db.close()
        }
        return categories
    }

    /*add recipe*/
    fun insertRecipe(recipe: Recipe) {
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_FK_USERID, recipe.userID)
            put(COLUMN_TITLE, recipe.title)
            put(COLUMN_CATEGORY, recipe.category)
            put(COLUMN_INGREDIENTS, recipe.ingredients)
            put(COLUMN_PROCEDURE, recipe.procedure)
        }
        db.insert(TABLE_RECIPE, null, values)
        db.close()
    }

    /*para sa login to*/
    @SuppressLint("Range")
    fun readUserByUsername(username: String): User? {
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ?"
        val selectionArgs = arrayOf(username)
        val cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null)

        var user: User? = null

        if (cursor.moveToFirst()) {
            user = User(
                cursor.getInt(cursor.getColumnIndex(COLUMN_USERID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD))
            )
        }
        cursor.close()
        return user
    }

    /*gets all recipes added by logged in user*/
    fun getAllRecipes(loggedInUserId: Int): List<Recipe> {
        val recipes = mutableListOf<Recipe>()
        val db = readableDatabase
        val selection = "$COLUMN_FK_USERID = ?"
        val selectionArgs = arrayOf(loggedInUserId.toString())

        val cursor = db.query(TABLE_RECIPE, null, selection, selectionArgs, null, null, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val userID =  cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FK_USERID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
            val ingredients = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENTS))
            val procedure = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROCEDURE))

            val recipe = Recipe(id, userID, title, category, ingredients, procedure)
            recipes.add(recipe)
        }
            cursor.close()
            db.close()
            return recipes
    }

    fun updateRecipe(recipe: Recipe) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, recipe.title)
            put(COLUMN_CATEGORY, recipe.category)
            put(COLUMN_INGREDIENTS, recipe.ingredients)
            put(COLUMN_PROCEDURE, recipe.procedure)
        }

        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(recipe.id.toString())

        try {
            val rowsAffected = db.update(TABLE_RECIPE, values, whereClause, whereArgs)
            Log.d(TAG, "Rows affected: $rowsAffected")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating recipe", e)
        } finally {
            db.close()
        }
    }

    /*for update recipe, para makuha ung recipeId*/
    fun getRecipeById(recipeId: Int): Recipe? {
        val db = readableDatabase
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(recipeId.toString())

        val cursor = db.query(TABLE_RECIPE, null, selection, selectionArgs, null, null, null)

        var recipe: Recipe? = null

        if (cursor.moveToFirst()) {
            recipe = Recipe(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FK_USERID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENTS)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROCEDURE))
            )
        }
        cursor.close()
        db.close()
        return recipe
    }

    fun deleteRecipe(recipeId: Int): Int {
        return writableDatabase.use { db ->
            db.delete(
                TABLE_RECIPE,
                "$COLUMN_ID = ?",
                arrayOf(recipeId.toString())
            )
        }
    }
}