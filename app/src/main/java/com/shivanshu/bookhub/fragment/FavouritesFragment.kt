@file:Suppress("DEPRECATION")

package com.shivanshu.bookhub.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.shivanshu.bookhub.R
import com.shivanshu.bookhub.adapter.FavouriteRecyclerAdapter
import com.shivanshu.bookhub.database.BookDatabase
import com.shivanshu.bookhub.database.BookEntity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Favourites.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("KDocUnresolvedReference", "KDocUnresolvedReference")
class FavouritesFragment : Fragment() {


    lateinit var recyclerFavourite: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    var dbBookList = listOf<BookEntity>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
// Inflate the layout for this fragment


        val view = inflater.inflate(R.layout.fragment_favourites, container, false)


        recyclerFavourite = view.findViewById(R.id.recyclerFav)
        progressLayout = view.findViewById(R.id.progressFavLayout)
        progressBar = view.findViewById(R.id.progressFavBar)


        layoutManager = GridLayoutManager(activity as Context, 2)


        dbBookList = RetrieveFavourites(activity as Context).execute().get()


        Log.d("list", dbBookList.toString())
        if (activity != null) {
            progressLayout.visibility = View.GONE
            recyclerAdapter = FavouriteRecyclerAdapter(activity as Context, dbBookList)
            recyclerFavourite.layoutManager = layoutManager
            recyclerFavourite.adapter = recyclerAdapter


        }


        return view
    }



    class RetrieveFavourites(val context: Context) : AsyncTask<Void, Void, List<BookEntity>>() {
        override fun doInBackground(vararg p0: Void?): List<BookEntity> {
            val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()


            return db.BookDao().getAllBooks()
        }


    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AboutAppFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AboutAppFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
