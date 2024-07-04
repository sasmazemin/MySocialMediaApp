package com.eminsasmaz.kotlininstagram.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.eminsasmaz.kotlininstagram.R
import com.eminsasmaz.kotlininstagram.adapter.FeedRecyclerAdapter
import com.eminsasmaz.kotlininstagram.databinding.ActivityFeedBinding
import com.eminsasmaz.kotlininstagram.model.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class FeedActivity : AppCompatActivity() {
    private lateinit var binding : ActivityFeedBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var db:FirebaseFirestore
    private lateinit var postArrayList:ArrayList<Post>
    private lateinit var feedAdapter:FeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFeedBinding.inflate(layoutInflater)
        val view =binding.root
        setContentView(view)

        auth= Firebase.auth
        db= Firebase.firestore

        postArrayList= ArrayList<Post>()

        getData()

        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        feedAdapter= FeedRecyclerAdapter(postArrayList)
        binding.recyclerView.adapter=feedAdapter

    }
    private fun getData(){
        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->

            if(error!=null){
                Toast.makeText(this@FeedActivity,error.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if(value!=null){
                    if(!value.isEmpty){
                        val documents=value.documents

                        postArrayList.clear()
                        for (document in documents){
                            //casting
                            val comment=document.get("comment") as String
                            val userMail=document.get("userMail") as String
                            val downloadUrl=document.get("downloadUrl") as String
                            //tabiki bunları kaydetmek için bir dizi oluşturmak lazım her birine ayrı ayrı dizilere
                            //kaydetmek yerine bi tane model oluşturuyoruz


                            val post=Post(userMail,comment,downloadUrl)
                            postArrayList.add(post)
                        }
                        feedAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.insta_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId== R.id.add_post){
            val intent= Intent(this@FeedActivity, UploadActivity::class.java)
            startActivity(intent)
            finish()
        }else if(item.itemId== R.id.signout){
            auth.signOut()
            val intent=Intent(this@FeedActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}