package com.anexus.simplemusic

import android.Manifest
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.song_list_item.*
import java.io.File
import java.nio.file.Files
import android.content.DialogInterface
import android.R.string.ok
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.widget.Toast


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{ //

    lateinit var adapter: SongsAdapter

    fun requestPermission(context: Context){
        // 1. Instantiate an AlertDialog.Builder with its constructor
        val builder = AlertDialog.Builder(context)
//         2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title)
        builder.setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, id ->
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    0)
            requestPermission(context)
          })
        if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            val dialog = builder.create()
            dialog.show()
        }
    }

    fun retriveMusic(): ArrayList<Song>{
        val a1: ArrayList<Song> = ArrayList()
        val contentResolver: ContentResolver = getContentResolver()
        val uri= android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            // query failed, handle error.
        } else if (!cursor.moveToFirst()) {
            // no media on the device

        } else {
            val titleColumn: Int = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
            val idColumn: Int = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
            do {
                val thisId = cursor.getLong(idColumn);
                val thisTitle = cursor.getString(titleColumn);
                a1.add(Song(thisTitle, null, "", 0, thisId))
                // ...process entry...
            } while (cursor.moveToNext())
        }
        return a1
    }

    fun inverti(a: ArrayList<Song>, i: Int, j: Int): Int{
        val tmp = a[i + 1]
        a[i + 1] = a[j]
        a[j] = tmp
        return i + 1
    }

    fun distribution(a: ArrayList<Song>, sx: Int, dx: Int, orderBy: String): Int {
        val px = dx
        var i: Int = sx - 1
        var j: Int = sx
        for (j in sx..dx){
            when(orderBy){
                "title" -> if(a[j].title < a[px].title) i = inverti(a, i, j)
                "artist" ->if(a[j].artist < a[px].artist) i = inverti(a, i, j)
                "length" ->if(a[j].length < a[px].length) i = inverti(a, i, j)
            }
        }
        val tmp = a[i + 1]
        a[i + 1] = a[px]
        a[px] = tmp
        return i + 1
    }

    fun myQuickSort(a: ArrayList<Song>, sx: Int, dx: Int, orderBy: String): Unit{
        if(sx < dx){
            val px = distribution(a, sx, dx, orderBy)
            myQuickSort(a, sx, px - 1, orderBy)
            myQuickSort(a, px + 1, dx, orderBy)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        requestPermission(this)
//        val mySongs: ArrayList<Song> = findSongs(Environment.getExternalStorageDirectory(), this)
        val songs = retriveMusic()

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.generic_cover )  //creating bitmap to make image round
        val rounded = RoundedBitmapDrawableFactory.create(resources, bitmap)
        rounded.isCircular = true
        songImageFront.setImageDrawable(rounded)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        myQuickSort(songs, 0, songs.size - 1, "title")
        adapter = SongsAdapter(this, songs) {
            song ->
            songTitleFront.text = song.title
            songArtistFront.text = song.artist
        }    //setting the adapter of recylcerView and the song click
        songsListView.adapter = adapter                             //assigning the adapter
        val layoutManager =  LinearLayoutManager(this)       //layout manager
        songsListView.layoutManager = layoutManager
        songsListView.setHasFixedSize(true)


//        adapter = SongsAdapter(this, SongsData.songsList) {
//            song ->
//            songTitleFront.text = song.title
//            songArtistFront.text = song.artist
//        }    //setting the adapter of recylcerView and the song click
//        songsListView.adapter = adapter                             //assigning the adapter
//        val layoutManager =  LinearLayoutManager(this)       //layout manager
//        songsListView.layoutManager = layoutManager
//        songsListView.setHasFixedSize(true)

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
