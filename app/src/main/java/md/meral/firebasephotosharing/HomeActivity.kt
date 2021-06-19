package md.meral.firebasephotosharing

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import md.meral.firebasephotosharing.adapter.HomeRecyclerAdapter
import md.meral.firebasephotosharing.databinding.ActivityHomeBinding
import md.meral.firebasephotosharing.model.Post

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    var postList = ArrayList<Post>()

    private lateinit var binding: ActivityHomeBinding
    private lateinit var recyclerViewAdapter: HomeRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        parseData()

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = HomeRecyclerAdapter(postList)
        binding.recyclerView.adapter = recyclerViewAdapter
    }

    private fun parseData() {

        database.collection("post").orderBy("history", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                if (snapshot != null) {

                    if (!snapshot.isEmpty) {

                        val documents = snapshot.documents

                        postList.clear()

                        for (document in documents) {
                            val userEmail = document.get("user_email") as String
                            val comment = document.get("comment") as String
                            val imageUrl = document.get("image_url") as String

                            val downloadedPost = Post(userEmail, comment, imageUrl)
                            postList.add(downloadedPost)
                        }

                        recyclerViewAdapter.notifyDataSetChanged()

                    }

                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share_photo) {
            // Will go to photo sharing activity

            val intent = Intent(this, PhotoSharingActivity::class.java)
            startActivity(intent)
            finish()

        } else if (item.itemId == R.id.sign_out) {

            auth.signOut()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}