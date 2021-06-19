package md.meral.firebasephotosharing

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import md.meral.firebasephotosharing.databinding.ActivityPhotoSharingBinding
import java.util.*

class PhotoSharingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoSharingBinding

    private var selectedBitmap: Bitmap? = null
    private var selectedImage: Uri? = null

    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPhotoSharingBinding.inflate(layoutInflater)

        setContentView(binding.root)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
    }

    fun send(view: View) {
        // storage works
        val uuid = UUID.randomUUID()
        val imageName = "${uuid}.jpg"

        val reference = storage.reference

        val imageReference = reference.child("images").child(imageName)
        if (selectedImage != null) {

            imageReference.putFile(selectedImage!!).addOnSuccessListener { taskSnapshot ->
                val uploadedImageReference = FirebaseStorage.getInstance().reference.child("images").child(imageName)
                uploadedImageReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val currentUserEmail = auth.currentUser!!.email.toString()
                    val userComment = binding.comment.text.toString()
                    val history = Timestamp.now()

                    // Database operations
                    val postHashMap = hashMapOf<String, Any>()
                    postHashMap.put("image_url", downloadUrl)
                    postHashMap.put("user_email", currentUserEmail)
                    postHashMap.put("comment", userComment)
                    postHashMap.put("history", history)

                    database.collection("post").add(postHashMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    fun chooseImage(view: View) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // we did not get permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                0
            )
        } else {
            openGallery()
        }
    }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 0) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // What to do when allowed
                openGallery()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImage = data.data

            if (selectedImage != null) {
                if (Build.VERSION.SDK_INT >= 28) {

                    val source = ImageDecoder.createSource(this.contentResolver, selectedImage!!)
                    selectedBitmap = ImageDecoder.decodeBitmap(source)
                    binding.photo.setImageBitmap(selectedBitmap)

                } else {
                    selectedBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                    binding.photo.setImageBitmap(selectedBitmap)
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}