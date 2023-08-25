package elfak.mosis.myplaces.model

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import elfak.mosis.myplaces.data.User

class UserViewModel: ViewModel() {
    var loggedUser: User? = null

    fun login(user: User){
        loggedUser = user
    }
    fun logout(){
        loggedUser = null
    }
}