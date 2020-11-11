package ru.raider.date.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.findFragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.squareup.picasso.Picasso
import convertImageUrl
import kotlinx.android.synthetic.main.matches_recycler_view.view.*
import kotlinx.android.synthetic.main.profile_photo_item.view.*
import ru.raider.date.R
import ru.raider.date.fragments.ProfileViewPhotosFragment


class ViewPagerAdapter(val context: Context, private val images: List<String>?): PagerAdapter() {
    private var inflater: LayoutInflater? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {

        return view === `object`
    }

    override fun getCount(): Int {
        return images?.size ?: 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater!!.inflate(R.layout.profile_photo_item, null)
        val newUrl = convertImageUrl(images?.get(position))
        Picasso.get().load(newUrl).into(view.idProfilePhotoImageView)
        view.idProfilePhotoImageView.setOnClickListener {
            it.findFragment<ProfileViewPhotosFragment>().activity?.onBackPressed()
        }
        val vp = container as ViewPager
        vp.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }
}