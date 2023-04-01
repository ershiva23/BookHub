package com.shivanshu.bookhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.shivanshu.bookhub.R
import com.shivanshu.bookhub.adapter.DashboardRecylerAdapter
import com.shivanshu.bookhub.model.Book
import com.shivanshu.bookhub.util.ConnectionManager
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.recycler_dashboard_single_row.*
import org.json.JSONException
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("UNREACHABLE_CODE")
class DashboardFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter:DashboardRecylerAdapter
    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar:ProgressBar

    var ratingComparator = Comparator<Book> { book1, book2 ->

        if (book1.bookRating.compareTo(book2.bookRating, true) == 0) {
            book1.bookName.compareTo(book2.bookName, true)
        } else {
            book1.bookRating.compareTo(book2.bookRating, true)
        }
    }
    val bookInfoList = arrayListOf<Book>()
    /*val bookInfoList= arrayListOf<Book>(
        Book("P.S. I love You", "Cecelia Ahern", "Rs. 299", "4.5", R.drawable.ps_ily),
        Book("The Great Gatsby", "F. Scott Fitzgerald", "Rs. 399", "4.1", R.drawable.great_gatsby),
        Book("Anna Karenina", "Leo Tolstoy", "Rs. 199", "4.3", R.drawable.anna_kare),
        Book("Madame Bovary", "Gustave Flaubert", "Rs. 500", "4.0", R.drawable.madame),
        Book("War and Peace", "Leo Tolstoy", "Rs. 249", "4.8", R.drawable.war_and_peace),
        Book("Lolita", "Vladimir Nabokov", "Rs. 349", "3.9", R.drawable.lolita),
        Book("Middlemarch", "George Eliot", "Rs. 599", "4.2", R.drawable.middlemarch),
        Book("The Adventures of Huckleberry Finn", "Mark Twain", "Rs. 699", "4.5", R.drawable.adventures_finn),
        Book("Moby-Dick", "Herman Melville", "Rs. 499", "4.5", R.drawable.moby_dick),
        Book("The Lord of the Rings", "J.R.R Tolkien", "Rs. 749", "5.0", R.drawable.lord_of_rings)
    )*/




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        setHasOptionsMenu(true)
        recyclerDashboard=view.findViewById(R.id.recyclerDashboard)
        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        progressLayout.visibility=View.VISIBLE

        layoutManager=LinearLayoutManager(activity)


        val queue=Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v1/book/fetch_books/"
        if(ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    try {
                        progressLayout.visibility=View.GONE
                        val success = it.getBoolean("success")
                        if (success) {
                            val data = it.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val bookJsonObject = data.getJSONObject(i)
                                val bookObject = Book(
                                    bookJsonObject.getString("book_id"),
                                    bookJsonObject.getString("name"),
                                    bookJsonObject.getString("author"),
                                    bookJsonObject.getString("rating"),
                                    bookJsonObject.getString("price"),
                                    bookJsonObject.getString("image")
                                )

                                bookInfoList.add(bookObject)
                                recyclerAdapter =
                                    DashboardRecylerAdapter(activity as Context, bookInfoList)
                                recyclerDashboard.adapter = recyclerAdapter
                                recyclerDashboard.layoutManager = layoutManager
                                }

                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error occurred",
                                Toast.LENGTH_SHORT
                            ).show()


                        }
                    }catch(e:JSONException){
                        Toast.makeText(activity as Context,"Some unexpected error occured",
                            Toast.LENGTH_SHORT).show()
                    } }, Response.ErrorListener {
                    if(activity!=null){
                        Toast.makeText(activity as Context,"Volley error occurred",
                            Toast.LENGTH_SHORT).show()}
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "9f2b1dd2d9a39f"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)

        }else{
            val dialog=AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not found")
            dialog.setPositiveButton("Open Settings"){ _, _ ->
                val settingIntent=Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){ _, _ ->
                ActivityCompat.finishAffinity(activity as Activity  )

            }
            dialog.create()
            dialog.show()

        }

        return view


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_dashboard,menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_sort) {
            //will return in decreasing order of rating
            Collections.sort(bookInfoList, ratingComparator)
            //we want increasing,hence reverse
            bookInfoList.reverse()
        }
        //notfy adapter of changes made
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}