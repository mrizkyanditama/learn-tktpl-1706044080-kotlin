package id.ac.ui.cs.mobileprogramming

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import id.ac.ui.cs.mobileprogramming.databinding.ItemWifiBinding


class WifiAdapter(wifiList: List<WifiModel>, context: Context): BaseAdapter() {
    private var wifiList = wifiList
    private var context = context

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View? {
        println("MASUK PLIS")
        val binding: ItemWifiBinding
        if (view == null) {
            binding = ItemWifiBinding.inflate(LayoutInflater.from(context), viewGroup, false)
            binding.root.tag = binding
        } else {
            binding = view.tag as ItemWifiBinding
        }
        binding?.wifi = getItem(i) as WifiModel

        return binding.root
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getItem(p0: Int): Any {
        return wifiList.get(p0);
    }

    override fun getCount(): Int {
        println("MAWSUKW")
        return wifiList.size
    }
}