package elfak.mosis.myplaces.model

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class UserViewModel: ViewModel() {
    var loggedUser: FirebaseUser? = null

    fun login(user: FirebaseUser){
        loggedUser = user
    }
    fun logout(){
        loggedUser = null
    }
}