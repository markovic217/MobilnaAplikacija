package elfak.mosis.myplaces.data

import android.net.Uri
import java.util.UUID


data class Holes(var title:String, var imageURI: String, var longitude: String, var latitude: String, var postedBy: String?)
{
    var id: String = UUID.randomUUID().toString()
    override fun toString():String = "$title"
}
