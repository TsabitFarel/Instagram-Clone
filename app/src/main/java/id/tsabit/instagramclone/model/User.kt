package id.tsabit.instagramclone.model

// Isi var dari data class User harus persis sama dengan yang ada di realtime database Firebase
data class User(
    var bio:String="",
    var email:String="",
    var fullname:String="",
    var image:String="",
    var uid:String="",
    var username:String=""
)