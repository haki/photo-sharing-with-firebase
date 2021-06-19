package md.meral.firebasephotosharing.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import md.meral.firebasephotosharing.R
import md.meral.firebasephotosharing.model.Post
import java.util.*
import kotlin.collections.ArrayList


class HomeRecyclerAdapter(private val postList: ArrayList<Post>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.PostHolder>() {
    class PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row, parent, false)

        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        val r = Random()
        val red: Int = r.nextInt(255 - 0 + 1) + 0
        val green: Int = r.nextInt(255 - 0 + 1) + 0
        val blue: Int = r.nextInt(255 - 0 + 1) + 0

        val draw = GradientDrawable()
        draw.setColor(Color.rgb(red, green, blue))
        holder.itemView.findViewById<LinearLayout>(R.id.recycler_row_linear).background = draw

        val imageView: ImageView = holder.itemView.findViewById(R.id.recycler_image_view)

        holder.itemView.findViewById<TextView>(R.id.recycler_user_email).text =
            postList[position].userEmail
        holder.itemView.findViewById<TextView>(R.id.recycler_comment).text =
            postList[position].comment
        Picasso.get().load(postList[position].imageUrl).into(imageView)
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}