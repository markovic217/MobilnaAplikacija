package elfak.mosis.myplaces.model

import androidx.lifecycle.ViewModel
import elfak.mosis.myplaces.data.Holes

class HolesViewModel: ViewModel() {
    var HolesList: ArrayList<Holes> = ArrayList<Holes>()
    fun addHole(hole:Holes){
        HolesList.add(hole)
    }
    var selected: Holes? = null
}