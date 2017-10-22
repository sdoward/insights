package org.sharethemeal.hackathon.android

import android.Manifest
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.SheetsScopes

import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    companion object {
        val PERMISSIONS = 200
        val ACCOUNT = 300
    }

    val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("default", 0)
    }

    val credential: GoogleAccountCredential by lazy {
        GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), arrayListOf(SheetsScopes.SPREADSHEETS_READONLY))
                .setBackOff(ExponentialBackOff())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        setSupportActionBar(toolbar)
        if (havePermission().and(haveAccount())) {
            GraphActivity.start(this)
            finish()
        } else {
            setUpUi()
        }
    }

    private fun setUpUi() {
        setAccountImageView()
        setPermissionsImageView()

        permissionButton.setOnClickListener {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.GET_ACCOUNTS), PERMISSIONS);
        }

        accountButton.setOnClickListener {
            startActivityForResult(credential.newChooseAccountIntent(), ACCOUNT);
        }

        fab.setOnClickListener { view ->
            if (havePermission().not()) {
                Snackbar.make(view, "Please give permission", Snackbar.LENGTH_LONG).show()
            } else if (haveAccount().not()) {
                Snackbar.make(view, "Set up an account to use", Snackbar.LENGTH_LONG).show()
            } else {
                GraphActivity.start(this)
                finish()
            }
        }
    }

    private fun setPermissionsImageView() {
        val imageResource = if (havePermission()) R.drawable.tick_24dp else R.drawable.cross_24dp
        permissionImageView.setImageResource(imageResource)
    }

    private fun havePermission(): Boolean {
        val permissionKey = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
        return permissionKey == PackageManager.PERMISSION_GRANTED
    }

    private fun setAccountImageView() {
        val imageResource = if (haveAccount()) R.drawable.tick_24dp else R.drawable.cross_24dp
        accountImageView.setImageResource(imageResource)
    }

    private fun haveAccount(): Boolean {
        val userName = sharedPreferences.getString("userName", "")
        return !userName.isNullOrBlank()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACCOUNT) {
            val accountName = data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            sharedPreferences.edit().putString("userName", accountName).commit()
            credential.selectedAccountName = accountName;
            setAccountImageView()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        setPermissionsImageView()
    }

}
