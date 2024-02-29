package com.example.lab_2_to_7

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MovieDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MOVIES"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "movies_table"
        const val ID_COL = "id"
        const val NAME_COL = "name"
        const val GENRE_COL = "genre"
        const val DIRECTOR_COL = "director"
        const val COMPANY_COL = "company"
        const val PICTURE_COL = "picture"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val query = (
                "CREATE TABLE $TABLE_NAME ("
                        + "$ID_COL INTEGER PRIMARY KEY autoincrement, "
                        + "$NAME_COL TEXT,"
                        + "$GENRE_COL TEXT,"
                        + "$DIRECTOR_COL TEXT,"
                        + "$COMPANY_COL TEXT,"
                        + "$PICTURE_COL TEXT)"
                )

        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getCursor(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun isEmpty(): Boolean {
        val cursor = getCursor()
        return !cursor.moveToFirst()
    }

    fun printDb() {
        val cursor = getCursor()

        if (!isEmpty()) {
            cursor.moveToFirst()

            val nameColIndex = cursor.getColumnIndex(NAME_COL)
            val genreColIndex = cursor.getColumnIndex(GENRE_COL)
            val directorColIndex = cursor.getColumnIndex(DIRECTOR_COL)
            val companyColIndex = cursor.getColumnIndex(COMPANY_COL)
            val pictureColIndex = cursor.getColumnIndex(PICTURE_COL)

            do {
                print("${cursor.getString(nameColIndex)} ")
                print("${cursor.getString(genreColIndex)} ")
                print("${cursor.getString(directorColIndex)} ")
                print("${cursor.getString(companyColIndex)} ")
                println("${cursor.getString(pictureColIndex)} ")
            } while (cursor.moveToNext())
        } else {
            println("Database is empty")
        }
    }

    fun addArrayToDB(movies: ArrayList<Movie>) {
        movies.forEach { addMovie(it) }
    }


    fun addMovie(movie: Movie) {
        val values = ContentValues()

        values.put(NAME_COL, movie.name)
        values.put(GENRE_COL, movie.genre)
        values.put(DIRECTOR_COL, movie.director)
        values.put(COMPANY_COL, movie.company)
        values.put(PICTURE_COL, movie.picture)

        val db = this.writableDatabase

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun changeImage(name: String, image: String) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(PICTURE_COL, image)

        db.update(TABLE_NAME, values, "$NAME_COL = '$name'", null)
        db.close()
    }

    fun deleteMovie(name: String) {
        val db = this.writableDatabase

        db.delete(TABLE_NAME, "name=?", Array(1) { name })
        db.close()
    }


    fun clear() {
        val db = this.writableDatabase

        db.delete(TABLE_NAME, null, null)
        db.close()
    }

    fun getMoviesArray(): ArrayList<Movie> {
        val moviesArray = ArrayList<Movie>()
        val cursor = getCursor()

        if (!isEmpty()) {
            cursor.moveToFirst()

            val nameColIndex = cursor.getColumnIndex(NAME_COL)
            val directorColIndex = cursor.getColumnIndex(DIRECTOR_COL)
            val genreColIndex = cursor.getColumnIndex(GENRE_COL)
            val companyColIndex = cursor.getColumnIndex(COMPANY_COL)
            val pictureColIndex = cursor.getColumnIndex(PICTURE_COL)

            do {
                val name = cursor.getString(nameColIndex)
                val director = cursor.getString(directorColIndex)
                val company = cursor.getString(companyColIndex)
                val genre = cursor.getString(genreColIndex)
                val picture = cursor.getString(pictureColIndex)

                moviesArray.add(Movie(name, director, company, genre, picture))
            } while (cursor.moveToNext())
        } else {
            println("Database is empty")
        }

        return moviesArray
    }
}