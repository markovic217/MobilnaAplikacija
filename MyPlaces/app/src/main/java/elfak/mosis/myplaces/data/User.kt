package elfak.mosis.myplaces.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(val email: String? = null, val password: String? = null,
                val name: String? = null, val surname: String? = null,
                val phoneNumber: String? = null, val pictureSrc: String? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}