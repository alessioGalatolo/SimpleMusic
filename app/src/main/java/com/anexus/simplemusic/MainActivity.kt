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
import android.widget.Toast


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{ //

    lateinit var adapter: SongsAdapter

    fun findSongs(root: File, context: Context): ArrayList<Song> {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        val builder = AlertDialog.Builder(context)
        var a1: ArrayList<Song> = ArrayList()
        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title)
        builder.setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, id ->
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    0)
            a1 = findSongs(root, context)
        })
        if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            val dialog = builder.create()
            dialog.show()
            return a1
        }
        else {
            val files: Array<File> = root.listFiles()
            for (singleFile in files) {
                if (singleFile.isDirectory)
                    a1.addAll(findSongs(singleFile, context))
                else if (singleFile.endsWith(".mp3")) {
                    a1.add(Song(singleFile.toString(), null, "", 0, singleFile.toURI()))
                }
            }
            return a1
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

//        val mySongs: ArrayList<Song> = findSongs(Environment.getExternalStorageDirectory(), this)

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.legends_preview )  //creating bitmap to make image round
        val rounded = RoundedBitmapDrawableFactory.create(resources, bitmap)
        rounded.isCircular = true
        songImageFront.setImageDrawable(rounded)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)


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
