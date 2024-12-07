package com.example.checksdk

import OnTracking
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.teragroup.io.onappdata.OneLoyalty
import com.teragroup.io.onappdata.configs.Config
import com.teragroup.io.onappdata.model.LoyaltyDevice
import com.teragroup.io.onappdata.model.constant.DeviceType
import com.teragroup.io.onappdata.model.tracking.App
import com.teragroup.io.onappdata.model.tracking.AppContext
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setup()

        OneLoyalty.setToken("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MzYyMTc3NjAsImlhdCI6MTczMzUzOTM2MCwiaXNzIjoiSGlQbGF5IiwibmJmIjoxNzMzNTM5MzYwLCJzdWIiOiJPQjE5Wk1EUk4ifQ.YObO5ruB_CTQc-Nl50HUzWAaTZEF3zYKbs7RpBZya9k5r-TW0aJXH3M0dGdor7OCTv9KXPojraKFL0zAbeN0iOGchuhCOVd9DOpgpz6WaP1xQ41AEK7AFPsOjrdam7u0UI3-4GMLaRloKpPOfs4mOIbrT47_RE_DW8KZdw0D2UtzAnK1wwoDAW8yoOnGKh9U-_nBQc1mj1CFC4T1R-iJ-ToigWCjTznoZVQebX5d783aonfH-CWGPluBvSr8gMfLQD5M9QTo_Ol2voqoi0USTdnMQIPSWdbV9V0WETkReo6rJ31gjNZmwiWDTSA5wDlVUwi3dP3h8DHgMbhWJEb4kg")

        findViewById<Button>(R.id.btnTracking).setOnClickListener {
            lifecycleScope.launch {
                runCatching {
                    OnTracking.trackEvent("check_in", mapOf("abc" to "23"), forceCleanQueue = true)
                }
            }
        }

        findViewById<Button>(R.id.btnGetMission).setOnClickListener {
            lifecycleScope.launch {
                runCatching {
                    OneLoyalty.loyaltyService.getListMission()
                }
            }
        }

        findViewById<Button>(R.id.btnGetUser).setOnClickListener {
            lifecycleScope.launch {
                runCatching {
                    OneLoyalty.loyaltyService.getUser()
                }
            }
        }

        findViewById<Button>(R.id.btnTracking).setOnClickListener {
            lifecycleScope.launch {
                runCatching {
                    OnTracking.trackEvent("check_in", mapOf("abc" to "23"), forceCleanQueue = true)
                }
            }
        }
    }

    private fun setup() {
        lifecycleScope.launch {
            val deviceId = kotlin.runCatching {
                Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
            }.getOrNull().orEmpty()

            OneLoyalty.setup(
                applicationContext,
                Config(apiKey = "oneloyalty-dev-oekao9roNahpat6sho2zua1ieghai1eishae4ua", apiClientIdKey = "X-Client-Id", clientId = "oneloyalty-app"),
                appContext = buildAppContext(context = applicationContext, deviceId)
            )
        }
    }

    private fun buildAppContext(context: Context, deviceId: String): AppContext {
        val deviceName = "${Build.BRAND} ${Build.MODEL}".trim()
        val displayMetrics = context.resources.displayMetrics
        return AppContext(
            appInformation = context.getAppInfo(),
            device = LoyaltyDevice(
                id = deviceId,
                name = deviceName,
                timezone = TimeZone.getDefault().id,
                language = Locale.getDefault().language,
                os = "Android",
                osVersion = Build.VERSION.RELEASE,
                type = context.deviceType(),
                width = displayMetrics.widthPixels.toString(),
                height = displayMetrics.heightPixels.toString(),
                model = Build.MODEL
            )
        )
    }

    private fun Context.deviceType(): DeviceType {
        return DeviceType.TABLET.takeIf {
            resources.configuration.smallestScreenWidthDp >= 600
        } ?: DeviceType.PHONE
    }

    private fun Context.getAppInfo(): App {
        val packageInfo = try {
            packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        val versionCode = packageInfo?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }
        } ?: 1L
        return App(
            build = versionCode.toString(),
            bundleId = packageName,
            name = packageManager.getApplicationLabel(applicationInfo).toString(),
            version = packageInfo?.versionName ?: "1.0"
        )
    }
}