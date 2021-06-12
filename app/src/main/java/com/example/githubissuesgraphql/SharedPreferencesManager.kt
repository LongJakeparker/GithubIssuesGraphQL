package com.example.githubissuesgraphql

import android.content.Context
import com.example.githubissuesgraphql.model.UserModel
import com.example.githubissuesgraphql.util.AESCrypt
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author longtran
 * @since 09/06/2021
 */
@Singleton
class SharedPreferencesManager @Inject constructor(@ApplicationContext context : Context){
    companion object {
        private const val PREF_NAME = BuildConfig.APPLICATION_ID + "app.local.Preferences"
        private const val TOKEN_CRYPT_KEY = "TOKEN_CRYPT_KEY_" // length must be 16, 24 or 32
        private const val PREF_KEY_ENCRYPTED_ACCESS_TOKEN = "pref_key_encrypted_access_token"
        private const val PREF_KEY_USER = "pref_key_user"
    }

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * Puts user's data and access token
     * @param userModel
     * @param token
     */
    fun putSignInData(userModel: UserModel, token: String) {
        putUser(userModel)
        putAccessToken(token)
    }

    /**
     * Removes user's data and access token
     */
    fun removeSignInData() {
        removeUser()
        removeAccessToken()
    }

    /**
     * Puts user's data
     * @param userModel
     */
    fun putUser(userModel: UserModel) {
        val json = Gson().toJson(userModel)
        val editor = prefs.edit()
        editor.putString(PREF_KEY_USER, json)
        editor.apply()
    }

    /**
     * Removes user's data
     */
    fun removeUser() {
        val editor = prefs.edit()
        editor.remove(PREF_KEY_USER)
        editor.apply()
    }

    /**
     * Gets user's data
     * @return UserModel
     */
    fun getUser(): UserModel? {
        return Gson().fromJson(prefs.getString(PREF_KEY_USER, ""), UserModel::class.java)
    }

    /**
     * Encrypts and puts user's access token
     * @param token
     */
    fun putAccessToken(token: String) {
        val editor = prefs.edit()
        editor.putString(PREF_KEY_ENCRYPTED_ACCESS_TOKEN, AESCrypt.encrypt(token, TOKEN_CRYPT_KEY))
        editor.apply()
    }

    /**
     * Removes user's access token
     */
    fun removeAccessToken() {
        val editor = prefs.edit()
        editor.remove(PREF_KEY_ENCRYPTED_ACCESS_TOKEN)
        editor.apply()
    }

    /**
     * Gets and decrypts user's access token
     * @return String
     */
    fun getAccessToken(): String {
        val encryptedToken = prefs.getString(PREF_KEY_ENCRYPTED_ACCESS_TOKEN, "")
        return AESCrypt.decrypt(encryptedToken ?: "", TOKEN_CRYPT_KEY)
    }
}